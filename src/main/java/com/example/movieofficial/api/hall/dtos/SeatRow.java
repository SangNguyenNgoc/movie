package com.example.movieofficial.api.hall.dtos;

import com.example.movieofficial.api.hall.entities.Seat;
import jakarta.persistence.Column;
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
        private SeatTypeDto type;
        private Boolean isReserved;

        public SeatDto() {
            this.isReserved = false;
        }

        /**
         * DTO for {@link com.example.movieofficial.api.hall.entities.SeatType}
         */
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SeatTypeDto implements Serializable {
            private Integer id;
            private String name;
            private Long price;
        }
    }
}
