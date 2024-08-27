package com.example.movieofficial.api.hall.entities;

import com.example.movieofficial.api.hall.entities.Seat;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "seats_type")
public class SeatType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    @OneToMany(
            mappedBy = "type",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private Set<Seat> seats;

}
