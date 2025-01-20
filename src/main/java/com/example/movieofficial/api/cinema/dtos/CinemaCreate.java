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
public class CinemaCreate implements Serializable {
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotBlank
    private String description;
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid number phone")
    private String phoneNumber;
}