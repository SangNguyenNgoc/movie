package com.example.movieofficial.api.show.interfaces;

import com.example.movieofficial.api.bill.dtos.BillDetail;
import com.example.movieofficial.api.cinema.dtos.CinemaAndShows;
import com.example.movieofficial.api.hall.entities.Seat;
import com.example.movieofficial.api.hall.dtos.SeatRow;
import com.example.movieofficial.api.movie.dtos.MovieAndShows;
import com.example.movieofficial.api.show.dtos.ShowDetail;
import com.example.movieofficial.api.show.entities.Show;
import com.example.movieofficial.api.show.dtos.ShowInfo;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ShowMapper {

    CinemaAndShows.ShowDto toDtoInCinema(Show show);

    MovieAndShows.ShowDto toShowDtoInMovieAndShows(Show show);

    ShowDetail toDetail(Show show);

    BillDetail.TicketDto.ShowDto toShowInBillDetail(Show show);

    ShowInfo toInfo(Show show);
}
