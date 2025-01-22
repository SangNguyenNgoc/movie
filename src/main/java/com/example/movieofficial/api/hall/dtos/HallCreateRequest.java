package com.example.movieofficial.api.hall.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for {@link com.example.movieofficial.api.hall.entities.Hall}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HallCreateRequest {
    @NotBlank
    private String cinemaId;
    @NotBlank
    private String name;
    @NotNull
    private Integer totalSeats;
    @NotNull
    private Integer numberOfRows;
    @NotNull
    private Integer colsPerRow;
    @NotNull
    private List<SeatRow> rows;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatRow {
        @NotBlank
        private String rowName;
        @NotNull
        private List<SeatDto> seats;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatDto {
        @NotNull
        private Integer typeId;
        @NotNull
        private Integer currCol;
    }
}
