package com.example.movieofficial.api.movie.usecases;

import com.example.movieofficial.api.movie.dtos.MovieInfoAdmin;
import com.example.movieofficial.api.movie.dtos.MovieUpdate;
import org.springframework.web.multipart.MultipartFile;


public interface UpdateMovieUseCase {
    MovieInfoAdmin updateMovieInfo(MovieUpdate movieUpdate, String movieId);

//    MovieInfoAdmin updateGenres(Long genreId, String movieId);
//
//    MovieInfoAdmin updateFormats(Long formatId, String movieId);

    MovieInfoAdmin addImage(MultipartFile file, String movieId);

    MovieInfoAdmin deleteImage(Long imageId, String movieId);

    MovieInfoAdmin updatePoster(MultipartFile file, String movieId, Boolean horizontal);
}
