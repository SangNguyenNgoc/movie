package com.example.movieofficial.api.reports.services;

import com.example.movieofficial.api.reports.dtos.OverviewReport;

public interface IOverviewService {
    OverviewReport getOverviewReport(String timeFrame);
}
