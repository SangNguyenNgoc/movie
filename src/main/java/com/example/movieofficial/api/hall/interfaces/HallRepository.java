package com.example.movieofficial.api.hall.interfaces;

import com.example.movieofficial.api.hall.entities.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HallRepository extends JpaRepository<Hall, Long> {

    @Query("select h from Hall h " +
            "where h.id = ?1 and h.status.id = 1 and h.cinema.status.id = 1")
    Optional<Hall> findByIdAndStatusIdAndCinemaStatusId(Long id);

}