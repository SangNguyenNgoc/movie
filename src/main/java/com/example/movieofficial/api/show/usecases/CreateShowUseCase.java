package com.example.movieofficial.api.show.usecases;

import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.movie.entities.Format;
import com.example.movieofficial.api.movie.entities.Movie;
import com.example.movieofficial.api.show.dtos.ShowCreate;
import com.example.movieofficial.api.show.dtos.ShowInfo;

import java.time.LocalDateTime;

public interface CreateShowUseCase {
    Movie checkMovieInput(String movieId, LocalDateTime dateTime);

    Format checkFormatInput(String movieId, Long formatId);

    Hall checkHallInput(Long hallId, LocalDateTime dateTime, Movie movie);

    ShowInfo execute(ShowCreate showCreate);
}
