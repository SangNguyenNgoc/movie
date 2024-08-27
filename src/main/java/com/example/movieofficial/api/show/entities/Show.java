package com.example.movieofficial.api.show.entities;

import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.movie.entities.Format;
import com.example.movieofficial.api.movie.entities.Movie;
import com.example.movieofficial.api.ticket.entities.Ticket;
import com.example.movieofficial.utils.auditing.AuditorEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "shows")
public class Show extends AuditorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "start_date", nullable = false)
    @JsonProperty("start_date")
    private LocalDate startDate;

    @Column(name = "start_time", nullable = false)
    @JsonProperty("start_time")
    private LocalTime startTime;

    @Column(name = "running_time", nullable = false)
    @JsonProperty("running_time")
    private Integer runningTime;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "movie_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Movie movie;

    @OneToMany(
            mappedBy = "show",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private Set<Ticket> tickets;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "format_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Format format;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "hall_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Hall hall;

}
