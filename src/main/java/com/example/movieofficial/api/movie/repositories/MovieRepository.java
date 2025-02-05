package com.example.movieofficial.api.movie.repositories;

import com.example.movieofficial.api.movie.entities.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {
    List<Movie> findByStatusSlugOrderBySumOfRatingsDescReleaseDateAsc(String slug);

    @Query("select m from Movie m order by m.status.id, m.createDate DESC")
    Page<Movie> findAllOrderByStatusIdAscCreateDateDesc(Pageable pageable);

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
    List<Movie> findByStatusIdOrStatusIdAndShowsOrderBySumOfRatingsDesc(
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
    List<Movie> findByStatusIdOrStatusIdAndShowsOrderBySumOfRatingsDesc(
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<Movie> findBySlug(String slug);

    @Transactional
    @Modifying
    @Query(value = "update movies m set m.status_id = 4 where m.end_date < ?1", nativeQuery = true)
    void updateStatusByEndDateBefore(LocalDate endDate);

    @Transactional
    @Modifying
    @Query(value = "update movies m set m.status_id = 2 where m.release_date <= ?1", nativeQuery = true)
    void updateStatusByReleaseDateEqualsOrBefore(LocalDate releaseDate);


    @Query(value = "select m from Movie m " +
            "join fetch m.formats " +
            "where m.releaseDate < ?1 " +
            "and m.endDate > ?1")
    List<Movie> findByDate(LocalDate date);

    @Query("select mv from Movie mv " +
            "where mv.slug LIKE %:input% " +
            "and (mv.status.id = 1 or mv.status.id = 2)")
    List<Movie> searchBySlug(@Param(value = "input") String input);

    @Query(value = "select EXISTS(select 1 from movie_genre where genre_id = ?1 and movie_id = ?2)", nativeQuery = true)
    boolean existsByGenreAndId(Long genreId, String movieId);

    @Transactional
    @Modifying
    @Query(value = "insert into movie_genre value (?2, ?1)", nativeQuery = true)
    void addGenre(Long genreId, String movieId);

    @Transactional
    @Modifying
    @Query(value = "delete from movie_genre where genre_id = ?1 and movie_id = ?2", nativeQuery = true)
    void deleteGenre(Long genreId, String movieId);

    @Query(value = "select EXISTS(select 1 from movie_format where format_id = ?1 and movie_id = ?2)", nativeQuery = true)
    boolean existsByFormatAndId(Long formatId, String movieId);

    @Transactional
    @Modifying
    @Query(value = "insert into movie_format value (?2, ?1)", nativeQuery = true)
    void addFormat(Long genreId, String movieId);

    @Transactional
    @Modifying
    @Query(value = "delete from movie_format where format_id = ?1 and movie_id = ?2", nativeQuery = true)
    void deleteFormat(Long formatId, String movieId);

    @Modifying
    @Query("UPDATE Movie m SET m.sumOfRatings = m.sumOfRatings + :rating, " +
            "m.numberOfRatings = m.numberOfRatings + 1 WHERE m.slug = :slug")
    void updateRating(@Param("slug") String slug, @Param("rating") Integer rating);

}
