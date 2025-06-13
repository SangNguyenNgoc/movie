package com.example.movieofficial.api.reports.repositories;

import com.example.movieofficial.api.reports.dtos.OccupancyByShow;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IOverviewRepository {
    Optional<BigDecimal> getTotalProfit(LocalDate startDate, LocalDate endDate);
    Optional<Long> getTotalTickets(LocalDate startDate, LocalDate endDate);
    Optional<Long> getTotalShows(LocalDate startDate, LocalDate endDate);
    List<OccupancyByShow> getFillRate(LocalDate startDate, LocalDate endDate);
}
