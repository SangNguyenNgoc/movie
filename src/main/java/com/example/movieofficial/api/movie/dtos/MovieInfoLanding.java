package com.example.movieofficial.api.movie.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.sql.Date;
import java.util.Set;

/**
 * DTO for {@link com.example.movieofficial.api.movie.entities.Movie}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieInfoLanding implements Serializable {
    String id;
    String name;
    String subName;
    String description;
    Date releaseDate;
    String language;
    Integer numberOfRatings;
    Integer sumOfRatings;
    String poster;
    String horizontalPoster;
    String trailer;
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
