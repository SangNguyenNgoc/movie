package com.example.movieofficial.api.bill.usecases;

import com.example.movieofficial.api.bill.dtos.AddConcessionToBill;
import com.example.movieofficial.api.bill.dtos.BillCreate;
import com.example.movieofficial.api.bill.dtos.BillSession;
import com.example.movieofficial.api.bill.entities.Bill;
import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.hall.entities.Seat;
import com.example.movieofficial.api.show.entities.Show;
import com.example.movieofficial.api.ticket.entities.Ticket;

import java.util.List;
import java.util.Set;

public interface BookingUseCase {

    BillSession createSession(BillCreate billCreate, String token);

    void checkSeatsInHall(List<Long> seatIds, Hall hall);

    void checkSeatsAreReserved(List<Long> seatIds, List<Ticket> ticketsInShow);

    Set<Ticket> createTicket(Show show, List<Seat> seats, Bill bill);

    String addConcessionsToBill(String billId, AddConcessionToBill request, String token);
}
