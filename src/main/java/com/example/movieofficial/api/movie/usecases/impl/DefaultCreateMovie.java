package com.example.movieofficial.api.movie.usecases.impl;

import com.example.movieofficial.api.movie.dtos.MovieCreate;
import com.example.movieofficial.api.movie.dtos.MovieInfoAdmin;
import com.example.movieofficial.api.movie.entities.Image;
import com.example.movieofficial.api.movie.mappers.MovieMapper;
import com.example.movieofficial.api.movie.repositories.*;
import com.example.movieofficial.api.movie.usecases.CreateMovieUseCase;
import com.example.movieofficial.utils.exceptions.ServerInternalException;
import com.example.movieofficial.utils.services.ObjectsValidator;
import com.example.movieofficial.utils.services.S3Service;
import com.example.movieofficial.utils.services.UtilsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DefaultCreateMovie implements CreateMovieUseCase {

    ObjectMapper objectMapper;
    MovieMapper movieMapper;
    UtilsService utilsService;
    S3Service s3Service;
    ObjectsValidator<MovieCreate> validator;

    MovieRepository movieRepository;
    MovieStatusRepository movieStatusRepository;
    ImageRepository imageRepository;
    GenreRepository genreRepository;
    FormatRepository formatRepository;

    ApplicationContext applicationContext;

    @Override
    public MovieInfoAdmin execute(
            String movieRequest, MultipartFile poster,
            MultipartFile horPoster, List<MultipartFile> images
    ) {
        try {
            MovieCreate movieCreate =  objectMapper.readValue(movieRequest, MovieCreate.class);
            validator.validate(movieCreate);
            var movie = movieMapper.toEntity(movieCreate);
            movie.setSlug(utilsService.toSlug(movie.getName()));
            movie.setNumberOfRatings(0);
            movie.setSumOfRatings(0);
            movie.setPerformers(String.join( ", ", movieCreate.getPerformerList()));
            var movieStatus = movieStatusRepository.findById(5L).orElseThrow(
                    () -> new ServerInternalException("Server internal", List.of("Movie status not found"))
            );
            movie.setStatus(movieStatus);
            var genres = genreRepository.findAllById(movieCreate.getGenreIds());
            var formats = formatRepository.findAllById(movieCreate.getFormatIds());
            movie.setFormats(formats.stream().collect(Collectors.toSet()));
            movie.setGenres(genres.stream().collect(Collectors.toSet()));
            movie.setPoster(s3Service.uploadFile(poster, movie.getSlug(), "posters"));
            movie.setHorizontalPoster(s3Service.uploadFile(horPoster, movie.getSlug(), "hor-poster"));
            movieRepository.save(movie);
            DefaultCreateMovie me = applicationContext.getBean(DefaultCreateMovie.class);
            me.sendImage(images, movie.getId());
            return movieMapper.toInfoAdmin(movie);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new ServerInternalException("Server internal", List.of(e.getMessage()));
        }
    }

    private void sendImage(List<MultipartFile> images, String movieId) {
        var movie = movieRepository.findById(movieId);
        movie.ifPresent(value -> {
            List<Image> results = new ArrayList<>();
            for (MultipartFile img : images) {
                String extension = Objects.requireNonNull(img.getOriginalFilename()).substring(
                        img.getOriginalFilename().lastIndexOf(".") + 1
                );
                Image image = Image.builder()
                        .movie(value)
                        .path(s3Service.uploadFile(img, value.getSlug(), "images"))
                        .extension(extension)
                        .build();
                results.add(image);
            }
            imageRepository.saveAll(results);
        });
    }
}
