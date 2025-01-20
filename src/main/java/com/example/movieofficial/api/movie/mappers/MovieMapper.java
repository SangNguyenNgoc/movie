package com.example.movieofficial.api.movie.mappers;

import com.example.movieofficial.api.movie.dtos.*;
import com.example.movieofficial.api.movie.entities.Movie;
import com.example.movieofficial.api.movie.entities.MovieStatus;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring")
public interface MovieMapper {

    StatusInfo toStatusInfo(MovieStatus movieStatus);

    MovieInfoLanding toInfoLanding(Movie movie);

    MovieInfoAdmin toInfoAdmin(Movie movie);

    MovieDetail toDetail(Movie movie);

    MovieAndShows toMovieAndShows(Movie movie);

    Movie toEntity(MovieCreate movieCreate);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Movie partialUpdate(MovieUpdate movieUpdate, @MappingTarget Movie movie);
}

