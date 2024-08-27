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
@Table(name = "movies_status")
public class MovieStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description", length = 50, nullable = false)
    private String description;

    @Column(name = "slug", length = 20, nullable = false)
    private String slug;

    @OneToMany(
            mappedBy = "status",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    private Set<Movie> movies;

}
