package com.example.movieofficial.api.hall.interfaces;

import com.example.movieofficial.api.hall.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
}