package com.example.movieofficial.api.movie.entities;

import com.example.movieofficial.api.show.entities.Show;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "formats")
public class Format {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "caption", length = 20, nullable = false)
    private String caption;

    @Column(name = "version", length = 20, nullable = false)
    private String version;

    @OneToMany(
            mappedBy = "format",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private Set<Show> shows;

    @ManyToMany(
            mappedBy = "formats",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    private Set<Movie> movies;
}
