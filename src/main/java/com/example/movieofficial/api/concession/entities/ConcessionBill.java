package com.example.movieofficial.api.concession.entities;

import com.example.movieofficial.api.bill.entities.Bill;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "concession_bill")
public class ConcessionBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "bill_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Bill bill;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "concession_id",
            referencedColumnName = "id",
            nullable = false
    )
    private Concession concession;

    @Column(name = "amount", nullable = false)
    private Long amount;

}
