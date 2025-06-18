package com.example.movieofficial.api.payment.method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentFactory {

    private final Map<PaymentMethod, PaymentProcess> strategyMap;

    @Autowired
    public PaymentFactory(List<PaymentProcess> payments) {
        this.strategyMap = payments.stream()
                .collect(Collectors.toMap(PaymentProcess::getSupportedMethod, Function.identity()));
    }

    public PaymentProcess getPaymentStrategy(PaymentMethod method) {
        PaymentProcess paymentService = strategyMap.get(method);
        if (paymentService == null) {
            throw new IllegalArgumentException("Không hỗ trợ phương thức thanh toán: " + method);
        }
        return paymentService;
    }
}
