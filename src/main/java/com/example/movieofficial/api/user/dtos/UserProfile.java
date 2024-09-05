package com.example.movieofficial.api.user.dtos;

import com.example.movieofficial.api.user.entities.Role;
import com.example.movieofficial.api.user.entities.User;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.sql.Date;

/**
 * DTO for {@link User}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile extends RepresentationModel<UserProfile> implements Serializable {
    private String id;
    private String fullName;
    private String email;
    private Date dateOfBirth;
    private String avatar;
    private Boolean verify;
    private String phoneNumber;
    private String gender;
    private RoleDto role;

    /**
     * DTO for {@link Role}
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleDto implements Serializable {
        private String name;

    }
}