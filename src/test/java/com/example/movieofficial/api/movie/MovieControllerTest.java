package com.example.movieofficial.api.movie;

import com.example.movieofficial.api.movie.dtos.MovieDetail;
import com.example.movieofficial.api.movie.dtos.MovieInfoAdmin;
import com.example.movieofficial.api.movie.dtos.MovieInfoLanding;
import com.example.movieofficial.api.movie.dtos.StatusInfo;
import com.example.movieofficial.api.movie.interfaces.MovieService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(controllers = MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Test
    void getMoviesToLanding() throws Exception {
        Mockito.when(movieService.getMovieToLandingFromRedis()).thenReturn(List.of(
                new StatusInfo(),
                new StatusInfo(),
                new StatusInfo()
        ));

        ResultActions response = mockMvc.perform(get("/api/v1/movies/home"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(3)));
    }

    @Test
    void getMoviesShowingNow() throws Exception {
        Mockito.when(movieService.getMoviesByStatusFromRedis("showing-now", 0, 5)).thenReturn(List.of(
                new MovieInfoLanding(),
                new MovieInfoLanding(),
                new MovieInfoLanding(),
                new MovieInfoLanding(),
                new MovieInfoLanding()
        ));

        ResultActions response = mockMvc.perform(get("/api/v1/movies/showing-now")
                .param("page","1")
                .param("size", "5"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size", CoreMatchers.is(5)));
    }

    @Test
    void getMoviesByComingSoon() throws Exception {
        Mockito.when(movieService.getMoviesByStatusFromRedis("coming-soon", 0, 5)).thenReturn(List.of(
                new MovieInfoLanding(),
                new MovieInfoLanding(),
                new MovieInfoLanding(),
                new MovieInfoLanding(),
                new MovieInfoLanding()
        ));

        ResultActions response = mockMvc.perform(get("/api/v1/movies/coming-soon")
                .param("page","1")
                .param("size", "5"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size", CoreMatchers.is(5)));

    }

    @Test
    void getAll() throws Exception {
        Mockito.when(movieService.getAll(0, 5)).thenReturn(List.of(
                new MovieInfoAdmin(),
                new MovieInfoAdmin(),
                new MovieInfoAdmin(),
                new MovieInfoAdmin(),
                new MovieInfoAdmin()
        ));

        ResultActions response = mockMvc.perform(get("/api/v1/movies")
                .param("page","1")
                .param("size", "5"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size", CoreMatchers.is(5)));
    }

    @Test
    void getById() throws Exception {

        MovieInfoAdmin data = new MovieInfoAdmin();
        data.setName("Test movie");
        data.setId("1234567");

        Mockito.when(movieService.getById("1234567")).thenReturn(data);

        ResultActions response = mockMvc.perform(get("/api/v1/movies/1234567"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(data.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(data.getId())));
    }

    @Test
    void getMovieDetail() throws Exception {
        MovieDetail data = new MovieDetail();
        data.setId("1234567");
        data.setName("The Dark Night");
        data.setSlug("the-dark-night");

        Mockito.when(movieService.getMovieAndShowsFromRedis(data.getSlug())).thenReturn(data);

        ResultActions response = mockMvc.perform(get("/api/v1/movies/the-dark-night/shows"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(data.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(data.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.slug", CoreMatchers.is(data.getSlug())));
        ;
    }
}