package com.example.movieofficial.api.reports.dtos;

import lombok.Getter;

@Getter
public enum Timely {
    YEARLY("yearly"),
    MONTHLY("monthly");

    private final String value;

    Timely(String value) {
        this.value = value;
    }
}
