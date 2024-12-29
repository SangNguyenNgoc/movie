package com.example.movieofficial.api.bill.services;

import com.example.movieofficial.api.bill.dtos.BillCreate;
import com.example.movieofficial.api.bill.dtos.BillDetail;
import com.example.movieofficial.api.bill.entities.Bill;
import com.example.movieofficial.api.bill.entities.BillStatus;
import com.example.movieofficial.api.bill.interfaces.mappers.BillMapper;
import com.example.movieofficial.api.bill.interfaces.repositories.BillRepository;
import com.example.movieofficial.api.bill.interfaces.services.BillService;
import com.example.movieofficial.api.bill.interfaces.repositories.BillStatusRepository;
import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.hall.entities.Seat;
import com.example.movieofficial.api.hall.interfaces.SeatRepository;
import com.example.movieofficial.api.show.entities.Show;
import com.example.movieofficial.api.show.interfaces.ShowMapper;
import com.example.movieofficial.api.show.interfaces.ShowRepository;
import com.example.movieofficial.api.ticket.entities.Ticket;
import com.example.movieofficial.api.ticket.interfaces.TicketRepository;
import com.example.movieofficial.api.user.entities.User;
import com.example.movieofficial.api.user.exceptions.UserNotFoundException;
import com.example.movieofficial.api.user.interfaces.UserRepository;
import com.example.movieofficial.utils.exceptions.DataNotFoundException;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import com.example.movieofficial.utils.services.TokenService;
import com.example.movieofficial.utils.services.VnPayService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultBillService implements BillService {

    ShowRepository showRepository;
    SeatRepository seatRepository;
    UserRepository userRepository;
    BillStatusRepository billStatusRepository;
    BillRepository billRepository;
    TokenService tokenService;
    VnPayService vnPayService;
    TicketRepository ticketRepository;
    BillMapper billMapper;
    ShowMapper showMapper;


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String create(BillCreate billCreate, String token) {
        User user = getUser(token);

        Show show = showRepository.findWithDetailsByIdAndDateTime(
                billCreate.getShowId(),
                LocalDate.now(),
                LocalTime.now()
        ).orElseThrow(() -> new DataNotFoundException("Data not found", List.of("Show not found")));

        checkSeatsInHall(billCreate.getSeatIds(), show.getHall());

        List<Ticket> ticketsInShow = ticketRepository.findByShowIdOrderBySeatRowNameAscSeatRowIndexAsc(
                show.getId(),
                LocalDateTime.now().minusMinutes(2)
        );
        checkSeatsAreReserved(billCreate.getSeatIds(), ticketsInShow);

        List<Seat> seats = seatRepository.findAllById(billCreate.getSeatIds());
        long totalPrice = seats.stream().mapToLong(seat -> seat.getType().getPrice()).sum();

        BillStatus billStatus = billStatusRepository.findById(1).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Status not found")));

        String billId = tokenService.getRandomNumber(12);
        String paymentUrl = vnPayService.doPost(totalPrice, billId);
        var newBill = Bill.builder()
                .id(billId)
                .total(totalPrice)
                .user(user)
                .status(billStatus)
                .expireAt(LocalDateTime.now().plusMinutes(10))
                .paymentUrl(paymentUrl)
                .build();
        Set<Ticket> tickets = createTicket(show, seats, newBill);
        newBill.setTickets(tickets);
        billRepository.save(newBill);
        return paymentUrl;
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

    @Override
    public User getUser(String token) {
        token = tokenService.validateTokenBearer(token);
        String userId = tokenService.extractSubject(token);
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Not found", List.of("User not found"))
        );
    }

    @Override
    @Transactional
    public String payment(String id, String responseCode, String transactionStatus, String paymentAt) {
        Bill bill = billRepository.findByIdAndStatusId(id, 1).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Bill not found"))
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime dateTime = LocalDateTime.parse(paymentAt, formatter);
        if (responseCode.equals("00") && transactionStatus.equals("00")) {
            BillStatus billStatus = billStatusRepository.findById(2).orElseThrow(
                    () -> new DataNotFoundException("Data not found", List.of("Status not found")));
            if (bill.getFailure() != null) bill.setFailure(null);
            if (bill.getFailureReason() != null) bill.setFailureReason(null);
            bill.setStatus(billStatus);
            bill.setPaymentAt(dateTime);
        } else {
            String message = vnPayService.getMessage(responseCode, transactionStatus);
            bill.setFailureReason(message);
            bill.setFailureAt(dateTime);
            bill.setFailure(true);
        }
        return vnPayService.getRedirectBill() + bill.getId();
    }



    @Override
    public List<BillDetail> getBillByUser(String token, Integer page, Integer size, String status) {
        User user = getUser(token);
        Pageable pageable = PageRequest.of(page, size);
        List<Bill> bills = billRepository.findByUserIdOrderByCreateDateDesc(user.getId(), status, pageable);
        return bills.stream()
                .map(bill -> {
                    BillDetail billDetail = billMapper.toDetail(bill);
                    Show show = bill.getTickets().iterator().next().getShow();
                    BillDetail.TicketDto.ShowDto showDto = showMapper.toShowInBillDetail(show);
                    billDetail.setShow(showDto);
                    return billDetail;
                })
                .collect(Collectors.toList());
    }

    @Override
    public BillDetail getBillDetail(String id, String token) {
        User user = getUser(token);
        Bill bill = billRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Bill not found"))
        );
        BillDetail billDetail = billMapper.toDetail(bill);
        Show show = bill.getTickets().iterator().next().getShow();
        BillDetail.TicketDto.ShowDto showDto = showMapper.toShowInBillDetail(show);
        billDetail.setShow(showDto);
        return billDetail;
    }

    @Override
    @Scheduled(cron = "0 */5 * * * *")
    public void deleteBillTask() {
        billRepository.deleteByExpireAtAndStatusId(LocalDateTime.now(), 1);
    }
}
