package com.example.movieofficial.api.payment.mappers;

import com.example.movieofficial.api.payment.dtos.PaymentInfo;
import com.example.movieofficial.api.payment.entities.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentInfo toInfo(Payment payment);
}
