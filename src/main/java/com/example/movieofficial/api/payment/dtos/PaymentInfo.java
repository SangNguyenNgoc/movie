package com.example.movieofficial.api.payment.dtos;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfo {
    private Long id;
    private String name;
    private String expand;
    private String image;
    private String tag;
    private Boolean enabled;
}
