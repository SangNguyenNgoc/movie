package com.example.movieofficial.api.cinema.dtos;

import com.example.movieofficial.api.movie.dtos.MovieAndShows;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.example.movieofficial.api.cinema.entities.Cinema}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CinemaDetail implements Serializable {
    private String id;
    private String name;
    private String slug;
    private String address;
    private String description;
    private String phoneNumber;
    private List<MovieAndShows> movies;
}