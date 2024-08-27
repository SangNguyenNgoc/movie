package com.example.movieofficial.api.show.dtos;

import com.example.movieofficial.api.hall.entities.Seat;
import com.example.movieofficial.api.show.entities.Show;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link Show}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowDetail implements Serializable {
    private UUID id;
    private LocalDate startDate;
    private LocalTime startTime;
    private Integer runningTime;
    private Boolean status;
    private MovieDto movie;
    private HallDto hall;

    /**
     * DTO for {@link com.example.movieofficial.api.movie.entities.Movie}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MovieDto implements Serializable {
        private UUID id;
        private String name;
        private String subName;
        private LocalDate releaseDate;
        private Integer runningTime;
        private String slug;
    }

    /**
     * DTO for {@link com.example.movieofficial.api.hall.entities.Hall}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HallDto implements Serializable {
        private Long id;
        private String name;
        private Integer totalSeats;
        private Integer availableSeats;
        private CinemaDto cinema;
        private List<SeatDto> seats;

        /**
         * DTO for {@link com.example.movieofficial.api.cinema.entities.Cinema}
         */
        @Data
        @AllArgsConstructor
        public static class CinemaDto implements Serializable {
            private UUID id;
            private String name;
        }

        /**
         * DTO for {@link Seat}
         */
        @Data
        @AllArgsConstructor
        public static class SeatDto implements Serializable {
            private Long id;
            private Boolean status;
            private String rowName;
            private Integer rowIndex;
            private Long price;
            private Boolean isReserved;

            public SeatDto() {
                this.isReserved = false;
            }
        }
    }
}