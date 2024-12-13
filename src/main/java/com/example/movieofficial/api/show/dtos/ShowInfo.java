package com.example.movieofficial.api.show.dtos;

import com.example.movieofficial.api.show.entities.Show;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO for {@link Show}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowInfo implements Serializable {
    private LocalDateTime createDate;
    private String id;
    private LocalDate startDate;
    private LocalTime startTime;
    private Integer runningTime;
}