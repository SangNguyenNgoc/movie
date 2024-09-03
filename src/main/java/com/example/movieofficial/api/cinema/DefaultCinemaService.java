package com.example.movieofficial.api.cinema;

import com.example.movieofficial.api.cinema.dtos.CinemaDetail;
import com.example.movieofficial.api.cinema.dtos.CinemaInfo;
import com.example.movieofficial.api.cinema.dtos.CinemaInfoLanding;
import com.example.movieofficial.api.cinema.entities.Cinema;
import com.example.movieofficial.api.cinema.interfaces.CinemaMapper;
import com.example.movieofficial.api.cinema.interfaces.CinemaRepository;
import com.example.movieofficial.api.cinema.interfaces.CinemaService;
import com.example.movieofficial.api.movie.dtos.MovieAndShows;
import com.example.movieofficial.api.movie.entities.Movie;
import com.example.movieofficial.api.movie.interfaces.MovieMapper;
import com.example.movieofficial.api.movie.interfaces.MovieRepository;
import com.example.movieofficial.api.show.interfaces.ShowMapper;
import com.example.movieofficial.utils.exceptions.DataNotFoundException;
import com.example.movieofficial.utils.services.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultCinemaService implements CinemaService {

    CinemaRepository cinemaRepository;
    MovieRepository movieRepository;
    CinemaMapper cinemaMapper;
    MovieMapper movieMapper;
    ShowMapper showMapper;
    RedisService<List<CinemaDetail>> redisCinemaDetails;

    @Override
    public List<CinemaInfo> getAll() {
        List<Cinema> cinemas = cinemaRepository.findByOrderByCreateDateAsc();
        return cinemas.stream().map(cinemaMapper::toInfo).collect(Collectors.toList());
    }

    @Override
    public List<CinemaInfo> getAll(Integer page, Integer size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<Cinema> cinemas = cinemaRepository.findByOrderByCreateDateAsc(pageable);
        return cinemas.stream().map(cinemaMapper::toInfo).collect(Collectors.toList());
    }

    @Override
    public CinemaInfo getById(String id) {
        Cinema cinema = cinemaRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Not found", List.of("Cinema not found!"))
        );
        return cinemaMapper.toInfo(cinema);
    }

    @Override
    @Cacheable(value = "cinemas", key = "'landing-page'")
    public List<CinemaInfoLanding> getCinemaForLanding() {
        List<Cinema> cinemas = cinemaRepository.findByStatusId();
        return cinemas.stream().map(cinemaMapper::toInfoLanding).collect(Collectors.toList());
    }

    @Override
    public CinemaDetail getCinemaAndShows(String slug) {
        Cinema cinema = cinemaRepository.findBySlugAndStatusId(slug).orElseThrow(
                () -> new DataNotFoundException("Not found", List.of("Cinema not found!"))
        );
        CinemaDetail cinemaDetail = cinemaMapper.toDetail(cinema);
        List<Movie> movies = movieRepository.findByStatusIdOrStatusIdAndShowsOrderBySumOfRatingsDesc(
                slug,
                LocalDate.now(),
                LocalDate.now().plusDays(3)
        );
        List<MovieAndShows> movieAndShows = movies.stream()
                .map(movie -> {
                    MovieAndShows result = movieMapper.toMovieAndShows(movie);
                    List<MovieAndShows.ShowDto> showAfterSort = result.getShows().stream()
                            .sorted(Comparator.comparing(MovieAndShows.ShowDto::getStartDate)
                                    .thenComparing(MovieAndShows.ShowDto::getStartTime))
                            .collect(Collectors.toList());
                    result.setShows(showAfterSort);
                    return result;
                })
                .collect(Collectors.toList());
        cinemaDetail.setMovies(movieAndShows);
        return cinemaDetail;
    }

    @Override
    public List<CinemaDetail> getAllCinemaAndShows() {
        List<Cinema> cinemas = cinemaRepository.findByStatusId();
        List<Movie> movies = movieRepository.findByStatusIdOrStatusIdAndShowsOrderBySumOfRatingsDesc(
                LocalDate.now(),
                LocalDate.now().plusDays(3)
        );
        return cinemas.stream()
                .map(cinema -> {
                    CinemaDetail cinemaDetail = cinemaMapper.toDetail(cinema);
                    List<MovieAndShows> movieAndShows = movies.stream()
                            .map(movie -> {
                                List<MovieAndShows.ShowDto> showDtos = movie.getShows().stream()
                                        .filter(item -> item.getHall().getCinema().getSlug().equals(cinema.getSlug()))
                                        .map(showMapper::toShowDtoInMovieAndShows)
                                        .sorted(Comparator.comparing(MovieAndShows.ShowDto::getStartDate)
                                                .thenComparing(MovieAndShows.ShowDto::getStartTime))
                                        .collect(Collectors.toList());
                                if (!showDtos.isEmpty()) {
                                    MovieAndShows result = movieMapper.toMovieAndShows(movie);
                                    result.setShows(showDtos);
                                    return Optional.of(result);
                                }
                                return Optional.<MovieAndShows>empty();
                            })
                            .flatMap(Optional::stream)
                            .collect(Collectors.toList());
                    cinemaDetail.setMovies(movieAndShows);
                    return cinemaDetail;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CinemaDetail> getAllCinemaAndShowsFromRedis() {
        List<CinemaDetail> cinemaDetails = redisCinemaDetails.getValue("cinemas_movies_shows", new TypeReference<List<CinemaDetail>>() {
        });
        if (cinemaDetails == null) {
            cinemaDetails = getAllCinemaAndShows();
            redisCinemaDetails.setValue("cinemas_movies_shows", cinemaDetails);
        }
        cinemaDetails.forEach(cinemaDetail -> cinemaDetail.getMovies().forEach(movieAndShows -> {
            Iterator<MovieAndShows.ShowDto> iterator = movieAndShows.getShows().iterator();
            while (iterator.hasNext()) {
                MovieAndShows.ShowDto show = iterator.next();
                LocalDateTime showTime = LocalDateTime.of(show.getStartDate(), show.getStartTime());
                if (showTime.isBefore(LocalDateTime.now())) {
                    iterator.remove();
                } else {
                    break;
                }
            }
        }));
        return cinemaDetails;
    }

    @Override
    @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Ho_Chi_Minh")
//    @EventListener(ApplicationReadyEvent.class)
    @Async
    @Transactional
    public void cacheAllCinemasMoviesShows() {
        List<CinemaDetail> cinemaDetails = getAllCinemaAndShows();
        redisCinemaDetails.setValue("cinemas_movies_shows", cinemaDetails);
    }
}
