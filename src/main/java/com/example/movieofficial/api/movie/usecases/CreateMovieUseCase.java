package com.example.movieofficial.api.movie.usecases;

import com.example.movieofficial.api.movie.dtos.MovieInfoAdmin;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CreateMovieUseCase {
    MovieInfoAdmin execute(String movieRequest, MultipartFile poster, MultipartFile horPoster, List<MultipartFile> images);

}
