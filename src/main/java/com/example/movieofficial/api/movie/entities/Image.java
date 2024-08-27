package com.example.movieofficial.api.movie.entities;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "path", length = 500, nullable = false)
    private String path;

    @Column(name = "extension", length = 10, nullable = false)
    private String extension;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "movie_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Movie movie;
}
