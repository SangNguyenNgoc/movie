package com.example.movieofficial.api.bill.entities;

import com.example.movieofficial.api.ticket.entities.Ticket;
import com.example.movieofficial.api.user.entities.User;
import com.example.movieofficial.utils.auditing.AuditorEntity;
import com.example.movieofficial.utils.auditing.TimestampEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "bills")
public class Bill extends TimestampEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "payment_at", nullable = true)
    private LocalDateTime paymentAt;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @Column(name = "total", nullable = false)
    private Long total;

    @Column(name = "payment_url", columnDefinition = "TEXT", nullable = false)
    private String paymentUrl;

    @Column(name = "failure_reason", nullable = true)
    private String failureReason;

    @Column(name = "failure_at", nullable = true)
    private LocalDateTime failureAt;

    @Column(name = "failure", nullable = true)
    private Boolean failure;

    @OneToMany(
            mappedBy = "bill",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Ticket> tickets;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            referencedColumnName = "id"
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(
            name = "status_id",
            nullable = false,
            referencedColumnName = "id"
    )
    private BillStatus status;


}
