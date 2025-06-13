package com.example.movieofficial.api.reports.utils;

import java.time.LocalDate;
import java.util.List;

public record TimelyRange(List<String> keys, LocalDate startDate, LocalDate endDate) {}

