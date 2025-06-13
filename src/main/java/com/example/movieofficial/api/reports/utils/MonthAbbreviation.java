package com.example.movieofficial.api.reports.utils;

public enum MonthAbbreviation {
    JAN("1", "Jan"),
    FEB("2", "Feb"),
    MAR("3", "Mar"),
    APR("4", "Apr"),
    MAY("5", "May"),
    JUN("6", "Jun"),
    JUL("7", "Jul"),
    AUG("8", "Aug"),
    SEP("9", "Sep"),
    OCT("10", "Oct"),
    NOV("11", "Nov"),
    DEC("12", "Dec");

    private final String number;
    private final String abbreviation;

    MonthAbbreviation(String number, String abbreviation) {
        this.number = number;
        this.abbreviation = abbreviation;
    }

    public static String fromNumber(String number) {
        for (MonthAbbreviation m : MonthAbbreviation.values()) {
            if (m.number.equals(number)) {
                return m.abbreviation;
            }
        }
        return null; // hoặc throw new IllegalArgumentException("Invalid month number: " + number);
    }
}
