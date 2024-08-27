package com.example.movieofficial.api.cinema.interfaces;

import com.example.movieofficial.api.cinema.entities.Cinema;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, String> {
    @Query("select c from Cinema c where c.status.id = 1")
    List<Cinema> findByStatusId();


    @Query("select c from Cinema c where c.slug = ?1 and c.status.id = 1")
    Optional<Cinema> findBySlugAndStatusId(String slug);


    @Query("select c from Cinema c order by c.createDate")
    List<Cinema> findByOrderByCreateDateAsc();

    @Query("""
            select c from Cinema c
            join fetch c.halls h
            join fetch h.shows s
            where c.status.id = 1
            and s.movie.slug = ?1
            and s.startDate between ?2 and ?3
            order by c.createDate""")
    List<Cinema> findByStatusIdOrderByCreateDateAsc(String slug, LocalDate startDate, LocalDate endDate);

    @Query("select c from Cinema c order by c.createDate")
    List<Cinema> findByOrderByCreateDateAsc(Pageable pageable);

}