package com.example.movieofficial.api.movie.services;

import com.example.movieofficial.api.cinema.dtos.CinemaAndShows;
import com.example.movieofficial.api.cinema.entities.Cinema;
import com.example.movieofficial.api.cinema.interfaces.CinemaMapper;
import com.example.movieofficial.api.cinema.interfaces.CinemaRepository;
import com.example.movieofficial.api.movie.dtos.MovieDetail;
import com.example.movieofficial.api.movie.dtos.MovieInfoAdmin;
import com.example.movieofficial.api.movie.dtos.MovieInfoLanding;
import com.example.movieofficial.api.movie.dtos.StatusInfo;
import com.example.movieofficial.api.movie.entities.Movie;
import com.example.movieofficial.api.movie.entities.MovieStatus;
import com.example.movieofficial.api.movie.interfaces.mappers.MovieMapper;
import com.example.movieofficial.api.movie.interfaces.repositories.MovieRepository;
import com.example.movieofficial.api.movie.interfaces.services.MovieService;
import com.example.movieofficial.api.movie.interfaces.repositories.MovieStatusRepository;
import com.example.movieofficial.api.show.interfaces.ShowMapper;
import com.example.movieofficial.utils.dtos.PageResponse;
import com.example.movieofficial.utils.exceptions.DataNotFoundException;
import com.example.movieofficial.utils.services.RedisService;
import com.example.movieofficial.utils.services.UtilsService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultMovieService implements MovieService {

    private final MovieStatusRepository movieStatusRepository;
    private final MovieRepository movieRepository;
    private final CinemaRepository cinemaRepository;
    private final MovieMapper movieMapper;
    private final CinemaMapper cinemaMapper;
    private final ShowMapper showMapper;
    private final RedisService<MovieDetail> redisMovieDetail;
    private final RedisService<List<StatusInfo>> redisStatusInfo;
    private final RedisService<List<MovieInfoLanding>> redisMovieInfo;
    private final UtilsService utils;

    @Value("${show.showing-before-day}")
    private Integer showBeforeDay;

    @Override
    public List<StatusInfo> getMovieToLanding() {
        List<MovieStatus> movieStatusList = movieStatusRepository.findByIdOrId(1L, 2L);
        return movieStatusList.stream()
                .map(movieStatus -> {
                    StatusInfo statusAndMovie = movieMapper.toStatusInfo(movieStatus);
                    List<MovieInfoLanding> movieInfoLandings = statusAndMovie.getMovies();
                    if (movieInfoLandings != null) {
                        var moviesAfterSort = movieInfoLandings.stream()
                                .limit(5)
                                .sorted(Comparator.comparing(MovieInfoLanding::getReleaseDate))
                                .collect(Collectors.toList());
                        statusAndMovie.setMovies(moviesAfterSort);
                    }
                    return statusAndMovie;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StatusInfo> getMovieToLandingFromRedis() {
        List<StatusInfo> movieStatusList = redisStatusInfo.getValue("movies", new TypeReference<List<StatusInfo>>() {
        });
        if (movieStatusList == null) {
            movieStatusList = getMovieToLanding();
            redisStatusInfo.setValue("movies", movieStatusList);
        }
        return movieStatusList;
    }

    @Override
    public List<MovieInfoLanding> getMoviesByStatus(String slug) {
        List<Movie> movies = movieRepository.findByStatusSlugOrderBySumOfRatingsDescReleaseDateAsc(slug);
        return movies.stream().map(movieMapper::toInfoLanding).collect(Collectors.toList());
    }

    @Override
    public PageResponse<MovieInfoLanding> getMoviesByStatusFromRedis(String slug, Integer page, Integer size) {
        List<MovieInfoLanding> movieInfoLandings = redisMovieInfo.getValue(slug, new TypeReference<List<MovieInfoLanding>>() {
        });
        if (movieInfoLandings == null) {
            movieInfoLandings = getMoviesByStatus(slug);
            redisMovieInfo.setValue(slug, movieInfoLandings);
        }
        int fromIndex = page * size;
        int totalItem = movieInfoLandings.size();
        int toIndex = Math.min(fromIndex + size, totalItem);
        return PageResponse.<MovieInfoLanding>builder()
                .data(movieInfoLandings.subList(fromIndex, toIndex))
                .totalPages((totalItem + size - 1) / size)
                .build();
    }

    @Override
    public PageResponse<MovieInfoAdmin> getAll(Integer page, Integer size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Movie> movies = movieRepository.findAllOrderByStatusIdAscCreateDateDesc(pageable);
        var data = movies.stream().map(movieMapper::toInfoAdmin).collect(Collectors.toList());
        return PageResponse.<MovieInfoAdmin>builder()
                .data(data)
                .totalPages(movies.getTotalPages())
                .build();
    }

    @Override
    public MovieInfoAdmin getById(String id) {
        Movie movie = movieRepository.findById(id).orElseThrow(
                () -> new DataNotFoundException("Not found", List.of("Movie not found!"))
        );
        return movieMapper.toInfoAdmin(movie);
    }

    @Override
    public MovieDetail getMovieAndShows(String slug) {
        Movie movie = movieRepository.findBySlug(slug).orElseThrow(
                () -> new DataNotFoundException("Not found", List.of("Movie not found!"))
        );
        return addShowsToMovie(movie);
    }

    @Override
    public List<MovieDetail> getAllMoviesAndShows() {
        List<Movie> movies = movieRepository.findByStatusIdOrStatusIdOrderBySumOfRatingsDesc(1L, 2L);
        return movies.stream().map(this::addShowsToMovie).toList();
    }

    public MovieDetail addShowsToMovie(Movie movie) {
        MovieDetail movieDetail = movieMapper.toDetail(movie);
        List<Cinema> cinemas = cinemaRepository.findByStatusIdOrderByCreateDateAsc(
                movie.getSlug(),
                LocalDate.now(),
                LocalDate.now().plusDays(showBeforeDay)
        );
        List<CinemaAndShows> cinemaAndShows = cinemas.stream()
                .map(cinema -> {
                    CinemaAndShows result = cinemaMapper.toCinemaAndShows(cinema);
                    List<CinemaAndShows.ShowDto> shows = cinema.getHalls().stream()
                            .flatMap(hall -> hall.getShows().stream())
                            .map(showMapper::toDtoInCinema)
                            .sorted(Comparator.comparing(CinemaAndShows.ShowDto::getStartDate)
                                    .thenComparing(CinemaAndShows.ShowDto::getStartTime)
                            )
                            .collect(Collectors.toList());
                    result.setShows(shows);
                    return result;
                })
                .collect(Collectors.toList());
        movieDetail.setCinemas(cinemaAndShows);
        return movieDetail;
    }

    @Override
    public MovieDetail getMovieAndShowsFromRedis(String slug) {
        MovieDetail movieDetail = redisMovieDetail.getValue(
                "movie_detail:" + slug, new TypeReference<MovieDetail>() {
                });
        if (movieDetail == null) {
            movieDetail = getMovieAndShows(slug);
            redisMovieDetail.setValue("movie_detail:" + slug, movieDetail);

        }
        movieDetail.getCinemas().forEach(cinemaAndShows -> {
            Iterator<CinemaAndShows.ShowDto> iterator = cinemaAndShows.getShows().iterator();
            while (iterator.hasNext()) {
                CinemaAndShows.ShowDto show = iterator.next();
                LocalDateTime showTime = LocalDateTime.of(show.getStartDate(), show.getStartTime());
                if (showTime.isBefore(LocalDateTime.now())) {
                    iterator.remove();
                } else {
                    break;
                }
            }
        });
        return movieDetail;
    }

    @Override
    @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Ho_Chi_Minh")
//    @EventListener(ApplicationReadyEvent.class)
    @Async
    @Transactional
    public void cacheAllMoviesCinemasShows() {
        redisMovieDetail.deleteKeysWithPrefix("movie_detail:");
        List<MovieDetail> movieDetails = getAllMoviesAndShows();
        movieDetails.forEach(movieDetail ->
                redisMovieDetail.setValue("movie_detail:" + movieDetail.getSlug(), movieDetail)
        );
    }

    @Override
    @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Ho_Chi_Minh")
//    @EventListener(ApplicationReadyEvent.class)
    @Async
    @Transactional
    public void cacheAllMoviesToLanding() {
        List<StatusInfo> movieStatusList = getMovieToLanding();
        redisStatusInfo.setValue("movies", movieStatusList);

        List<MovieInfoLanding> comingSoon = getMoviesByStatus("coming-soon");
        redisMovieInfo.setValue("coming-soon", comingSoon);

        List<MovieInfoLanding> showingNow = getMoviesByStatus("showing-now");
        redisMovieInfo.setValue("showing-now", showingNow);

    }

    @Override
    @Scheduled(cron = "0 0 3 * * ?", zone = "Asia/Ho_Chi_Minh")
//    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void updateMovieStatus() {
        LocalDate now = LocalDate.now();
        movieRepository.updateStatusByEndDateBefore(now);
        movieRepository.updateStatusByReleaseDateEqualsOrBefore(now);
    }

    @Override
    public List<MovieInfoLanding> searchMoviesBySlug(String search) {
        var slug = utils.toSlug(search);
        var movies = movieRepository.searchBySlug(slug);
        return movies.stream().map(movieMapper::toInfoLanding).collect(Collectors.toList());
    }
}
