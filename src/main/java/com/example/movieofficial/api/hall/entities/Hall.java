package com.example.movieofficial.api.hall.entities;

import com.example.movieofficial.api.cinema.entities.Cinema;
import com.example.movieofficial.api.show.entities.Show;
import com.example.movieofficial.utils.auditing.AuditorEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "halls")
public class Hall extends AuditorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @OneToMany(
            mappedBy = "hall",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private Set<Show> shows;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "cinema_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Cinema cinema;

    @OneToMany(
            mappedBy = "hall",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    private Set<Seat> seats;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "status_id",
            referencedColumnName = "id",
            nullable = false
    )
    private HallStatus status;


}
