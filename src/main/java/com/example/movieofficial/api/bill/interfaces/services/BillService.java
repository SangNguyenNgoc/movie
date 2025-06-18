package com.example.movieofficial.api.bill.interfaces.services;

import com.example.movieofficial.api.bill.dtos.AddConcessionToBill;
import com.example.movieofficial.api.bill.dtos.BillCreate;
import com.example.movieofficial.api.bill.dtos.BillDetail;
import com.example.movieofficial.api.bill.dtos.BillSession;
import com.example.movieofficial.api.bill.entities.Bill;
import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.hall.entities.Seat;
import com.example.movieofficial.api.show.entities.Show;
import com.example.movieofficial.api.ticket.entities.Ticket;
import com.example.movieofficial.api.user.entities.User;

import java.util.List;
import java.util.Set;

public interface BillService {

    BillSession createSession(BillCreate billCreate, String token);

    String addConcessionToBill(String billId, AddConcessionToBill addConcessionToBill, String token);

    User getUser(String token);


    List<BillDetail> getBillByUser(String token, Integer page, Integer size, String status);

    BillDetail getBillDetail(String id, String token);

    void deleteBillTask();

    void deleteSession(String token, String billId);
}
