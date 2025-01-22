package com.example.movieofficial.api.hall.repositories;

import com.example.movieofficial.api.hall.entities.HallStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface HallStatusRepository extends JpaRepository<HallStatus, Long> {
}