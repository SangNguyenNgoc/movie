package com.example.movieofficial.api.movie.interfaces;

import com.example.movieofficial.api.movie.dtos.*;
import com.example.movieofficial.api.movie.entities.Movie;
import com.example.movieofficial.api.movie.entities.MovieStatus;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface MovieMapper {

    StatusInfo toStatusInfo(MovieStatus movieStatus);

    MovieInfoLanding toInfoLanding(Movie movie);

    MovieInfoAdmin toInfoAdmin(Movie movie);

    MovieDetail toDetail(Movie movie);

    MovieAndShows toMovieAndShows(Movie movie);

}

