package com.example.movieofficial.api.reports.services;

import com.example.movieofficial.api.reports.dtos.NumberByTimely;
import com.example.movieofficial.api.reports.dtos.OccupancyByDate;
import com.example.movieofficial.api.reports.dtos.ProfitByTimely;

import java.util.List;

public interface IStatisticService {
    List<ProfitByTimely> getProfitByTimely(String timely, Integer year);
    List<NumberByTimely> getTotalShowsByTimely(String timely, Integer year);
    List<OccupancyByDate> getOccupancyByDate(String timely);
}
