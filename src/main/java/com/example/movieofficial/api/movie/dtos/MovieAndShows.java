package com.example.movieofficial.api.movie.dtos;

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
 * DTO for {@link com.example.movieofficial.api.movie.entities.Movie}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieAndShows implements Serializable {
    private String id;
    private String name;
    private String subName;
    private LocalDate releaseDate;
    private Integer runningTime;
    private Integer numberOfRatings;
    private Integer sumOfRatings;
    private String poster;
    private String horizontalPoster;
    private Integer ageRestriction;
    private List<ShowDto> shows;

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
        private FormatDto format;

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
        }
    }
}