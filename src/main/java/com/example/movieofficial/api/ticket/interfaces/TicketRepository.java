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
            order by t.seat.currRow desc , t.seat.currRow
            """)
    List<Ticket> findByShowIdOrderBySeatRowNameAscSeatRowIndexAsc(String id, LocalDateTime dateTime);
    //Nếu vé còn thời hạn thanh toán và trạng thái chưa thanh toán hoặc đã thanh toán rồi thì tính ghế bận
    //Nếu vé hết thời hạn thanh toán và trạng thái chưa thanh toán thì tính ghế trống


}