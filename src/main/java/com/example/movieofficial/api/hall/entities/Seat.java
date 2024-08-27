package com.example.movieofficial.api.hall.entities;

import com.example.movieofficial.api.ticket.entities.Ticket;
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
@Table(name = "seats")
public class Seat extends AuditorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "row_name", length = 1, nullable = false)
    private String rowName;

    @Column(name = "row_index", nullable = false)
    private Integer rowIndex;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "hall_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Hall hall;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "seat_type_id",
            referencedColumnName = "id",
            nullable = false
    )
    private SeatType type;

    @OneToMany(
            mappedBy = "seat",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private Set<Ticket> tickets;
}
