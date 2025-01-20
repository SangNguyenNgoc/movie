package com.example.movieofficial.api.cinema.interfaces;

import com.example.movieofficial.api.cinema.dtos.*;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public interface CinemaService {

    List<CinemaInfo> getAll();

    List<CinemaInfo> getAll(Integer page, Integer size);

    CinemaInfo getById(String id);

    List<CinemaInfoLanding> getCinemaForLanding();

    CinemaDetail getCinemaAndShows(String slug);

    CinemaDetail getCinemaAndShowsFromRedis(String slug);

    List<CinemaDetail> getAllCinemaAndShows();

    List<CinemaDetail> getAllCinemaAndShowsFromRedis();

    @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Ho_Chi_Minh")
    void cacheAllCinemasMoviesShows();

    CinemaInfo createCinema(CinemaCreate cinemaCreate);

    CinemaInfo updateCinema(CinemaUpdate cinemaUpdate, String cinemaId);
}
