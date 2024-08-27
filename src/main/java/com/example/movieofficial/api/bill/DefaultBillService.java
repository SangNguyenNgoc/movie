package com.example.movieofficial.api.bill;

import com.example.movieofficial.api.bill.dtos.BillCreate;
import com.example.movieofficial.api.bill.dtos.BillDetail;
import com.example.movieofficial.api.bill.entities.Bill;
import com.example.movieofficial.api.bill.entities.BillStatus;
import com.example.movieofficial.api.bill.interfaces.BillMapper;
import com.example.movieofficial.api.bill.interfaces.BillRepository;
import com.example.movieofficial.api.bill.interfaces.BillService;
import com.example.movieofficial.api.bill.interfaces.BillStatusRepository;
import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.hall.entities.Seat;
import com.example.movieofficial.api.hall.interfaces.HallMapper;
import com.example.movieofficial.api.hall.interfaces.SeatRepository;
import com.example.movieofficial.api.show.entities.Show;
import com.example.movieofficial.api.show.interfaces.ShowMapper;
import com.example.movieofficial.api.show.interfaces.ShowRepository;
import com.example.movieofficial.api.ticket.entities.Ticket;
import com.example.movieofficial.api.ticket.interfaces.TicketRepository;
import com.example.movieofficial.api.user.DefaultUserService;
import com.example.movieofficial.api.user.entities.User;
import com.example.movieofficial.api.user.exceptions.UserNotFoundException;
import com.example.movieofficial.api.user.interfaces.UserRepository;
import com.example.movieofficial.utils.exceptions.DataNotFoundException;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import com.example.movieofficial.utils.services.TokenService;
import com.example.movieofficial.utils.services.VnPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultBillService implements BillService {

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final BillStatusRepository billStatusRepository;
    private final BillRepository billRepository;
    private final TokenService tokenService;
    private final VnPayService vnPayService;
    private final TicketRepository ticketRepository;
    private final BillMapper billMapper;
    private final ShowMapper showMapper;


    @Override
    public String create(BillCreate billCreate, String token) {
        User user = getUser(token);

        Show show = showRepository.findWithDetailsByIdAndDateTime(
                billCreate.getShowId(),
                LocalDate.now(),
                LocalTime.now()
        ).orElseThrow(() -> new DataNotFoundException("Data not found", List.of("Show not found")));

        checkSeatsInHall(billCreate.getSeatIds(), show.getHall());

        checkSeatsAreReserved(billCreate.getSeatIds(), show);

        List<Seat> seats = seatRepository.findAllById(billCreate.getSeatIds());
        long totalPrice = seats.stream().mapToLong(seat -> seat.getType().getPrice()).sum();

        BillStatus billStatus = billStatusRepository.findById(1).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Status not found")));

        String billId = tokenService.getRandomNumber(12);
        String paymentUrl = vnPayService.doPost(totalPrice, billId);
        Bill newBill = Bill.builder()
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
    public void checkSeatsAreReserved(List<Long> seatIds, Show show) {
        List<Ticket> ticketsByShow = ticketRepository.findByShowIdOrderBySeatRowNameAscSeatRowIndexAsc(
                show.getId(),
                LocalDateTime.now().minusMinutes(2)
        );
        Set<Long> seatIdsAreReserved = ticketsByShow.stream()
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
            if (bill.getFailure()) bill.setFailure(null);
            if (bill.getFailureReason() != null) bill.setFailureReason(null);
            bill.setStatus(billStatus);
            bill.setPaymentAt(dateTime);
            return "Success";
        } else {
            String message = getMessage(responseCode, transactionStatus);
            bill.setFailureReason(message);
            bill.setFailureAt(dateTime);
            bill.setFailure(true);
            return message;
        }
    }

    public String getMessage(String responseCode, String transactionStatus) {
        Map<String, String> responseCodeMessages = getStringStringMap();
        if (responseCodeMessages.containsKey(responseCode)) {
            return responseCodeMessages.get(transactionStatus);
        }
        if (transactionStatus.equals("01")) {
            return "Chưa thanh toán";
        } else {
            return "Transaction Status invalid";
        }
    }

    private Map<String, String> getStringStringMap() {
        var responseCodeMessages = new HashMap<String, String>();
        responseCodeMessages.put("09", "Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng");
        responseCodeMessages.put("10", "Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần");
        responseCodeMessages.put("11", "Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.");
        responseCodeMessages.put("12", "Thẻ/Tài khoản của khách hàng bị khóa.");
        responseCodeMessages.put("24", "Khách hàng hủy giao dịch.");
        responseCodeMessages.put("51", "Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.");
        responseCodeMessages.put("65", "Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.");
        responseCodeMessages.put("75", "Ngân hàng thanh toán đang bảo trì.");
        responseCodeMessages.put("79", "KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch.");
        responseCodeMessages.put("99", "Lỗi không xác định.");
        return responseCodeMessages;
    }

    @Override
    public List<BillDetail> getBillByUser(String token, Integer page, Integer size) {
        User user = getUser(token);
        Pageable pageable = PageRequest.of(page, size);
        List<Bill> bills = billRepository.findByUserIdOrderByCreateDateDesc(user.getId(), pageable);
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
}
