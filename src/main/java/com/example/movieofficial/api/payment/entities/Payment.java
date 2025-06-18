package com.example.movieofficial.api.payment.entities;

import com.example.movieofficial.api.bill.entities.Bill;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "expand", length = 500)
    private String expand;

    @Column(name = "image", nullable = false, length = 500)
    private String image;

    @Column(name = "tag", nullable = false, length = 50, unique = true)
    private String tag;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @OneToMany(
            mappedBy = "paymentMethod",
            cascade = CascadeType.PERSIST,
            fetch = FetchType.LAZY
    )
    private List<Bill> bills;

}
