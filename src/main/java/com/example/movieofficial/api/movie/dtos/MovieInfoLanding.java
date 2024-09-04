package com.example.movieofficial.api.movie.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for {@link com.example.movieofficial.api.movie.entities.Movie}
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieInfoLanding extends RepresentationModel<MovieInfoLanding> implements Serializable {
    String id;
    String name;
    String subName;
    LocalDate releaseDate;
    Integer numberOfRatings;
    Integer sumOfRatings;
    String poster;
    String slug;
    Integer ageRestriction;
    Set<FormatDto> formats;
    Set<GenreDto> genres;

    /**
     * DTO for {@link com.example.movieofficial.api.movie.entities.Format}
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class FormatDto implements Serializable {
        Long id;
        String caption;
        String version;
    }

    /**
     * DTO for {@link com.example.movieofficial.api.movie.entities.Genre}
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class GenreDto implements Serializable {
        Long id;
        String name;
    }
}
