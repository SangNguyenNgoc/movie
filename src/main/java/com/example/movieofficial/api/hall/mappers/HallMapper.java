package com.example.movieofficial.api.hall.mappers;

import com.example.movieofficial.api.bill.dtos.BillDetail;
import com.example.movieofficial.api.hall.dtos.HallDetail;
import com.example.movieofficial.api.hall.dtos.HallResponse;
import com.example.movieofficial.api.hall.entities.Hall;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface HallMapper {

    BillDetail.TicketDto.SeatDto.HallDto toHallInBillDetail(Hall hall);

    HallResponse toDto(Hall hall);

    HallDetail toDetail(Hall hall);

}
