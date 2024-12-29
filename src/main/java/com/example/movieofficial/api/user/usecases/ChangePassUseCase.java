package com.example.movieofficial.api.user.usecases;

import com.example.movieofficial.api.user.dtos.ChangePassRequest;

public interface ChangePassUseCase {
    void setUpUpdatePassword(ChangePassRequest request, String token);

    void reSendOtp(String token);

    void changePassword(String otp, String token);
}
