package com.example.movieofficial.api.concession.repositories;

import com.example.movieofficial.api.concession.entities.Concession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConcessionRepository extends JpaRepository<Concession, String> {

}
