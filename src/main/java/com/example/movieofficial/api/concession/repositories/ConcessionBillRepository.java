package com.example.movieofficial.api.concession.repositories;

import com.example.movieofficial.api.bill.entities.Bill;
import com.example.movieofficial.api.concession.entities.ConcessionBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcessionBillRepository extends JpaRepository<ConcessionBill, Long> {

    void deleteByBill(Bill bill);
}
