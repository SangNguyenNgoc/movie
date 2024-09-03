package com.example.movieofficial.api.bill.interfaces;

import com.example.movieofficial.api.bill.dtos.BillDetail;
import com.example.movieofficial.api.bill.entities.Bill;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BillMapper {

    BillDetail toDetail(Bill source);


}
