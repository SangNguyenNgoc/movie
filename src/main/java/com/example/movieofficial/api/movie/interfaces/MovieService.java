package com.example.movieofficial.api.movie.interfaces;

import com.example.movieofficial.api.movie.dtos.MovieDetail;
import com.example.movieofficial.api.movie.dtos.MovieInfoAdmin;
import com.example.movieofficial.api.movie.dtos.MovieInfoLanding;
import com.example.movieofficial.api.movie.dtos.StatusInfo;
import com.example.movieofficial.utils.dtos.PageResponse;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public interface MovieService {

    List<StatusInfo> getMovieToLanding();

    List<StatusInfo> getMovieToLandingFromRedis();

    List<MovieInfoLanding> getMoviesByStatus(String slug);

    PageResponse<MovieInfoLanding> getMoviesByStatusFromRedis(String slug, Integer page, Integer size);

    PageResponse<MovieInfoAdmin> getAll(Integer page, Integer size);

    MovieInfoAdmin getById(String id);

    MovieDetail getMovieAndShows(String slug);

    List<MovieDetail> getAllMoviesAndShows();

    MovieDetail getMovieAndShowsFromRedis(String slug);

    @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Ho_Chi_Minh")
    void cacheAllMoviesCinemasShows();

    @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Ho_Chi_Minh")
    void cacheAllMoviesToLanding();

    void updateMovieStatus();
}
