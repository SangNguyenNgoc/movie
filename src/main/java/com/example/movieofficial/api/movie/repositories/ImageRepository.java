package com.example.movieofficial.api.movie.repositories;

import com.example.movieofficial.api.movie.entities.Image;
import com.example.movieofficial.api.movie.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    int deleteByMovieAndId(Movie movie, Long id);
}
