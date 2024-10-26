package com.example.movieofficial.api.movie.interfaces.services;

import com.example.movieofficial.api.movie.dtos.MovieInfoLanding;

import java.util.List;

public interface FormatService {
    List<MovieInfoLanding.FormatDto> getAllFormats();
}
