package com.example.movieofficial.api.hall.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatRow implements Comparable<SeatRow> , Serializable {
    private Integer row;
    private String rowName;
    private List<SeatDto> seats;

    @Override
    public int compareTo(SeatRow o) {
        return Integer.compare(o.row, this.row);
    }

    /**
     * DTO for {@link Seat}
     */
    @Data
    @AllArgsConstructor
    public static class SeatDto implements Serializable {
        private Long id;
        private String name;
        private Integer currRow; //currRow start at 1
        private Integer currCol; //currCol start at 1
        private Long price;
        private Boolean isReserved;

        public SeatDto() {
            this.isReserved = false;
        }
    }
}
