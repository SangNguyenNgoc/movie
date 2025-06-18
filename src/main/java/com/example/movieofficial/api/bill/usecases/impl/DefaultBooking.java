package com.example.movieofficial.api.bill.usecases.impl;

import com.example.movieofficial.api.bill.dtos.AddConcessionToBill;
import com.example.movieofficial.api.bill.dtos.BillCreate;
import com.example.movieofficial.api.bill.dtos.BillSession;
import com.example.movieofficial.api.bill.entities.Bill;
import com.example.movieofficial.api.bill.entities.BillStatus;
import com.example.movieofficial.api.bill.interfaces.repositories.BillRepository;
import com.example.movieofficial.api.bill.interfaces.repositories.BillStatusRepository;
import com.example.movieofficial.api.bill.usecases.BookingUseCase;
import com.example.movieofficial.api.concession.entities.Concession;
import com.example.movieofficial.api.concession.entities.ConcessionBill;
import com.example.movieofficial.api.concession.repositories.ConcessionBillRepository;
import com.example.movieofficial.api.concession.repositories.ConcessionRepository;
import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.hall.entities.Seat;
import com.example.movieofficial.api.hall.repositories.SeatRepository;
import com.example.movieofficial.api.show.entities.Show;
import com.example.movieofficial.api.show.interfaces.ShowRepository;
import com.example.movieofficial.api.ticket.entities.Ticket;
import com.example.movieofficial.api.ticket.interfaces.TicketRepository;
import com.example.movieofficial.api.user.entities.User;
import com.example.movieofficial.api.user.exceptions.UserNotFoundException;
import com.example.movieofficial.api.user.interfaces.UserRepository;
import com.example.movieofficial.utils.exceptions.DataNotFoundException;
import com.example.movieofficial.utils.exceptions.ForbiddenException;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import com.example.movieofficial.utils.services.TokenService;
import com.example.movieofficial.api.payment.method.impl.vnpay.VnPayPaymentProcess;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultBooking implements BookingUseCase {

    TokenService tokenService;

    ShowRepository showRepository;
    UserRepository userRepository;
    TicketRepository ticketRepository;
    SeatRepository seatRepository;
    BillStatusRepository billStatusRepository;
    BillRepository billRepository;
    ConcessionRepository concessionRepository;
    ConcessionBillRepository concessionBillRepository;

    RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public BillSession createSession(BillCreate billCreate, String token) {
        User user = getUser(token);
        List<String> lockedKeys = new ArrayList<>();
        try {
            lockedKeys = lockSeat(billCreate, user);
            Show show = showRepository.findWithDetailsByIdAndDateTime(
                    billCreate.getShowId(),
                    LocalDate.now(),
                    LocalTime.now()
            ).orElseThrow(() -> new DataNotFoundException("Data not found", List.of("Show not found")));
            List<Ticket> ticketsInShow = ticketRepository.findByShowIdOrderBySeatRowNameAscSeatRowIndexAsc(
                    show.getId(),
                    LocalDateTime.now().minusMinutes(2)
            );
            checkSeatsAreReserved(billCreate.getSeatIds(), ticketsInShow);
            checkSeatsInHall(billCreate.getSeatIds(), show.getHall());

            List<Seat> seats = seatRepository.findAllById(billCreate.getSeatIds());
            long totalPrice = calculateTotalPrice(seats);

            BillStatus billStatus = billStatusRepository.findById(4).orElseThrow(
                    () -> new DataNotFoundException("Data not found", List.of("Status not found")));

            String billId = tokenService.getRandomNumber(12);
//            String paymentUrl = vnPayService.doPost(totalPrice, billId);
            LocalDateTime expireTime = LocalDateTime.now().plusMinutes(10);
            var newBill = Bill.builder()
                    .id(billId)
                    .total(totalPrice)
                    .user(user)
                    .status(billStatus)
                    .expireAt(expireTime)
                    .build();
            Set<Ticket> tickets = createTicket(show, seats, newBill);
            newBill.setTickets(tickets);
            billRepository.save(newBill);
            return BillSession.builder()
                    .billId(billId)
                    .expireTime(expireTime.toString())
                    .build();
        } finally {
            if (!lockedKeys.isEmpty()) {
                redisTemplate.delete(lockedKeys);
            }
        }
    }


    @Override
    public void checkSeatsInHall(List<Long> seatIds, Hall hall) {
        Set<Long> seatIdsInHall = hall.getSeats().stream()
                .map(Seat::getId)
                .collect(Collectors.toSet());
        for (Long seatId : seatIds) {
            if (!seatIdsInHall.contains(seatId)) {
                throw new DataNotFoundException("Data not found", List.of("Seats are not found"));
            }
        }
    }

    @Override
    public void checkSeatsAreReserved(List<Long> seatIds, List<Ticket> ticketsInShow) {
        Set<Long> seatIdsAreReserved = ticketsInShow.stream()
                .map(ticket -> ticket.getSeat().getId())
                .collect(Collectors.toSet());
        for (Long seatId : seatIds) {
            if (seatIdsAreReserved.contains(seatId)) {
                throw new InputInvalidException("Input invalid", List.of("Seats are reserved"));
            }
        }
    }

    @Override
    public Set<Ticket> createTicket(Show show, List<Seat> seats, Bill bill) {
        return seats.stream()
                .map(seat -> Ticket.builder()
                        .id(tokenService.getRandomNumber(15))
                        .bill(bill)
                        .seat(seat)
                        .show(show)
                        .stillValid(true)
                        .build())
                .collect(Collectors.toSet());
    }

    private User getUser(String token) {
        token = tokenService.validateTokenBearer(token);
        String userId = tokenService.extractSubject(token);
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Not found", List.of("User not found"))
        );
    }

    private long calculateTotalPrice(List<Seat> seats) {
        return seats.stream()
                .mapToLong(seat -> seat.getType().getPrice())
                .sum();
    }

    private List<String> lockSeat(BillCreate billCreate, User user) {
        List<String> lockedKeys = new ArrayList<>();
        for (var seatId : billCreate.getSeatIds()) {
            String lockKey = "lock:seat:" + seatId + ":" + billCreate.getShowId();
            boolean isLocked = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, user.getId(), 10, TimeUnit.MINUTES));
            if (!isLocked) {
                throw new InputInvalidException("Input invalid", List.of("Seats are reserved"));
            } else {
                System.out.println("Seats are locked");
                lockedKeys.add(lockKey);
            }
        }
        return lockedKeys;
    }


    @Override
    @Transactional
    public String addConcessionsToBill(String billId, AddConcessionToBill request, String token) {
        var bill = billRepository.findByIdAndStatusId(billId, 4).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Bill not found"))
        );
        User user = getUser(token);

        if (!Objects.equals(bill.getUser().getId(), user.getId())) {
            throw new ForbiddenException("Forbidden", List.of("Access denied"));
        }

        if (!bill.getConcessionBills().isEmpty()) {
            long price = bill.getConcessionBills().stream().mapToLong(cb -> cb.getConcession().getPrice()).sum();
            bill.setConcessionBills(null);
            concessionBillRepository.deleteByBill(bill);
            bill.setTotal(bill.getTotal() - price);
        }

        Map<String, Long> concessionIdToAmount = request.getConcessionOfBills().stream()
                .collect(Collectors.toMap(AddConcessionToBill.ConcessionOfBill::getConcessionId, AddConcessionToBill.ConcessionOfBill::getAmount));

        var concessionIds = new ArrayList<>(concessionIdToAmount.keySet());
        var concessions = concessionRepository.findAllById(concessionIds);

        Set<ConcessionBill> concessionBills = createConcessionBills(concessions, concessionIdToAmount, bill);

        long totalConcessionPrice = getTotalConcession(concessionBills);

        bill.setConcessionBills(concessionBills);
        bill.setTotal(bill.getTotal() + totalConcessionPrice);
        return "success";
    }


    private Set<ConcessionBill> createConcessionBills(List<Concession> concessions,
                                                      Map<String, Long> concessionIdToAmount,
                                                      Bill bill) {
        return concessions.stream()
                .map(c -> ConcessionBill.builder()
                        .bill(bill)
                        .concession(c)
                        .amount(concessionIdToAmount.get(c.getId()))
                        .build())
                .collect(Collectors.toSet());
    }


    private Long getTotalConcession(Set<ConcessionBill> concessionBills) {
        return concessionBills.stream().mapToLong(item -> item.getAmount() * item.getConcession().getPrice()).sum();
    }
}
