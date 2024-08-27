package com.example.movieofficial.api.cinema.interfaces;

import com.example.movieofficial.api.cinema.dtos.CinemaDetail;
import com.example.movieofficial.api.cinema.dtos.CinemaInfo;
import com.example.movieofficial.api.cinema.dtos.CinemaInfoLanding;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public interface CinemaService {

    List<CinemaInfo> getAll();

    List<CinemaInfo> getAll(Integer page, Integer size);

    CinemaInfo getById(String id);

    List<CinemaInfoLanding> getCinemaForLanding();

    CinemaDetail getCinemaAndShows(String slug);

    List<CinemaDetail> getAllCinemaAndShows();

    List<CinemaDetail> getAllCinemaAndShowsFromRedis();

    @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Ho_Chi_Minh")
    void cacheAllCinemasMoviesShows();
}
