package com.example.movieofficial.api.payment.method;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;

public interface PaymentProcess {
    String execute(long cost, String id, LocalDateTime expireTime);
    PaymentMethod getSupportedMethod();
    String handelCallBack(HttpServletRequest request);
}
