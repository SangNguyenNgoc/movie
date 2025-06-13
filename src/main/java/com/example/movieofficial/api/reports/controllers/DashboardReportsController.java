package com.example.movieofficial.api.reports.controllers;

import com.example.movieofficial.api.reports.dtos.NumberByTimely;
import com.example.movieofficial.api.reports.dtos.OccupancyByDate;
import com.example.movieofficial.api.reports.dtos.OverviewReport;
import com.example.movieofficial.api.reports.dtos.ProfitByTimely;
import com.example.movieofficial.api.reports.services.IOverviewService;
import com.example.movieofficial.api.reports.services.IStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardReportsController {
    private final IOverviewService overviewService;
    private final IStatisticService statisticService;

    @GetMapping("/profit")
    public ResponseEntity<List<ProfitByTimely>> getProfitByTimely(
            @RequestParam(value = "timely") String timely,
            @RequestParam(value = "year", required = false) Integer year
    ) {
        return ResponseEntity.ok(statisticService.getProfitByTimely(timely, year));
    }


    @GetMapping("/shows")
    public ResponseEntity<List<NumberByTimely>> getNumberOfShowsByDate(
            @RequestParam(value = "timely") String timely,
            @RequestParam(value = "year", required = false) Integer year
    ) {
        return ResponseEntity.ok(statisticService.getTotalShowsByTimely(timely, year));
    }


    @GetMapping("/overview")
    public ResponseEntity<OverviewReport> getOverviewByDate(
            @RequestParam("time") String time
    ) {
        return ResponseEntity.ok(overviewService.getOverviewReport(time));
    }

    @GetMapping("/sold-tickets")
    public ResponseEntity<List<OccupancyByDate>> getSoldTicketsByDate(
            @RequestParam("time") String time
    ) {
        return ResponseEntity.ok(statisticService.getOccupancyByDate(time));
    }
}

