package com.example.movieofficial.api.show.interfaces;

import com.example.movieofficial.api.show.entities.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show, String> {
    @Query("select s from Show s where s.startDate = ?1 and s.hall.id = ?2")
    List<Show> findByStartDateAndHallId(LocalDate startDate, Long id);

    @Query("""
            select s from Show s
            join fetch s.hall h
            join fetch s.movie m
            join fetch h.seats
            where s.id = ?1
            and (s.startDate > ?2
            or (s.startDate = ?2 and s.startTime > ?3))
            """)
    Optional<Show> findWithDetailsByIdAndDateTime(String id, LocalDate date, LocalTime time);

    @Query("select s from Show s " +
            "where s.startDate = ?1 " +
            "and s.movie.slug = ?2 " +
            "and s.format.id = ?3 " +
            "and (s.startDate != ?5 or s.startTime > ?4) " +
            "order by s.startTime")
    List<Show> findSameShow(LocalDate startDate, String slug, Long formatId, LocalTime time, LocalDate today);

    @Transactional
    @Modifying
    @Query("update Show s set s.status = false where s.startDate < ?1")
    void updateStatusByStartDateBefore(LocalDate startDate);


}