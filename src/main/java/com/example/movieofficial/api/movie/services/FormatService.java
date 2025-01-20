package com.example.movieofficial.api.movie.services;

import com.example.movieofficial.api.movie.dtos.MovieInfoLanding;

import java.util.List;

public interface FormatService {
    List<MovieInfoLanding.FormatDto> getAllFormats();
}
