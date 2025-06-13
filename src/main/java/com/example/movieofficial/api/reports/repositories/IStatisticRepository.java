package com.example.movieofficial.api.reports.repositories;

import com.example.movieofficial.api.reports.dtos.OccupancyByDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IStatisticRepository {
    Map<String, BigDecimal> getProfitByTimely(String timely, LocalDate startDate, LocalDate endDate);
    Map<String, Long> getTotalShowsByTimely(String timely, LocalDate startDate, LocalDate endDate);
    List<OccupancyByDate> getOccupancyByDate(LocalDate startDate, LocalDate endDate);
}
