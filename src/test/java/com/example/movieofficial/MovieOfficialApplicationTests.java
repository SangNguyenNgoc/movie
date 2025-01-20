package com.example.movieofficial;

import com.example.movieofficial.api.movie.repositories.MovieRepository;
import com.example.movieofficial.config.MySQLContainerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest
@Import(MySQLContainerConfig.class)
class MovieOfficialApplicationTests {

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    MySQLContainer<?> mysqlContainer;

    @BeforeEach
    void setUp() {
        mysqlContainer.withInitScript("data/insertmysql/movie-repo-testdata.sql");
    }

    @Test
    void contextLoads() {
        var movies = movieRepository.findAll();
        System.out.println(movies.size());
    }

}
