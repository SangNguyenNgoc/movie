package com.example.movieofficial.api.user.dtos;

import com.example.movieofficial.api.user.entities.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.example.movieofficial.api.user.entities.User}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo implements Serializable {
    private LocalDateTime createDate;
    private LocalDateTime lastModifiedDate;
    private UUID id;
    private String fullName;
    private String email;
    private Date dateOfBirth;
    private String avatar;
    private Boolean verify;
    private String phoneNumber;
    private Gender gender;
    private RoleDto role;

    /**
     * DTO for {@link com.example.movieofficial.api.user.entities.Role}
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleDto implements Serializable {
        private Integer id;
        private String name;
    }
}