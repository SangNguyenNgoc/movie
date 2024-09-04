package com.example.movieofficial.api.movie;

import com.example.movieofficial.api.cinema.dtos.CinemaDetail;
import com.example.movieofficial.api.movie.dtos.MovieDetail;
import com.example.movieofficial.api.movie.dtos.MovieInfoAdmin;
import com.example.movieofficial.api.movie.dtos.MovieInfoLanding;
import com.example.movieofficial.api.movie.dtos.StatusInfo;
import com.example.movieofficial.api.show.ShowController;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class MovieModelAssembler {

    public void linkToGetMovieDetail(MovieInfoLanding movie) {
        movie.add(linkTo(methodOn(MovieController.class)
                .getMovieDetail(movie.getSlug()))
                .withRel("detail")
                .withType(HttpMethod.GET.name()));
    }

    public void linkToGetMovieStatus(StatusInfo statusInfo) {
        statusInfo.add(linkTo(MovieController.class)
                .slash(statusInfo.getSlug())
                .withRel(statusInfo.getSlug())
                .withType(HttpMethod.GET.name()));
    }

    public void linkToGetMovieInfoAdmin(MovieInfoAdmin movie) {
        movie.add(linkTo(methodOn(MovieController.class)
                .getById(movie.getId()))
                .withRel("detail")
                .withType(HttpMethod.GET.name()));
    }

    public void linkToGetShowDetail(MovieDetail movieDetail) {
        movieDetail.add(linkTo(methodOn(MovieController.class)
                .getMovieDetail(movieDetail.getSlug()))
                .withSelfRel()
                .withType(HttpMethod.GET.name())
        );

        movieDetail.getCinemas().forEach(cinema -> {
            cinema.getShows().forEach(show -> {
                show.add(linkTo(methodOn(ShowController.class)
                        .getShowDetail(show.getId()))
                        .withRel("detail")
                        .withType(HttpMethod.GET.name())
                );
            });
        });
    }

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
