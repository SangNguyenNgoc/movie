package com.example.movieofficial.api.show.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowAutoCreate {

    @NotNull(message = "Cinema id must not be null.")
    private String cinemaId;
    private LocalDate date;
    private List<MovieDto> movies;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovieDto {
        private String id;
        private Integer priority;
        private Integer runningTime;
        private LocalDate releaseDate;
        private Long formatId;
        private Integer originalPriority;

        public void resetPriority() {
            this.priority = this.originalPriority;
        }

        public void decreasePriority() {
            this.priority--;
        }
    }
}
