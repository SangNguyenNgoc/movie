package com.example.movieofficial.api.movie.interfaces.mappers;

import com.example.movieofficial.api.movie.dtos.MovieInfoLanding;
import com.example.movieofficial.api.movie.entities.Format;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FormatMapper {
    MovieInfoLanding.FormatDto toDto(Format format);
}
