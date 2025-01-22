package com.example.movieofficial.api.hall.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.example.movieofficial.api.hall.entities.Hall}
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HallResponse implements Serializable {
    private Long id;
    private String name;
    private Integer totalSeats;
    private Integer availableSeats;
    private Integer numberOfRows;
    private Integer colsPerRow;
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