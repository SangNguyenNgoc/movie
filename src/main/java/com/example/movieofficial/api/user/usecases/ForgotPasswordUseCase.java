package com.example.movieofficial.api.user.usecases;

import com.example.movieofficial.api.user.dtos.ResetPassRequest;

public interface ForgotPasswordUseCase {

    void setUpToResetPassword(String email);

    boolean checkTokenResetPassword(String token);

    void resetPassword(ResetPassRequest request);
}
