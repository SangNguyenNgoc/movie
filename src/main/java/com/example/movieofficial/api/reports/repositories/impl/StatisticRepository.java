package com.example.movieofficial.api.reports.repositories.impl;

import com.example.movieofficial.api.reports.dtos.OccupancyByDate;
import com.example.movieofficial.api.reports.repositories.IStatisticRepository;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticRepository implements IStatisticRepository {
    JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, BigDecimal> getProfitByTimely(String timely, LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "startDate cannot be null");
        Objects.requireNonNull(endDate, "endDate cannot be null");

        String actualTimeFrame = (timely != null ? timely.toLowerCase() : "monthly");

        String periodSelect;
        String groupBy;

        Map<String, BigDecimal> profitByTimely = new HashMap<>();

        switch (actualTimeFrame) {
            case "monthly" -> {
                periodSelect = "MONTH(temp.start_date) AS period_value";
                groupBy = "MONTH(temp.start_date)";
            }
            case "yearly" -> {
                periodSelect = "YEAR(temp.start_date) AS period_value";
                groupBy = "YEAR(temp.start_date)";
            }
            default -> throw new InputInvalidException("Unexpected value: " + actualTimeFrame, List.of());
        }

        String sql = "SELECT " +
                periodSelect + ", " +
                "SUM(temp.total) AS total_profit " +
                "FROM ( " +
                "    SELECT DISTINCT b.id, b.total, s.start_date " +
                "    FROM bills b " +
                "    JOIN tickets t ON b.id = t.bill_id " +
                "    JOIN shows s ON t.showtime_id = s.id " +
                "    WHERE s.start_date BETWEEN ? AND ? " +
                ") temp " +
                "GROUP BY " + groupBy + " " +
                "ORDER BY period_value";

        jdbcTemplate.query(sql, rs -> {
            profitByTimely.put(
                    rs.getString("period_value"),
                    rs.getBigDecimal("total_profit")
            );
        }, startDate, endDate);

        return profitByTimely;
    }

    @Override
    public Map<String, Long> getTotalShowsByTimely(String timely, LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(startDate, "startDate cannot be null");
        Objects.requireNonNull(endDate, "endDate cannot be null");

        String actualTimeFrame = (timely != null ? timely.toLowerCase() : "monthly");

        String periodSelect;
        String groupBy;

        Map<String, Long> totalShowsByTimely = new HashMap<>();

        switch (actualTimeFrame) {
            case "monthly" -> {
                periodSelect = "MONTH(s.start_date) AS period_value";
                groupBy = "MONTH(s.start_date)";
            }
            case "yearly" -> {
                periodSelect = "YEAR(s.start_date) AS period_value";
                groupBy = "YEAR(s.start_date)";
            }
            default -> throw new InputInvalidException("Unexpected value: " + actualTimeFrame, List.of());
        }

        String sql = "SELECT " +
                periodSelect + ", " +
                "COUNT(s.id) AS total_shows " +
                "FROM shows s " +
                "WHERE s.start_date BETWEEN ? AND ? " +
                "GROUP BY " + groupBy + " " +
                "ORDER BY period_value";

        jdbcTemplate.query(sql, rs -> {
            totalShowsByTimely.put(
                    rs.getString("period_value"),
                    rs.getLong("total_shows")
            );
        }, startDate, endDate);

        return totalShowsByTimely;
    }

    @Override
    public List<OccupancyByDate> getOccupancyByDate(LocalDate startDate, LocalDate endDate) {
        String sql = """
                SELECT s.start_date as date,
                       SUM(h.available_seats) AS all_seats,
                       COUNT(t.ticket_id) AS sold_seats
                FROM shows s
                         JOIN halls h ON s.hall_id = h.id
                         LEFT JOIN tickets t ON s.id = t.showtime_id
                WHERE s.start_date BETWEEN ? AND ?
                GROUP BY s.start_date
                ORDER BY s.start_date;
                """;
        return jdbcTemplate.query(sql, (rs, r) -> {
            var date = rs.getDate("date").toLocalDate();
            var allSeats = rs.getInt("all_seats");
            var soldSeats = rs.getInt("sold_seats");
            return OccupancyByDate.builder()
                    .startDate(date)
                    .label(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                    .allSeats(allSeats)
                    .soldSeats(soldSeats)
                    .build();
        }, startDate, endDate);
    }
}
