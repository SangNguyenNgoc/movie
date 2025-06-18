package com.example.movieofficial.api.concession.entities;

import com.example.movieofficial.api.cinema.entities.Cinema;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "concessions")
public class Concession {

    @Id
    @Column(name = "id", nullable = false, length = 60)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "image", nullable = false, length = 500)
    private String image;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "max_quantity", nullable = false)
    private Long maxQuantity;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @ManyToMany(
            mappedBy = "concessions",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    private Set<Cinema> cinemas;

    @OneToMany(
            mappedBy = "concession",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    private Set<ConcessionBill> concessionBills;

}
