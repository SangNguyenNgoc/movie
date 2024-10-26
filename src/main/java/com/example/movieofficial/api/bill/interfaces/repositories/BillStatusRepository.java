package com.example.movieofficial.api.bill.interfaces.repositories;

import com.example.movieofficial.api.bill.entities.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillStatusRepository extends JpaRepository<BillStatus, Integer> {
}