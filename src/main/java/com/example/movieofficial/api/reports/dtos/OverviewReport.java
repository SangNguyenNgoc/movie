package com.example.movieofficial.api.reports.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverviewReport implements Serializable {

    private OverviewValue<BigDecimal> profit;
    private OverviewValue<Long> tickets;
    private OverviewValue<Long> shows;
    private OverviewValue<Double> fillRate;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverviewValue<T> implements Serializable {
        private T value;
        private BigDecimal growthRate;
    }
}
