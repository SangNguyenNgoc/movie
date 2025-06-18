package com.example.movieofficial.api.payment.services.impl;

import com.example.movieofficial.api.bill.entities.Bill;
import com.example.movieofficial.api.bill.entities.BillStatus;
import com.example.movieofficial.api.bill.interfaces.repositories.BillRepository;
import com.example.movieofficial.api.bill.interfaces.repositories.BillStatusRepository;
import com.example.movieofficial.api.payment.dtos.PaymentInfo;
import com.example.movieofficial.api.payment.mappers.PaymentMapper;
import com.example.movieofficial.api.payment.method.PaymentFactory;
import com.example.movieofficial.api.payment.method.PaymentMethod;
import com.example.movieofficial.api.payment.method.PaymentProcess;
import com.example.movieofficial.api.payment.repositories.PaymentRepository;
import com.example.movieofficial.api.payment.services.PaymentService;
import com.example.movieofficial.api.user.entities.User;
import com.example.movieofficial.api.user.exceptions.UserNotFoundException;
import com.example.movieofficial.api.user.interfaces.UserRepository;
import com.example.movieofficial.utils.exceptions.DataNotFoundException;
import com.example.movieofficial.utils.exceptions.ForbiddenException;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import com.example.movieofficial.utils.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultPaymentService implements PaymentService {

    BillRepository billRepository;
    PaymentRepository paymentRepository;
    UserRepository userRepository;
    PaymentMapper paymentMapper;
    PaymentFactory paymentFactory;
    TokenService tokenService;

    @Override
    public List<PaymentInfo> getAll() {
        var payments = paymentRepository.findAll(Sort.by("id"));
        return payments.stream().map(paymentMapper::toInfo).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String createPaymentUrl(String token, String billId, PaymentMethod method) {
        var bill = billRepository.findByIdAndStatusId(billId, 4).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Bill not found"))
        );
        User user = getUser(token);
        if (!Objects.equals(bill.getUser().getId(), user.getId())) {
            throw new ForbiddenException("Forbidden", List.of("Access denied"));
        }
        if (LocalDateTime.now().isAfter(bill.getExpireAt())) {
            throw new InputInvalidException("Session is expired", List.of("Session is expired"));
        }
        PaymentProcess paymentProcess = paymentFactory.getPaymentStrategy(method);
        String paymentUrl = paymentProcess.execute(bill.getTotal(), billId, bill.getExpireAt());
        bill.setPaymentUrl(paymentUrl);
        return paymentUrl;
    }

    private User getUser(String token) {
        token = tokenService.validateTokenBearer(token);
        String userId = tokenService.extractSubject(token);
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Not found", List.of("User not found"))
        );
    }

    @Override
    @Transactional
    public String handleVnPay(HttpServletRequest request) {
        PaymentProcess paymentProcess = paymentFactory.getPaymentStrategy(PaymentMethod.VNPAY);
         return  paymentProcess.handelCallBack(request);
    }
}
