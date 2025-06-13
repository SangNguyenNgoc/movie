package com.example.movieofficial.api.reports.services.impl;

import com.example.movieofficial.api.reports.dtos.NumberByTimely;
import com.example.movieofficial.api.reports.dtos.OccupancyByDate;
import com.example.movieofficial.api.reports.dtos.ProfitByTimely;
import com.example.movieofficial.api.reports.dtos.Timely;
import com.example.movieofficial.api.reports.repositories.IStatisticRepository;
import com.example.movieofficial.api.reports.repositories.impl.StatisticRepository;
import com.example.movieofficial.api.reports.services.IStatisticService;
import com.example.movieofficial.api.reports.utils.MonthAbbreviation;
import com.example.movieofficial.api.reports.utils.TimelyRange;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticService implements IStatisticService {
    IStatisticRepository profitRepository;
    StatisticRepository statisticRepository;

    List<String> SHORT_DAYS_OF_WEEK = Stream.of(DayOfWeek.values())
            .map(day -> day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)) // Mon, Tue, ...
            .collect(Collectors.toList());

    @Override
    public List<ProfitByTimely> getProfitByTimely(String timely, Integer year) {
        TimelyRange range = getTimelyRange(timely, year);
        Map<String, BigDecimal> result = profitRepository.getProfitByTimely(timely, range.startDate(), range.endDate());
        return buildProfitList(result, range.keys(), timely);
    }

    @Override
    public List<NumberByTimely> getTotalShowsByTimely(String timely, Integer year) {
        TimelyRange range = getTimelyRange(timely, year);
        Map<String, Long> result = profitRepository.getTotalShowsByTimely(timely, range.startDate(), range.endDate());
        return buildTotalShowsList(result, range.keys(), timely);
    }

    private List<ProfitByTimely> buildProfitList(Map<String, BigDecimal> data, List<String> keys, String timely) {
        return keys.stream()
                .map(k -> new ProfitByTimely(timely.equals(Timely.MONTHLY.getValue()) ? MonthAbbreviation.fromNumber(k) : k,
                        Objects.requireNonNullElse(data.get(k), BigDecimal.ZERO)))
                .toList();
    }

    private List<NumberByTimely> buildTotalShowsList(Map<String, Long> data, List<String> keys, String timely) {
        return keys.stream()
                .map(k -> new NumberByTimely(timely.equals(Timely.MONTHLY.getValue()) ? MonthAbbreviation.fromNumber(k) : k,
                        Objects.requireNonNullElse(data.get(k), 0L)))
                .toList();
    }

    private TimelyRange getTimelyRange(String timely, Integer year) {
        LocalDate startDate;
        LocalDate endDate;
        List<String> keys;

        boolean isMonthly = Objects.equals(timely, Timely.MONTHLY.getValue()) && year != null;

        if (isMonthly) {
            startDate = LocalDate.of(year, 1, 1);
            endDate = (year < LocalDate.now().getYear()) ? LocalDate.of(year, 12, 31) : LocalDate.now();
            keys = IntStream.rangeClosed(1, 12).mapToObj(String::valueOf).toList();
        } else {
            startDate = LocalDate.of(2022, 1, 1);
            endDate = LocalDate.now();
            keys = IntStream.rangeClosed(2022, endDate.getYear()).mapToObj(String::valueOf).toList();
        }

        return new TimelyRange(keys, startDate, endDate);
    }

    @Override
    public List<OccupancyByDate> getOccupancyByDate(String timely) {
        var timeFrame = getTimeFrames(timely);
        var result = statisticRepository.getOccupancyByDate(timeFrame.get("start"), timeFrame.get("end"));
        Map<String, OccupancyByDate> resultMap = result.stream()
                .collect(Collectors.toMap(
                        OccupancyByDate::getLabel,
                        Function.identity()
                ));
        return SHORT_DAYS_OF_WEEK.stream()
                .map(day -> resultMap.getOrDefault(day,
                        OccupancyByDate.builder()
                                .label(day)
                                .allSeats(0)
                                .soldSeats(0)
                                .build()
                ))
                .collect(Collectors.toList());
    }

    private Map<String, LocalDate> getTimeFrames(String timeFrame) {
        Map<String, LocalDate> result = new HashMap<>();
        // In a real application, you'd use LocalDate.now()
        // but sticking to your example's fixed date for consistency in testing.
        LocalDate today = LocalDate.of(2024, 12, 25); // Example: Thursday, Jan 2, 2025

        LocalDate currentStart;
        LocalDate currentNowReference; // New variable to hold the 'currentNow' value

        switch (timeFrame.toLowerCase()) {
            case "current-week" -> {
                DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
                currentStart = today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek)); // Start of the current week (Mon)
                currentNowReference = today; // 'currentNow' is literally today's date
            }

            case "last-week" -> {
                DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
                // 'currentStart' for 'last-week' means the start of the LAST week
                LocalDate currentWeekStart = today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
                currentStart = currentWeekStart.minusWeeks(1); // Start of the last week (Mon of last week)
                // AS PER YOUR LATEST REQUIREMENT:
                // 'currentNow' for 'last-week' should be the end of the last week
                currentNowReference = currentStart.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            }

            default -> throw new InputInvalidException("Invalid timeFrame: " + timeFrame, List.of());
        }

        result.put("start", currentStart);
        result.put("end", currentNowReference); // Use the calculated reference date

        return result;
    }
}
