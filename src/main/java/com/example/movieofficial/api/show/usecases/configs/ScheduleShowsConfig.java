package com.example.movieofficial.api.show.usecases.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ScheduleShowsConfig {

    public final String START_IN_DAY;
    public final String END_IN_DAY;
    public final String CLEANING_TIME;
    public final String INTERVAL_TIME;

    public ScheduleShowsConfig(
             @Value("${schedule_shows.start_in_day}") String startInDay,
             @Value("${schedule_shows.end_in_day}") String endInDay,
             @Value("${schedule_shows.cleaning_time}") String cleaningTime,
             @Value("${schedule_shows.interval_time}") String intervalTime
    ) {
        START_IN_DAY = startInDay;
        END_IN_DAY = endInDay;
        CLEANING_TIME = cleaningTime;
        INTERVAL_TIME = intervalTime;
    }
}
