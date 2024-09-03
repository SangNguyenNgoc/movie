package com.example.movieofficial.api.bill.dtos;

import com.example.movieofficial.api.bill.entities.Bill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * DTO for {@link Bill}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillDetail implements Serializable {
    private LocalDateTime createDate;
    private String id;
    private LocalDateTime paymentAt;
    private LocalDateTime expireAt;
    private Long total;
    private String paymentUrl;
    private String failureReason;
    private LocalDateTime failureAt;
    private Boolean failure;
    private BillDetail.BillStatusDto status;
    private TicketDto.ShowDto show;
    private Set<TicketDto> tickets;

    /**
     * DTO for {@link com.example.movieofficial.api.bill.entities.BillStatus}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BillStatusDto implements Serializable {
        private Integer id;
        private String name;

    }

    /**
     * DTO for {@link com.example.movieofficial.api.ticket.entities.Ticket}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TicketDto implements Serializable {
        private String id;
        private SeatDto seat;

        /**
         * DTO for {@link com.example.movieofficial.api.show.entities.Show}
         */
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ShowDto implements Serializable {
            private String id;
            private LocalDate startDate;
            private LocalTime startTime;
            private Integer runningTime;
            private Boolean status;
            private MovieDto movie;
            private TicketDto.SeatDto.HallDto hall;

            /**
             * DTO for {@link com.example.movieofficial.api.movie.entities.Movie}
             */
            @Data
            @AllArgsConstructor
            @NoArgsConstructor
            public static class MovieDto implements Serializable {
                private String id;
                private String name;
                private String subName;
                private LocalDate releaseDate;
                private LocalDate endDate;
                private String slug;
            }
        }

        /**
         * DTO for {@link com.example.movieofficial.api.hall.entities.Seat}
         */
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class SeatDto implements Serializable {
            private Long id;
            private Boolean status;
            private String rowName;
            private Integer rowIndex;
            private SeatTypeDto type;

            /**
             * DTO for {@link com.example.movieofficial.api.hall.entities.Hall}
             */
            @Data
            @AllArgsConstructor
            @NoArgsConstructor
            public static class HallDto implements Serializable {
                private Long id;
                private String name;
                private CinemaDto cinema;

                /**
                 * DTO for {@link com.example.movieofficial.api.cinema.entities.Cinema}
                 */
                @Data
                @AllArgsConstructor
                @NoArgsConstructor
                public static class CinemaDto implements Serializable {
                    private String id;
                    private String name;
                }
            }

            /**
             * DTO for {@link com.example.movieofficial.api.hall.entities.SeatType}
             */
            @Data
            @AllArgsConstructor
            @NoArgsConstructor
            public static class SeatTypeDto implements Serializable {
                private Integer id;
                private String name;
                private Long price;
            }
        }
    }
}