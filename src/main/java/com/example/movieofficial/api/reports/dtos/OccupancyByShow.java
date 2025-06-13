package com.example.movieofficial.api.reports.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupancyByShow {
    private String id;
    private LocalDate startDate;
    private LocalTime startTime;
    private Integer availableSeats;
    private Integer soldSeats;
}
