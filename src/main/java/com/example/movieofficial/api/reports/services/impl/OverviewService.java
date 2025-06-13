package com.example.movieofficial.api.reports.services.impl;

import com.example.movieofficial.api.reports.dtos.OverviewReport;
import com.example.movieofficial.api.reports.repositories.IOverviewRepository;
import com.example.movieofficial.api.reports.services.IOverviewService;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OverviewService implements IOverviewService {
    IOverviewRepository overviewRepository;

    @Override
    public OverviewReport getOverviewReport(String timeFrame) {
        var time = getTimeFrames(timeFrame);
        return OverviewReport.builder()
                .profit(getTotalProfit(time))
                .tickets(getTotalTickets(time))
                .shows(getTotalShows(time))
                .fillRate(getFillRate(time))
                .build();
    }


    private OverviewReport.OverviewValue<BigDecimal> getTotalProfit(Map<String, LocalDate> time) {
        BigDecimal profit = overviewRepository.getTotalProfit(time.get("currentStart"), time.get("currentNow")).orElse(BigDecimal.ZERO);
        BigDecimal previousProfit = overviewRepository.getTotalProfit(time.get("previousStart"), time.get("previousEnd")).orElse(BigDecimal.ZERO);
        return OverviewReport.OverviewValue.<BigDecimal>builder()
                .value(profit)
                .growthRate(calculateGrowthRate(previousProfit, profit))
                .build();
    }


    private OverviewReport.OverviewValue<Long> getTotalTickets(Map<String, LocalDate> time) {
        var numberTickets = overviewRepository.getTotalTickets(time.get("currentStart"), time.get("currentNow")).orElse(0L);
        var previousNumberTickets = overviewRepository.getTotalTickets(time.get("previousStart"), time.get("previousEnd")).orElse(0L);
        return OverviewReport.OverviewValue.<Long>builder()
                .value(numberTickets)
                .growthRate(calculateGrowthRate(previousNumberTickets, numberTickets))
                .build();
    }


    private OverviewReport.OverviewValue<Long> getTotalShows(Map<String, LocalDate> time) {
        var numberShows = overviewRepository.getTotalShows(time.get("currentStart"), time.get("currentNow")).orElse(0L);
        var previousNumberShows = overviewRepository.getTotalShows(time.get("previousStart"), time.get("previousEnd")).orElse(0L);
        return OverviewReport.OverviewValue.<Long>builder()
                .value(numberShows)
                .growthRate(calculateGrowthRate(previousNumberShows, numberShows))
                .build();
    }


    private OverviewReport.OverviewValue<Double> getFillRate(Map<String, LocalDate> time) {
        Double fillRate = getOccupancyRateInShowsByDate(time.get("currentStart"), time.get("currentNow"));
        Double previousFillRate = getOccupancyRateInShowsByDate(time.get("previousStart"), time.get("previousEnd"));
        return OverviewReport.OverviewValue.<Double>builder()
                .value(fillRate)
                .growthRate(calculateGrowthRate(previousFillRate, fillRate))
                .build();
    }


    private Double getOccupancyRateInShowsByDate(LocalDate start, LocalDate end) {
        var result = overviewRepository.getFillRate(start, end);
        return result.stream()
                .mapToDouble(item -> {
                    double rate = (double) item.getSoldSeats() / item.getAvailableSeats() * 100;
                    return Math.round(rate * 100.0) / 100.0; // làm tròn đến 2 chữ số
                })
                .average()
                .orElse(0.0);
    }


    private Map<String, LocalDate> getTimeFrames(String timeFrame) {
        Map<String, LocalDate> result = new HashMap<>();
        // In a real application, you'd use LocalDate.now()
        // but sticking to your example's fixed date for consistency in testing.
        LocalDate today = LocalDate.now(); // Example: Thursday, Jan 2, 2025

        LocalDate previousStart;
        LocalDate previousEnd;
        LocalDate currentStart;
        LocalDate currentNowReference; // New variable to hold the 'currentNow' value

        switch (timeFrame.toLowerCase()) {
            case "current-week" -> {
                DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
                currentStart = today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek)); // Start of the current week (Mon)

                // For 'current-week', 'previous' refers to the week immediately before the current one
                previousEnd = currentStart.minusDays(1); // End of the previous week (Sun)
                previousStart = previousEnd.with(TemporalAdjusters.previousOrSame(firstDayOfWeek)); // Start of the previous week (Mon)

                currentNowReference = today; // 'currentNow' is literally today's date
            }

            case "last-week" -> {
                DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
                // 'currentStart' for 'last-week' means the start of the LAST week
                LocalDate currentWeekStart = today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
                currentStart = currentWeekStart.minusWeeks(1); // Start of the last week (Mon of last week)

                // 'previous' for 'last-week' refers to the week before the 'last week' (i.e., 2 weeks ago)
                previousEnd = currentStart.minusDays(1); // End of the week before last week (Sun of 2 weeks ago)
                previousStart = previousEnd.with(TemporalAdjusters.previousOrSame(firstDayOfWeek)); // Start of the week before last week (Mon of 2 weeks ago)

                // AS PER YOUR LATEST REQUIREMENT:
                // 'currentNow' for 'last-week' should be the end of the last week
                currentNowReference = currentStart.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            }

            case "current-year" -> {
                currentStart = today.with(TemporalAdjusters.firstDayOfYear()); // Start of the current year (Jan 1)

                // For 'current-year', 'previous' refers to the year immediately before the current one
                previousEnd = currentStart.minusDays(1); // End of the previous year (Dec 31 of last year)
                previousStart = previousEnd.with(TemporalAdjusters.firstDayOfYear()); // Start of the previous year (Jan 1 of last year)

                currentNowReference = today; // 'currentNow' is literally today's date
            }

            case "last-year" -> {
                // 'currentStart' for 'last-year' means the start of the LAST year
                LocalDate currentYearStart = today.with(TemporalAdjusters.firstDayOfYear());
                currentStart = currentYearStart.minusYears(1); // Start of the last year (Jan 1 of last year)

                // 'previous' for 'last-year' refers to the year before the 'last year' (i.e., 2 years ago)
                previousEnd = currentStart.minusDays(1); // End of the year before last year (Dec 31 of 2 years ago)
                previousStart = previousEnd.with(TemporalAdjusters.firstDayOfYear()); // Start of the year before last year (Jan 1 of 2 years ago)

                // If 'currentNow' for 'last-year' should be the end of the last year
                currentNowReference = currentStart.with(TemporalAdjusters.lastDayOfYear());
            }

            default -> throw new InputInvalidException("Invalid timeFrame: " + timeFrame, List.of());
        }

        result.put("previousStart", previousStart);
        result.put("previousEnd", previousEnd);
        result.put("currentStart", currentStart);
        result.put("currentNow", currentNowReference); // Use the calculated reference date

        return result;
    }

    private BigDecimal calculateGrowthRate(Number previous, Number now) {
        BigDecimal prev = toBigDecimal(previous);
        BigDecimal current = toBigDecimal(now);

        if (prev.compareTo(BigDecimal.ZERO) == 0) {
            if (current.compareTo(BigDecimal.ZERO) > 0) return BigDecimal.valueOf(1.00);   // +100%
            if (current.compareTo(BigDecimal.ZERO) < 0) return BigDecimal.valueOf(-1.00);  // -100%
            return BigDecimal.ZERO; // 0%
        }

        BigDecimal change = current.subtract(prev);
        BigDecimal rate = change.divide(prev.abs(), 6, RoundingMode.HALF_UP);
        return rate.setScale(2, RoundingMode.HALF_UP); // round to 2 decimal places
    }

    private BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal bd) return bd;
        if (number instanceof Double d) return BigDecimal.valueOf(d);
        if (number instanceof Long l) return BigDecimal.valueOf(l);
        throw new IllegalArgumentException("Unsupported type: " + number.getClass());
    }
}
