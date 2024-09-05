package com.example.movieofficial.api.user.dtos;

import com.example.movieofficial.api.user.entities.Gender;
import com.example.movieofficial.api.user.entities.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link User}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoUpdate implements Serializable {
    @NotBlank(message = "Tên người dùng không được để trống.")
    private String fullName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private Gender gender;
}