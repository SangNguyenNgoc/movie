package com.example.movieofficial.api.show.interfaces;

import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.movie.entities.Format;
import com.example.movieofficial.api.movie.entities.Movie;
import com.example.movieofficial.api.show.dtos.ShowCreate;
import com.example.movieofficial.api.show.dtos.ShowDetail;

import java.time.LocalDateTime;

public interface ShowService {

    Movie checkMovieInput(String movieId, LocalDateTime dateTime);

    Format checkFormatInput(String movieId, Long formatId);

    Hall checkHallInput(Long hallId, LocalDateTime dateTime, Movie movie);

    String create(ShowCreate showCreate);

    ShowDetail getShowDetail(String showId);

    void updateShowStatus();

}
