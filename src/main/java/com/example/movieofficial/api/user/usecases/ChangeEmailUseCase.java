package com.example.movieofficial.api.user.usecases;

import com.example.movieofficial.utils.mvc.MessageDto;

public interface ChangeEmailUseCase {

    void updateEmail(String verifyToken);

    void setUpUpdateEmail(String newEmail, String token);
}
