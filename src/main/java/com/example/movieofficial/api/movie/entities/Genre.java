package com.example.movieofficial.api.movie.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @ManyToMany(
            mappedBy = "genres",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    private Set<Movie> movies;

}
