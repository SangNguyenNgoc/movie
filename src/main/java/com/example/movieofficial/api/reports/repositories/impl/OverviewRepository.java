package com.example.movieofficial.api.reports.repositories.impl;

import com.example.movieofficial.api.reports.dtos.OccupancyByShow;
import com.example.movieofficial.api.reports.repositories.IOverviewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OverviewRepository implements IOverviewRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    public Optional<BigDecimal> getTotalProfit(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT SUM(sub.total) AS total
            FROM (
                SELECT DISTINCT b.id, b.total, s.start_date
                FROM bills b
                JOIN tickets t ON b.id = t.bill_id
                JOIN shows s ON t.showtime_id = s.id
                WHERE s.start_date BETWEEN ? AND ?
            ) AS sub
            """;
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(sql, BigDecimal.class, startDate, endDate)
        );
    }

    @Override
    public Optional<Long> getTotalTickets(LocalDate startDate, LocalDate endDate) {
        String sql = """
                select COUNT(t.ticket_id) as total_tickets
                from tickets t
                join shows s on s.id = t.showtime_id
                where s.start_date between ? and ?
                """;
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(sql, Long.class, startDate, endDate)
        );
    }

    @Override
    public Optional<Long> getTotalShows(LocalDate startDate, LocalDate endDate) {
        String sql = """
                select COUNT(s.id) as total_shows
                from shows s
                where s.start_date between ? and ?
                """;
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(sql, Long.class, startDate, endDate)
        );
    }

    @Override
    public List<OccupancyByShow> getFillRate(LocalDate startDate, LocalDate endDate) {
        String sql = """
                select s.id, s.start_date, s.start_time, h.available_seats as all_seats, COUNT(t.ticket_id) as sold_seats
                from tickets t
                join shows s on s.id = t.showtime_id
                join halls h on s.hall_id = h.id
                where s.start_date between ? and ?
                group by s.id;
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> OccupancyByShow.builder()
                .id(rs.getString("id"))
                .startDate(rs.getDate("start_date").toLocalDate())
                .startTime(rs.getTime("start_time").toLocalTime())
                .availableSeats(rs.getInt("all_seats"))
                .soldSeats(rs.getInt("sold_seats"))
                .build(), startDate, endDate);
    }
}
