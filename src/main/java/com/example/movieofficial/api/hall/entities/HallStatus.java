package com.example.movieofficial.api.hall.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "hall_status")
public class HallStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;

    @OneToMany(
            mappedBy = "status",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    private Set<Hall> rooms;
}
