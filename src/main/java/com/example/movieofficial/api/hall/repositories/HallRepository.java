package com.example.movieofficial.api.hall.repositories;

import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.hall.entities.HallStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HallRepository extends JpaRepository<Hall, Long> {

    @Query("select h from Hall h " +
            "where h.id = ?1 and h.status.id = 1 and h.cinema.status.id = 1")
    Optional<Hall> findByIdAndStatusIdAndCinemaStatusId(Long id);


    @Query("""
            SELECT h
            FROM Hall h
            WHERE h.cinema.id = :cinemaId AND h.status.id = 1 AND h.cinema.status.id = 1
            AND NOT EXISTS (
                SELECT 1 FROM Show s
                WHERE s.hall.id = h.id
                AND s.startDate = :date
            )
    """)
    List<Hall> findHallsWithoutShows(@Param("date") LocalDate date, @Param("cinemaId") String cinemaId);

    List<Hall> findByCinemaIdOrderByIdAsc(String id);

}