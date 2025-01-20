package com.example.movieofficial.api.cinema.interfaces;

import com.example.movieofficial.api.cinema.dtos.*;
import com.example.movieofficial.api.cinema.entities.Cinema;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CinemaMapper {

    CinemaAndShows toCinemaAndShows(Cinema cinema);

    CinemaInfo toInfo(Cinema cinema);

    CinemaInfoLanding toInfoLanding(Cinema cinema);

    CinemaDetail toDetail(Cinema cinema);

    Cinema toEntity(CinemaCreate cinemaCreate);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Cinema partialUpdate(CinemaCreate cinemaCreate, @MappingTarget Cinema cinema);

    Cinema toEntity(CinemaUpdate cinemaUpdate);

    CinemaUpdate toDto(Cinema cinema);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Cinema partialUpdate(CinemaUpdate cinemaUpdate, @MappingTarget Cinema cinema);
}
