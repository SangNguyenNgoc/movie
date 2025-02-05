package com.example.movieofficial.api.movie.services;

import com.example.movieofficial.api.movie.dtos.*;
import com.example.movieofficial.utils.dtos.PageResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.multipart.MultipartFile;

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

    List<MovieInfoLanding> searchMoviesBySlug(String search);

    MovieInfoAdmin create(
            String movieRequest, MultipartFile poster,
            MultipartFile horPoster, List<MultipartFile> images
    );

    MovieInfoAdmin updateMovieInfo(MovieUpdate movieUpdate, String movieId);

    MovieInfoAdmin updateImages(MultipartFile image, Long imageId, String movieId);

    MovieInfoAdmin updatePoster(MultipartFile poster, String movieId, Boolean horizontal);

    void ratingMovie(String movieSlug, Integer rating, String ratingKey);
}
