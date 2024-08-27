package com.example.movieofficial.api.movie.interfaces;

import com.example.movieofficial.api.movie.entities.Movie;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
@Sql(scripts = "/data/insertsql/movie-repo-testdata.sql")
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void findByStatusSlugOrderBySumOfRatingsDescReleaseDateAsc() {
        List<Movie> comingSoon = movieRepository.findByStatusSlugOrderBySumOfRatingsDescReleaseDateAsc("coming-soon");
        Assertions.assertThat(comingSoon).isNotEmpty();
        Assertions.assertThat(comingSoon.size()).isEqualTo(2);
        Assertions.assertThat(comingSoon).isSortedAccordingTo((movie1, movie2) -> {
            int ratingComparison = Integer.compare(movie2.getSumOfRatings(), movie1.getSumOfRatings());
            if (ratingComparison == 0) {
                return movie1.getReleaseDate().compareTo(movie2.getReleaseDate());
            }
            return ratingComparison;
        });

        List<Movie> showingNow = movieRepository.findByStatusSlugOrderBySumOfRatingsDescReleaseDateAsc("showing-now");
        Assertions.assertThat(showingNow).isNotEmpty();
        Assertions.assertThat(showingNow.size()).isEqualTo(6);
        Assertions.assertThat(showingNow).isSortedAccordingTo((movie1, movie2) -> {
            int ratingComparison = Integer.compare(movie2.getSumOfRatings(), movie1.getSumOfRatings());
            if (ratingComparison == 0) {
                return movie1.getReleaseDate().compareTo(movie2.getReleaseDate());
            }
            return ratingComparison;
        });
    }

    @Test
    void findAllOrderByStatusIdAscCreateDateDesc() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Movie> comingSoon = movieRepository.findAllOrderByStatusIdAscCreateDateDesc(pageable);
        Assertions.assertThat(comingSoon).isNotEmpty();
        Assertions.assertThat(comingSoon.size()).isEqualTo(2);
        Assertions.assertThat(comingSoon).isSortedAccordingTo((movie1, movie2) -> {
            int ratingComparison = Integer.compare(Math.toIntExact(movie1.getStatus().getId()), Math.toIntExact(movie2.getStatus().getId()));
            if (ratingComparison == 0) {
                return movie2.getCreateDate().compareTo(movie1.getCreateDate());
            }
            return ratingComparison;
        });
    }

    @Test
    void findByStatusIdOrStatusIdOrderBySumOfRatingsDesc() {
        List<Movie> comingSoon = movieRepository.findByStatusIdOrStatusIdOrderBySumOfRatingsDesc(1L, 2L);

        Assertions.assertThat(comingSoon).isNotEmpty();
        Assertions.assertThat(comingSoon.size()).isEqualTo(8);
        Assertions.assertThat(comingSoon).isSortedAccordingTo((movie1, movie2) ->
                Integer.compare(Math.toIntExact(movie2.getSumOfRatings()), Math.toIntExact(movie1.getSumOfRatings())));
    }

    @Test
    void findByStatusIdOrStatusIdAndShowsStatusTrueOrderBySumOfRatingsDesc() {
        List<Movie> comingSoon = movieRepository.findByStatusIdOrStatusIdAndShowsStatusTrueOrderBySumOfRatingsDesc(
                LocalDate.now(), LocalDate.now()
        );
        Assertions.assertThat(comingSoon).isEmpty();
    }


    @Test
    void findBySlug() {
        Movie movieBySlug = movieRepository.findBySlug("past-lives").get();
        Assertions.assertThat(movieBySlug).isNotNull();
    }
}