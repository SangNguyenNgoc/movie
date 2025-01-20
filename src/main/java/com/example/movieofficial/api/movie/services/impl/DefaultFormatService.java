package com.example.movieofficial.api.movie.services.impl;

import com.example.movieofficial.api.movie.dtos.MovieInfoLanding;
import com.example.movieofficial.api.movie.mappers.FormatMapper;
import com.example.movieofficial.api.movie.repositories.FormatRepository;
import com.example.movieofficial.api.movie.services.FormatService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultFormatService implements FormatService {

    private final FormatRepository formatRepository;
    private final FormatMapper formatMapper;

    @Override
    @Cacheable(value = "formats", key = "'landing-page'")
    public List<MovieInfoLanding.FormatDto> getAllFormats() {
        var formats = formatRepository.findAll();
        return formats.stream().map(formatMapper::toDto).collect(Collectors.toList());
    }
}
