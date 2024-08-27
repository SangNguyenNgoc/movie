package com.example.movieofficial.api.hall.interfaces;

import com.example.movieofficial.api.hall.entities.SeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatTypeRepository extends JpaRepository<SeatType, Integer> {
}