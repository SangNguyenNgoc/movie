package com.example.movieofficial.api.show.dtos;

import com.example.movieofficial.api.hall.dtos.SeatRow;
import com.example.movieofficial.api.show.entities.Show;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link Show}
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShowDetail extends RepresentationModel<ShowDetail> implements Serializable {
    private UUID id;
    private LocalDate startDate;
    private LocalTime startTime;
    private Integer runningTime;
    private Boolean status;
    private MovieDto movie;
    private HallDto hall;
    private FormatDto format;
    private List<ShowInfo> sameShows;


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
        private String poster;
        private Integer ageRestriction;
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
        private Integer numberOfRows;
        private Integer colsPerRow;
        private CinemaDto cinema;
        private List<SeatRow> rows;

        /**
         * DTO for {@link com.example.movieofficial.api.cinema.entities.Cinema}
         */
        @Data
        @AllArgsConstructor
        public static class CinemaDto implements Serializable {
            private String id;
            private String name;
            private String slug;
        }


    }

    /**
     * DTO for {@link com.example.movieofficial.api.movie.entities.Format}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FormatDto implements Serializable {
        private Long id;
        private String caption;
        private String version;
        private String slug;
    }
}