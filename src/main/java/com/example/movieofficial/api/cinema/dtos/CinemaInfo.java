package com.example.movieofficial.api.cinema.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.example.movieofficial.api.cinema.entities.Cinema}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CinemaInfo implements Serializable {
    private LocalDateTime createDate;
    private LocalDateTime lastModifiedDate;
    private String createBy;
    private String lastModifiedBy;
    private String id;
    private String name;
    private String slug;
    private String address;
    private String description;
    private String phoneNumber;
    private CinemaStatusDto status;

    /**
     * DTO for {@link com.example.movieofficial.api.cinema.entities.CinemaStatus}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CinemaStatusDto implements Serializable {
        private Long id;
        private String name;
    }
}