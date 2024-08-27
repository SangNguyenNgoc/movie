package com.example.movieofficial.api.movie.interfaces;

import com.example.movieofficial.api.movie.entities.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {
    List<Movie> findByStatusSlugOrderBySumOfRatingsDescReleaseDateAsc(String slug);

    @Query("select m from Movie m order by m.status.id, m.createDate DESC")
    List<Movie> findAllOrderByStatusIdAscCreateDateDesc(Pageable pageable);

    @Query("select m from Movie m " +
            "where m.status.id = ?1 or m.status.id = ?2 " +
            "order by m.sumOfRatings desc")
    List<Movie> findByStatusIdOrStatusIdOrderBySumOfRatingsDesc(Long id, Long id1);

    @Query("""
            select m from Movie m
            join fetch m.shows s
            join s.hall h
            where (m.status.id = 1 or m.status.id = 2)
            and s.startDate between ?2 and ?3
            and h.cinema.slug = ?1
            order by m.sumOfRatings desc""")
    List<Movie> findByStatusIdOrStatusIdAndShowsStatusTrueOrderBySumOfRatingsDesc(
            String slug,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
            select m from Movie m
            join fetch m.shows s
            join fetch s.hall h
            join fetch h.cinema
            where (m.status.id = 1 or m.status.id = 2)
            and s.startDate between ?1 and ?2
            order by m.sumOfRatings desc""")
    List<Movie> findByStatusIdOrStatusIdAndShowsStatusTrueOrderBySumOfRatingsDesc(
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<Movie> findBySlug(String slug);
}
