package com.example.movieofficial.api.concession.services.impl;

import com.example.movieofficial.api.cinema.interfaces.CinemaRepository;
import com.example.movieofficial.api.concession.dtos.ConcessionInfo;
import com.example.movieofficial.api.concession.entities.Concession;
import com.example.movieofficial.api.concession.mappers.ConcessionMapper;
import com.example.movieofficial.api.concession.services.ConcessionService;
import com.example.movieofficial.utils.exceptions.DataNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultConcessionService implements ConcessionService {

    CinemaRepository cinemaRepository;
    ConcessionMapper concessionMapper;

    @Override
    public List<ConcessionInfo> getConcessionByCinema(String cinemaId) {
        var cinema = cinemaRepository.findById(cinemaId).orElseThrow(
                () -> new DataNotFoundException("Cinema not found", List.of())
        );
        var concessions = cinema.getConcessions();
        return concessions.stream().sorted(Comparator.comparing(Concession::getPriority).reversed())
                .map(concessionMapper::toInfo).collect(Collectors.toList());
    }
}
