package com.example.movieofficial.api.ticket.interfaces;

import com.example.movieofficial.api.ticket.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    @Query("""
            select t from Ticket t
            where t.show.id = ?1
            and ( (t.bill.expireAt > ?2 and t.bill.status.id = 1) or t.bill.status.id = 2 )
            order by t.seat.rowName, t.seat.rowIndex
            """)
    List<Ticket> findByShowIdOrderBySeatRowNameAscSeatRowIndexAsc(String id, LocalDateTime dateTime);


}