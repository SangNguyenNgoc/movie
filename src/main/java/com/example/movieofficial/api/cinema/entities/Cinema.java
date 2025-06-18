package com.example.movieofficial.api.cinema.entities;

import com.example.movieofficial.api.concession.entities.Concession;
import com.example.movieofficial.api.hall.entities.Hall;
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
@Table(name = "cinemas")
public class Cinema extends AuditorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "phone_number")
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "status_id",
            nullable = false,
            referencedColumnName = "id"
    )
    private CinemaStatus status;

    @OneToMany(
            mappedBy = "cinema",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private Set<Hall> halls;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "cinema_concession",
            joinColumns = @JoinColumn(name = "cinema_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "concession_id", nullable = false)
    )
    private Set<Concession> concessions;


}
