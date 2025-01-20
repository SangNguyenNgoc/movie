package com.example.movieofficial.api.movie.interfaces;

import com.example.movieofficial.MovieOfficialApplication;
import com.example.movieofficial.api.cinema.entities.Cinema;
import com.example.movieofficial.api.cinema.interfaces.CinemaMapper;
import com.example.movieofficial.api.cinema.interfaces.CinemaRepository;
import com.example.movieofficial.api.movie.services.impl.DefaultMovieService;
import com.example.movieofficial.api.movie.dtos.MovieDetail;
import com.example.movieofficial.api.movie.dtos.MovieInfoAdmin;
import com.example.movieofficial.api.movie.dtos.MovieInfoLanding;
import com.example.movieofficial.api.movie.dtos.StatusInfo;
import com.example.movieofficial.api.movie.entities.Movie;
import com.example.movieofficial.api.movie.entities.MovieStatus;
import com.example.movieofficial.api.movie.mappers.MovieMapper;
import com.example.movieofficial.api.movie.repositories.MovieRepository;
import com.example.movieofficial.api.movie.repositories.MovieStatusRepository;
import com.example.movieofficial.api.show.interfaces.ShowMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private MovieStatusRepository movieStatusRepository;
    @Mock
    private CinemaRepository cinemaRepository;
    @Spy
    private MovieMapper movieMapper = Mappers.getMapper(MovieMapper.class);
    @Spy
    private CinemaMapper cinemaMapper = Mappers.getMapper(CinemaMapper.class);
    @Spy
    private ShowMapper showMapper = Mappers.getMapper(ShowMapper.class);
    @InjectMocks
    private DefaultMovieService movieService;
    private List<Movie> movies = new ArrayList<>();
    private List<MovieStatus> movieStatusList = new ArrayList<>();
    private List<Cinema> cinemaList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE));
        objectMapper.registerModule(javaTimeModule);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            InputStream movieStatusJson = MovieOfficialApplication.class.getResourceAsStream("/data/json/movieservice/movies_status.json");
            movieStatusList = objectMapper.readValue(movieStatusJson, new TypeReference<List<MovieStatus>>() {
            });

            InputStream moviesJson = MovieOfficialApplication.class.getResourceAsStream("/data/json/movieservice/movies.json");
            movies = objectMapper.readValue(moviesJson, new TypeReference<List<Movie>>() {
            });

            InputStream cinemasJson = MovieOfficialApplication.class.getResourceAsStream("/data/json/movieservice/cinemas.json");
            cinemaList = objectMapper.readValue(cinemasJson, new TypeReference<List<Cinema>>() {
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getMovieToLanding() {
        Mockito.when(movieStatusRepository.findByIdOrId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(movieStatusList.subList(0, 2));

        List<StatusInfo> result = movieService.getMovieToLanding();

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void getMoviesByStatus() {
        Mockito.when(
                movieRepository.findByStatusSlugOrderBySumOfRatingsDescReleaseDateAsc(Mockito.anyString())
        ).thenReturn(movies.stream().filter(movie -> movie.getStatus().getSlug().equals("coming-soon")).toList());

        List<MovieInfoLanding> result = movieService.getMoviesByStatus("coming-soon");

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void getAll() {
        var page = PageRequest.of(0, 5);
        Mockito.when(
                movieRepository.findAllOrderByStatusIdAscCreateDateDesc(Mockito.any(Pageable.class))
        ).thenReturn(new PageImpl<>(movies, page, movies.size()));

        List<MovieInfoAdmin> result = movieService.getAll(0, 5).getData();

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result.size()).isEqualTo(8);
    }

    @Test
    void getById() {
        UUID uuid = UUID.randomUUID();

        Mockito.when(movieRepository.findById(uuid.toString())).thenReturn(Optional.of(movies.stream().toList().get(0)));

        MovieInfoAdmin result = movieService.getById(uuid.toString());
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo("Challengers");
    }

    @Test
    void getMovieAndShows() {
        Mockito.when(
                movieRepository.findBySlug("paw-patrol-the-mighty-movie")
        ).thenReturn(Optional.ofNullable(movies.stream().toList().get(6)));
        Mockito.when(
                cinemaRepository.findByStatusIdOrderByCreateDateAsc(
                        Mockito.anyString(),
                        Mockito.any(LocalDate.class),
                        Mockito.any(LocalDate.class)
                )
        ).thenReturn(cinemaList);

        MovieDetail result = movieService.getMovieAndShows("paw-patrol-the-mighty-movie");

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getCinemas().size()).isEqualTo(3);
        Assertions.assertThat(result.getCinemas().get(0).getShows())
                .isSortedAccordingTo((show1, show2) -> {
                            int ratingComparison = show1.getStartDate().compareTo(show2.getStartDate());
                            if (ratingComparison == 0) {
                                return show1.getStartTime().compareTo(show2.getStartTime());
                            }
                            return ratingComparison;
                        }
                );
    }

    @Test
    void getAllMoviesAndShows() {
        Mockito.when(
                movieRepository.findByStatusIdOrStatusIdOrderBySumOfRatingsDesc(Mockito.anyLong(), Mockito.anyLong())
        ).thenReturn(movies.stream().toList().subList(0, 3));
        Mockito.when(
                cinemaRepository.findByStatusIdOrderByCreateDateAsc(
                        Mockito.anyString(),
                        Mockito.any(LocalDate.class),
                        Mockito.any(LocalDate.class)
                )
        ).thenReturn(cinemaList);

        List<MovieDetail> result = movieService.getAllMoviesAndShows();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.get(0).getCinemas().size()).isEqualTo(3);
        Assertions.assertThat(result.get(0).getCinemas().get(0).getShows())
                .isSortedAccordingTo((show1, show2) -> {
                            int ratingComparison = show1.getStartDate().compareTo(show2.getStartDate());
                            if (ratingComparison == 0) {
                                return show1.getStartTime().compareTo(show2.getStartTime());
                            }
                            return ratingComparison;
                        }
                );
    }
}