package com.example.movieofficial.api.hall.interfaces;

import com.example.movieofficial.api.bill.dtos.BillDetail;
import com.example.movieofficial.api.hall.entities.Hall;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HallMapper {

    BillDetail.TicketDto.SeatDto.HallDto toHallInBillDetail(Hall hall);
}
