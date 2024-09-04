package com.example.movieofficial.api.cinema;

import com.example.movieofficial.api.cinema.dtos.CinemaDetail;
import com.example.movieofficial.api.show.ShowController;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class CinemaModelAssembler {

    public void linkToGetShowDetail(CinemaDetail cinemaDetail) {
        cinemaDetail.getMovies().forEach(movie -> {
            movie.getShows().forEach(show -> {
                show.add(linkTo(methodOn(ShowController.class)
                        .getShowDetail(show.getId()))
                        .withRel("detail")
                        .withType(HttpMethod.GET.name())
                );
            });
        });
    }
}
