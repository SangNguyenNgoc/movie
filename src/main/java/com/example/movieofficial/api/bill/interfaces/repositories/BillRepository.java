package com.example.movieofficial.api.bill.interfaces.repositories;

import com.example.movieofficial.api.bill.entities.Bill;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, String> {
    @Query("select b from Bill b where b.id = ?1 and b.status.id = ?2")
    Optional<Bill> findByIdAndStatusId(String id, Integer id1);

    @Query("select b from Bill b where b.user.id = ?1 order by b.createDate DESC")
    List<Bill> findByUserIdOrderByCreateDateDesc(String id, Pageable pageable);


}