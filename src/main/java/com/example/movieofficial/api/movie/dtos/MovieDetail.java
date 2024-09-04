package com.example.movieofficial.api.movie.dtos;

import com.example.movieofficial.api.cinema.dtos.CinemaAndShows;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link com.example.movieofficial.api.movie.entities.Movie}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieDetail extends RepresentationModel<MovieDetail> implements Serializable {
    private String id;
    private String name;
    private String subName;
    private String director;
    private String performers;
    private LocalDate releaseDate;
    private Integer runningTime;
    private String language;
    private Integer numberOfRatings;
    private Integer sumOfRatings;
    private String description;
    private String poster;
    private String horizontalPoster;
    private String trailer;
    private Integer ageRestriction;
    private String producer;
    private String slug;
    private List<FormatDto> formats;
    private List<GenreDto> genres;
    private List<ImageDto> images;
    private MovieStatusDto status;
    private List<CinemaAndShows> cinemas;

    /**
     * DTO for {@link com.example.movieofficial.api.movie.entities.Format}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FormatDto implements Serializable {
        private Long id;
        private String caption;
        private String version;
    }

    /**
     * DTO for {@link com.example.movieofficial.api.movie.entities.Genre}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GenreDto implements Serializable {
        private Long id;
        private String name;
    }

    /**
     * DTO for {@link com.example.movieofficial.api.movie.entities.Image}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageDto implements Serializable {
        private Long id;
        private String path;
        private String extension;
    }

    /**
     * DTO for {@link com.example.movieofficial.api.movie.entities.MovieStatus}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MovieStatusDto implements Serializable {
        private Long id;
        private String description;
        private String slug;
    }
}