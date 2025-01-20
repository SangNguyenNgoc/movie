package com.example.movieofficial.api.cinema.interfaces;

import com.example.movieofficial.api.cinema.entities.CinemaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CinemaStatusRepository extends JpaRepository<CinemaStatus, Long> {
}
