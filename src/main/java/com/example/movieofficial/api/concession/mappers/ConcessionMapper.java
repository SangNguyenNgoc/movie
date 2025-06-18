package com.example.movieofficial.api.concession.mappers;

import com.example.movieofficial.api.concession.dtos.ConcessionInfo;
import com.example.movieofficial.api.concession.entities.Concession;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConcessionMapper {
    ConcessionInfo toInfo(Concession concession);
}
