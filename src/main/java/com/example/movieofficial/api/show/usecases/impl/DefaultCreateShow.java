package com.example.movieofficial.api.show.usecases.impl;

import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.hall.interfaces.HallRepository;
import com.example.movieofficial.api.movie.entities.Format;
import com.example.movieofficial.api.movie.entities.Movie;
import com.example.movieofficial.api.movie.repositories.FormatRepository;
import com.example.movieofficial.api.movie.repositories.MovieRepository;
import com.example.movieofficial.api.show.dtos.ShowCreate;
import com.example.movieofficial.api.show.dtos.ShowInfo;
import com.example.movieofficial.api.show.entities.Show;
import com.example.movieofficial.api.show.interfaces.ShowMapper;
import com.example.movieofficial.api.show.interfaces.ShowRepository;
import com.example.movieofficial.api.show.usecases.CreateShowUseCase;
import com.example.movieofficial.utils.exceptions.DataNotFoundException;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import com.example.movieofficial.utils.services.ObjectsValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultCreateShow implements CreateShowUseCase {

    ShowRepository showRepository;
    MovieRepository movieRepository;
    FormatRepository formatRepository;
    HallRepository hallRepository;
    ShowMapper showMapper;
    ObjectsValidator<ShowCreate> showValidator;


    @Override
    public Movie checkMovieInput(String movieId, LocalDateTime dateTime) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Movie not found"))
        );
        LocalDate date = dateTime.toLocalDate();
        if (!(date.isAfter(movie.getReleaseDate()) && date.isBefore(movie.getEndDate()))) {
            throw new InputInvalidException(
                    "Input invalid",
                    List.of("The movie is not allowed to be shown at this time.")
            );
        }
        return movie;
    }


    @Override
    public Format checkFormatInput(String movieId, Long formatId) {
        Format format = formatRepository.findById(formatId).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Movie format not found"))
        );
        if (!formatRepository.existsByIdAndMoviesId(formatId, movieId)) {
            throw new DataNotFoundException(
                    "Data not found",
                    List.of("The movie is not shown in the format you requested")
            );
        }
        return format;
    }


    @Override
    public Hall checkHallInput(Long hallId, LocalDateTime dateTime, Movie movie) {
        Hall hall = hallRepository.findByIdAndStatusIdAndCinemaStatusId(hallId).orElseThrow(
                () -> new InputInvalidException(
                        "Input invalid",
                        List.of("The screening room does not exist or is under maintenance.")
                ));
        List<Show> shows = showRepository.findByStartDateAndHallId(dateTime.toLocalDate(), hallId);
        LocalTime startTimeTesting = dateTime.toLocalTime();
        LocalTime endTimeTesting = startTimeTesting.plusMinutes(movie.getRunningTime() + 30);
        for (Show show : shows) {
            LocalTime startTimeInData = show.getStartTime();
            LocalTime endTimeInData = startTimeInData.plusMinutes(show.getRunningTime());
            if ((startTimeTesting.isAfter(startTimeInData) && startTimeTesting.isBefore(endTimeInData)) ||
                    (startTimeInData.isAfter(startTimeTesting) && startTimeInData.isBefore(endTimeTesting)) ||
                    (startTimeTesting.equals(startTimeInData))
            ) {
                throw new InputInvalidException(
                        "Input invalid",
                        List.of("The screening room is not available at this time.")
                );
            }
        }
        return hall;
    }


    @Override
    public ShowInfo execute(ShowCreate showCreate) {
        showValidator.validate(showCreate);
        Movie movie = checkMovieInput(showCreate.getMovieId(), showCreate.getStartTime());
        Format format = checkFormatInput(showCreate.getMovieId(), showCreate.getFormatId());
        Hall hall = checkHallInput(showCreate.getHallId(), showCreate.getStartTime(), movie);
        Show show = Show.builder()
                .startDate(showCreate.getStartTime().toLocalDate())
                .startTime(showCreate.getStartTime().toLocalTime())
                .format(format)
                .movie(movie)
                .runningTime(movie.getRunningTime() + 30)
                .hall(hall)
                .status(true)
                .build();
        var showSaved = showRepository.save(show);
        return showMapper.toInfo(showSaved);
    }
}
