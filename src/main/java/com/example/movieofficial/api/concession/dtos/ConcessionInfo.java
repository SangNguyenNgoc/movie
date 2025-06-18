package com.example.movieofficial.api.concession.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcessionInfo implements Serializable {
    private String id;
    private String name;
    private String description;
    private String image;
    private Long price;
    private Long maxQuantity;
}
