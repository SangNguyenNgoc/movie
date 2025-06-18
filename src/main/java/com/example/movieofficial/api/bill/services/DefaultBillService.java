package com.example.movieofficial.api.bill.services;

import com.example.movieofficial.api.bill.dtos.AddConcessionToBill;
import com.example.movieofficial.api.bill.dtos.BillCreate;
import com.example.movieofficial.api.bill.dtos.BillDetail;
import com.example.movieofficial.api.bill.dtos.BillSession;
import com.example.movieofficial.api.bill.entities.Bill;
import com.example.movieofficial.api.bill.entities.BillStatus;
import com.example.movieofficial.api.bill.interfaces.mappers.BillMapper;
import com.example.movieofficial.api.bill.interfaces.repositories.BillRepository;
import com.example.movieofficial.api.bill.interfaces.services.BillService;
import com.example.movieofficial.api.bill.interfaces.repositories.BillStatusRepository;
import com.example.movieofficial.api.bill.usecases.BookingUseCase;
import com.example.movieofficial.api.concession.entities.ConcessionBill;
import com.example.movieofficial.api.show.entities.Show;
import com.example.movieofficial.api.show.interfaces.ShowMapper;
import com.example.movieofficial.api.user.entities.User;
import com.example.movieofficial.api.user.exceptions.UserNotFoundException;
import com.example.movieofficial.api.user.interfaces.UserRepository;
import com.example.movieofficial.utils.exceptions.DataNotFoundException;
import com.example.movieofficial.utils.exceptions.ForbiddenException;
import com.example.movieofficial.utils.services.TokenService;
import com.example.movieofficial.api.payment.method.impl.vnpay.VnPayPaymentProcess;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultBillService implements BillService {

    UserRepository userRepository;
    BillRepository billRepository;
    TokenService tokenService;
    BillMapper billMapper;
    ShowMapper showMapper;

    BookingUseCase bookingUseCase;


    @Override
    public BillSession createSession(BillCreate billCreate, String token) {
        return bookingUseCase.createSession(billCreate, token);
    }

    @Override
    public String addConcessionToBill(String billId, AddConcessionToBill addConcessionToBill, String token) {
        return bookingUseCase.addConcessionsToBill(billId, addConcessionToBill, token);
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
                    billDetail.setConcessions(getConcessionIdBill(bill.getConcessionBills()));
                    return billDetail;
                })
                .collect(Collectors.toList());
    }

    private Set<BillDetail.ConcessionDto> getConcessionIdBill(Set<ConcessionBill> concessionBillSet) {
        return concessionBillSet.stream()
                .map(cb -> BillDetail.ConcessionDto.builder()
                        .name(cb.getConcession().getName())
                        .amount(cb.getAmount())
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    public BillDetail getBillDetail(String id, String token) {
        User user = getUser(token);
        var bill = billRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Bill not found"))
        );
        if (!Objects.equals(bill.getUser().getId(), user.getId())) {
            throw new ForbiddenException("Forbidden", List.of("Access denied"));
        }
        BillDetail billDetail = billMapper.toDetail(bill);
        Show show = bill.getTickets().iterator().next().getShow();
        BillDetail.TicketDto.ShowDto showDto = showMapper.toShowInBillDetail(show);
        billDetail.setShow(showDto);
        billDetail.setConcessions(getConcessionIdBill(bill.getConcessionBills()));
        return billDetail;
    }

    @Override
    @Scheduled(cron = "0 */5 * * * *")
    @PostConstruct
    public void deleteBillTask() {
        billRepository.deleteByExpireAtAndStatusId(LocalDateTime.now(), 4);
    }

    @Override
    public void deleteSession(String token, String billId) {
        User user = getUser(token);
        var bill = billRepository.findByIdAndStatusId(billId, 4).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Bill not found"))
        );
        if (!Objects.equals(bill.getUser().getId(), user.getId())) {
            throw new ForbiddenException("Forbidden", List.of("Access denied"));
        }
        billRepository.deleteByIdAndStatusId(billId, 4);
    }
}
