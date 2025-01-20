package com.example.movieofficial.api.movie.repositories;

import com.example.movieofficial.api.movie.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
}
