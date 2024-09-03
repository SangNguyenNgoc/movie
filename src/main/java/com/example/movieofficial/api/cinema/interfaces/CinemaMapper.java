package com.example.movieofficial.api.cinema.interfaces;

import com.example.movieofficial.api.cinema.dtos.CinemaAndShows;
import com.example.movieofficial.api.cinema.dtos.CinemaDetail;
import com.example.movieofficial.api.cinema.dtos.CinemaInfo;
import com.example.movieofficial.api.cinema.dtos.CinemaInfoLanding;
import com.example.movieofficial.api.cinema.entities.Cinema;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CinemaMapper {

    CinemaAndShows toCinemaAndShows(Cinema cinema);

    CinemaInfo toInfo(Cinema cinema);

    CinemaInfoLanding toInfoLanding(Cinema cinema);

    CinemaDetail toDetail(Cinema cinema);

}
