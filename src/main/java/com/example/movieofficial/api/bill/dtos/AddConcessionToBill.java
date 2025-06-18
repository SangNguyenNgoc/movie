package com.example.movieofficial.api.bill.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddConcessionToBill {
    private List<ConcessionOfBill> concessionOfBills;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConcessionOfBill {
        private String concessionId;
        private Long amount;
        private Long price;
    }
}
