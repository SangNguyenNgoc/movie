package com.example.movieofficial.api.movie.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.example.movieofficial.api.movie.entities.MovieStatus}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatusInfo implements Serializable {
    Long id;
    String description;
    String slug;
    List<MovieInfoLanding> movies;
}