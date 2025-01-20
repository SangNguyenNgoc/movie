package com.example.movieofficial.api.movie.usecases.impl;

import com.example.movieofficial.api.movie.dtos.MovieInfoAdmin;
import com.example.movieofficial.api.movie.dtos.MovieUpdate;
import com.example.movieofficial.api.movie.entities.Image;
import com.example.movieofficial.api.movie.entities.Movie;
import com.example.movieofficial.api.movie.mappers.MovieMapper;
import com.example.movieofficial.api.movie.repositories.FormatRepository;
import com.example.movieofficial.api.movie.repositories.GenreRepository;
import com.example.movieofficial.api.movie.repositories.ImageRepository;
import com.example.movieofficial.api.movie.repositories.MovieRepository;
import com.example.movieofficial.api.movie.usecases.UpdateMovieUseCase;
import com.example.movieofficial.utils.exceptions.DataNotFoundException;
import com.example.movieofficial.utils.services.ObjectsValidator;
import com.example.movieofficial.utils.services.S3Service;
import com.example.movieofficial.utils.services.UtilsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DefaultUpdateMovie implements UpdateMovieUseCase {

    MovieMapper movieMapper;
    MovieRepository movieRepository;
    ObjectsValidator<MovieUpdate> movieUpdateObjectsValidator;
    S3Service s3Service;
    GenreRepository genreRepository;
    FormatRepository formatRepository;
    UtilsService utilsService;

    @Override
    @Transactional
    public MovieInfoAdmin updateMovieInfo(MovieUpdate movieUpdate, String movieId) {
        movieUpdateObjectsValidator.validate(movieUpdate);
        var movie = movieRepository.findById(movieId).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Movie not found"))
        );
        var movieUpdated = movieMapper.partialUpdate(movieUpdate, movie);
        if (movieUpdate.getName() != null) {
            movie.setSlug(utilsService.toSlug(movieUpdate.getName()));
        }
        if (movieUpdate.getDirectorList() != null) {
            movie.setDirector(String.join(", ", movieUpdate.getDirectorList()));
        }
        if (movieUpdate.getPerformerList() != null) {
            movie.setPerformers(String.join(", ", movieUpdate.getPerformerList()));
        }
        if (movieUpdate.getGenreIdList() != null) {
            var genres = genreRepository.findAllById(movieUpdate.getGenreIdList());
            movie.setGenres(genres.stream().collect(Collectors.toSet()));
        }
        if (movieUpdate.getFormatIdList() != null) {
            var formats = formatRepository.findAllById(movieUpdate.getFormatIdList());
            movie.setFormats(formats.stream().collect(Collectors.toSet()));
        }
        return movieMapper.toInfoAdmin(movieUpdated);
    }

    @Override
    @Transactional
    public MovieInfoAdmin addImage(MultipartFile file, String movieId) {
        var movie = movieRepository.findById(movieId).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Movie not found"))
        );
        var url = s3Service.uploadFile(file, movie.getSlug(), "images");
        String extension = Objects.requireNonNull(file.getOriginalFilename()).substring(
                file.getOriginalFilename().lastIndexOf(".") + 1
        );
        var image = Image.builder()
                .extension(extension)
                .path(url)
                .movie(movie)
                .build();
        movie.getImages().add(image);
        return movieMapper.toInfoAdmin(movie);
    }

    @Override
    @Transactional
    public MovieInfoAdmin deleteImage(Long imageId, String movieId) {
        var movie = movieRepository.findById(movieId).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Movie not found"))
        );
        var imageToRemove = movie.getImages().stream()
                .filter(image -> image.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("Image not found", List.of("Image ID not found")));
        movie.getImages().remove(imageToRemove);
        return movieMapper.toInfoAdmin(movie);
    }

    @Override
    @Transactional
    public MovieInfoAdmin updatePoster(MultipartFile file, String movieId, Boolean horizontal) {
        var movie = movieRepository.findById(movieId).orElseThrow(
                () -> new DataNotFoundException("Data not found", List.of("Movie not found"))
        );
        if (horizontal) {
            movie.setHorizontalPoster(s3Service.uploadFile(file, movie.getSlug(), "hor-poster"));
        } else {
            movie.setPoster(s3Service.uploadFile(file, movie.getSlug(), "posters"));
        }
        return movieMapper.toInfoAdmin(movie);
    }



//    @Override
//    @Transactional
//    public MovieInfoAdmin updateGenres(Long genreId, String movieId) {
//        var movie = movieRepository.findById(movieId).orElseThrow(
//                () -> new DataNotFoundException("Data not found", List.of("Movie not found"))
//        );
//        boolean isExits = movieRepository.existsByGenreAndId(genreId, movieId);
//        if (isExits) {
//            movieRepository.deleteGenre(genreId, movieId);
//        } else {
//            movieRepository.addGenre(genreId, movieId);
//        }
//        return movieMapper.toInfoAdmin(movie);
//    }
//
//    @Override
//    @Transactional
//    public MovieInfoAdmin updateFormats(Long formatId, String movieId) {
//        var movie = movieRepository.findById(movieId).orElseThrow(
//                () -> new DataNotFoundException("Data not found", List.of("Movie not found"))
//        );
//        boolean isExits = movieRepository.existsByFormatAndId(formatId, movieId);
//        if (isExits) {
//            movieRepository.deleteFormat(formatId, movieId);
//        } else {
//            movieRepository.deleteGenre(formatId, movieId);
//        }
//        return movieMapper.toInfoAdmin(movie);
//    }
}
