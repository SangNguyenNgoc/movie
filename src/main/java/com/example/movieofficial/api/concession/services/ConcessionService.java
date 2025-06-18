package com.example.movieofficial.api.concession.services;

import com.example.movieofficial.api.concession.dtos.ConcessionInfo;

import java.util.List;

public interface ConcessionService {

    List<ConcessionInfo> getConcessionByCinema(String cinemaId);
}
