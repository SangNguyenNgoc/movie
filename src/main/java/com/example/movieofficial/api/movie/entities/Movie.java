package com.example.movieofficial.api.movie.entities;

import com.example.movieofficial.api.show.entities.Show;
import com.example.movieofficial.utils.auditing.AuditorEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "movies")
public class Movie extends AuditorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "sub_name", length = 255, nullable = false)
    @JsonProperty("sub_name")
    private String subName;

    @Column(name = "director", length = 100, nullable = false)
    private String director;

    @Column(name = "performers", length = 255, nullable = false)
    private String performers;

    @Column(name = "release_date", nullable = false)
    @JsonProperty("release_date")
    private LocalDate releaseDate;

    @Column(name = "end_date", nullable = false)
    @JsonProperty("end_date")
    private LocalDate endDate;

    @Column(name = "running_time", nullable = false)
    @JsonProperty("running_time")
    private Integer runningTime;

    @Column(name = "language", nullable = false)
    private String language;

    @Transient
    private Double rating;

    @Column(name = "number_of_ratings", nullable = false)
    @JsonProperty("number_of_ratings")
    private Integer numberOfRatings;

    @Column(name = "sum_of_ratings", nullable = false)
    @JsonProperty("sum_of_ratings")
    private Integer sumOfRatings;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "poster", length = 500, nullable = false)
    private String poster;

    @Column(name = "horizontal_poster", length = 500, nullable = false)
    @JsonProperty("horizontal_poster")
    private String horizontalPoster;

    @Column(name = "trailer", nullable = false)
    private String trailer;

    @Column(name = "age_restriction", nullable = false)
    @JsonProperty("age_restriction")
    private Integer ageRestriction;

    @Column(name = "producer", length = 100, nullable = false)
    private String producer;

    @Column(name = "slug", length = 100, nullable = false)
    private String slug;

    @OneToMany(
            mappedBy = "movie",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private Set<Show> shows;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "movie_format",
            joinColumns = @JoinColumn(name = "movie_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "format_id", nullable = false)
    )
    private Set<Format> formats;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "genre_id", nullable = false)
    )
    private Set<Genre> genres;

    @OneToMany(
            mappedBy = "movie",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private Set<Image> images;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "status_id",
            referencedColumnName = "id",
            nullable = false
    )
    private MovieStatus status;

    public Double getRating() {
        if (numberOfRatings > 0) {
            return Math.round((double) sumOfRatings / numberOfRatings * 10.0) / 10.0;
        } else {
            return 0.0;
        }

    }
}
