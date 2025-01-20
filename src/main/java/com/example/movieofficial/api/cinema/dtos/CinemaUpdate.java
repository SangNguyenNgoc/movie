package com.example.movieofficial.api.cinema.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.example.movieofficial.api.cinema.entities.Cinema}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CinemaUpdate implements Serializable {
    private String name;
    private String address;
    private String description;
    private String phoneNumber;

    private Long statusId;
}