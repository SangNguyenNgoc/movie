package com.example.movieofficial.api.show.interfaces;

import com.example.movieofficial.api.bill.dtos.BillDetail;
import com.example.movieofficial.api.cinema.dtos.CinemaAndShows;
import com.example.movieofficial.api.hall.entities.Seat;
import com.example.movieofficial.api.movie.dtos.MovieAndShows;
import com.example.movieofficial.api.show.entities.Show;
import com.example.movieofficial.api.show.dtos.ShowDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShowMapper {

    CinemaAndShows.ShowDto toDtoInCinema(Show show);

    MovieAndShows.ShowDto toShowDtoInMovieAndShows(Show show);

    ShowDetail toDetail(Show show);

    @Mapping(source = "type.price", target = "price")
    ShowDetail.HallDto.SeatDto seatToSeatDto(Seat seat);

    BillDetail.TicketDto.ShowDto toShowInBillDetail(Show show);
}
