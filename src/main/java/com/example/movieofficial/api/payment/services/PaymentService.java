package com.example.movieofficial.api.payment.services;

import com.example.movieofficial.api.payment.dtos.PaymentInfo;
import com.example.movieofficial.api.payment.method.PaymentMethod;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface PaymentService {
    List<PaymentInfo> getAll();
    String createPaymentUrl(String token, String billId, PaymentMethod method);
    String handleVnPay(HttpServletRequest req);

}
