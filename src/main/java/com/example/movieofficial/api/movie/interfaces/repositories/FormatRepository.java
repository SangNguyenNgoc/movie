package com.example.movieofficial.api.movie.interfaces.repositories;

import com.example.movieofficial.api.movie.entities.Format;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FormatRepository extends JpaRepository<Format, Long> {
    @Query("select (count(f) > 0) from Format f inner join f.movies movies where f.id = ?1 and movies.id = ?2")
    boolean existsByIdAndMoviesId(Long id, String id1);

}