package com.example.movieofficial.api.user.dtos;

import com.example.movieofficial.api.user.entities.Gender;

import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

/**
 * DTO for {@link com.example.movieofficial.api.user.entities.User}
 */
public record UserProfile(UUID id, String fullName, String email, Date dateOfBirth, String avatar, Boolean verify,
                          String phoneNumber, Gender gender, RoleDto role) implements Serializable {
    /**
     * DTO for {@link com.example.movieofficial.api.user.entities.Role}
     */
    public record RoleDto(String name) implements Serializable {
    }
}