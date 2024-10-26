package com.example.movieofficial.api.movie.services;

import com.example.movieofficial.api.movie.dtos.MovieInfoLanding;
import com.example.movieofficial.api.movie.interfaces.mappers.FormatMapper;
import com.example.movieofficial.api.movie.interfaces.repositories.FormatRepository;
import com.example.movieofficial.api.movie.interfaces.services.FormatService;
import com.example.movieofficial.utils.dtos.ListResponse;
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
