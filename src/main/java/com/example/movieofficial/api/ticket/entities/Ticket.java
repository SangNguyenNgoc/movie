package com.example.movieofficial.api.ticket.entities;

import com.example.movieofficial.api.bill.entities.Bill;
import com.example.movieofficial.api.hall.entities.Seat;
import com.example.movieofficial.api.show.entities.Show;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @Column(name = "ticket_id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "bill_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Bill bill;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "showtime_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Show show;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "seat",
            referencedColumnName = "id",
            nullable = false
    )
    private Seat seat;

    @Transient
    private Boolean stillValid;
}
