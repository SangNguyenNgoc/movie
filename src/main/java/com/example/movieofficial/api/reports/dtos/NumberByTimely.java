package com.example.movieofficial.api.reports.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NumberByTimely {
    private String timely;
    private Long amount;
}
