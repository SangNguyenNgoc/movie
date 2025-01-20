package com.example.movieofficial.api.movie.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link com.example.movieofficial.api.movie.entities.Movie}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieCreate implements Serializable {

    @NotBlank(message = "Name MUST not be plank")
    private String name;

    @NotBlank(message = "Sub name MUST not be plank")
    private String subName;

    @NotBlank(message = "Director MUST not be plank")
    private String director;

    @NotNull(message = "Performers MUST not be plank")
    private List<String> performerList;

    @NotNull(message = "Release date MUST not be null")
    @Future(message = "Release date MUST be future")
    private LocalDate releaseDate;

    @NotNull(message = "End date MUST not be null")
    @Future(message = "End date MUST be future")
    private LocalDate endDate;

    @NotNull(message = "Running time MUST not be null")
    @Digits(integer = 3, fraction = 2, message = "Running time MUST not be number")
    private Integer runningTime;

    @NotBlank(message = "Language MUST not be plank")
    private String language;

    @NotBlank(message = "Description MUST not be plank")
    private String description;

    @NotNull(message = "Age restriction MUST not be null")
    @Min(value = 8, message = "Age for rated MUST be at least 8")
    @Max(value = 18, message = "Age for rated MUST be less than or equal to 18")
    private Integer ageRestriction;

    @NotBlank(message = "Trailer MUST not be plank")
    private String trailer;

    @NotBlank(message = "Producer MUST not be plank")
    private String producer;

    @NotNull(message = "Genre MUST not be null")
    private List<Long> genreIds;

    @NotNull(message = "Formats MUST not be null")
    private List<Long> formatIds;
}