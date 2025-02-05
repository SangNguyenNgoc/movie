package com.example.movieofficial.api.movie.usecases;

public interface RatingUseCase {
    void execute(String movieSlug, Integer rating, String ratingKey);
}
