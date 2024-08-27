package com.example.movieofficial.api.user.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Tên người dùng không được để trống.")
    private String fullName;

    @Email(message = "Email sai định dạng.")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống.")
    @Pattern(regexp = "^[a-zA-Z0-9]{8,20}$", message = "Mật khẩu phải có độ dài từ 8 đến 20 ký tự")
    private String password;

    @NotBlank(message = "Hãy xác nhận lại mật khẩu")
    private String confirmPassword;

    private Boolean agreeTerms;
}
