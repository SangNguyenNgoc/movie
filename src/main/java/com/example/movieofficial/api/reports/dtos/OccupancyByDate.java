package com.example.movieofficial.api.reports.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupancyByDate {
    private LocalDate startDate;
    private String label;
    private Integer allSeats;
    private Integer soldSeats;
}
