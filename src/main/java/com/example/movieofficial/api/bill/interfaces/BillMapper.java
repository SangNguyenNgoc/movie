package com.example.movieofficial.api.bill.interfaces;

import com.example.movieofficial.api.bill.entities.Bill;
import com.example.movieofficial.api.bill.dtos.BillDetail;
import com.example.movieofficial.api.hall.entities.Hall;
import com.example.movieofficial.api.show.entities.Show;
import com.example.movieofficial.api.ticket.entities.Ticket;
import org.mapstruct.*;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BillMapper {

    BillDetail toDetail(Bill source);



}
