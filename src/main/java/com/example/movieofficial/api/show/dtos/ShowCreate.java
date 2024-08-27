package com.example.movieofficial.api.show.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowCreate {

    @NotBlank(message = "Movie id must not be blank")
    private String movieId;

    @NotBlank(message = "Movie id must not be blank")
    private Long hallId;

    @NotBlank(message = "Movie id must not be blank")
    private Long formatId;

    @NotBlank(message = "Movie id must not be blank")
    @Future(message = "Start time must be a future date")
    private LocalDateTime startTime;
}
