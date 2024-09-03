package com.example.movieofficial.api.user.interfaces;

import com.example.movieofficial.api.user.dtos.RegisterRequest;
import com.example.movieofficial.api.user.dtos.UserInfo;
import com.example.movieofficial.api.user.dtos.UserProfile;
import com.example.movieofficial.api.user.entities.User;

import java.net.URI;
import java.util.List;

public interface UserService {

    String register(RegisterRequest registerRequest);

    URI verify(String token);

    String sendToVerify(String email);

    void sendToVerify(User user);

    boolean isEmailTaken(String email);

    boolean isPasswordConfirmed(RegisterRequest registerRequest);

    UserProfile getProfile(String token);

    List<UserInfo> getAll(Integer page, Integer size);

    UserInfo getById(String id);

    List<UserInfo> getByRole(Integer id, Integer page, Integer size);

}
