package com.example.movieofficial.api.hall.interfaces;

import com.example.movieofficial.api.hall.entities.Seat;
import com.example.movieofficial.api.hall.entities.SeatRow;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    SeatRow.SeatDto toDto(Seat seat);
}
