package com.example.movieofficial.api.movie.interfaces;

import com.example.movieofficial.api.movie.entities.MovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieStatusRepository extends JpaRepository<MovieStatus, Long> {
    @Query("select m from MovieStatus m " +
            "left join fetch m.movies mv " +
            "left join fetch mv.formats " +
            "left join fetch mv.genres " +
            "where m.id = ?1 or m.id = ?2 " +
            "order by m.id")
    List<MovieStatus> findByIdOrId(Long id, Long id1);

}
