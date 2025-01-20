package com.example.movieofficial.api.movie.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link com.example.movieofficial.api.movie.entities.Movie}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieUpdate implements Serializable {

    private String name;
    private String subName;

    @Future(message = "Release date MUST be future")
    private LocalDate releaseDate;

    @Future(message = "End date MUST be future")
    private LocalDate endDate;

    @Digits(integer = 3, fraction = 2, message = "Running time MUST not be number")
    private Integer runningTime;

    private String language;

    private String description;

    @Min(value = 8, message = "Age for rated MUST be at least 8")
    @Max(value = 18, message = "Age for rated MUST be less than or equal to 18")
    private Integer ageRestriction;

    private String trailer;

    private String producer;

    private List<String> performerList;

    private List<String> directorList;

    private List<Long> genreIdList;

    private List<Long> formatIdList;
}