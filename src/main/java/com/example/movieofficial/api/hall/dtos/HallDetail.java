package com.example.movieofficial.api.hall.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * DTO for {@link com.example.movieofficial.api.hall.entities.Hall}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HallDetail implements Serializable {
    private Long id;
    private String name;
    private Integer totalSeats;
    private Integer availableSeats;
    private Integer numberOfRows;
    private Integer colsPerRow;
    private List<SeatRow> seats;
    private HallStatusDto status;

    /**
     * DTO for {@link com.example.movieofficial.api.hall.entities.HallStatus}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HallStatusDto implements Serializable {
        private Long id;
        private String name;
    }
}