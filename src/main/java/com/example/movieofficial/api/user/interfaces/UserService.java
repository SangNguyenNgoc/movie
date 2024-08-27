package com.example.movieofficial.api.user.interfaces;

import com.example.movieofficial.api.user.dtos.RegisterRequest;
import com.example.movieofficial.api.user.dtos.UserInfo;
import com.example.movieofficial.api.user.dtos.UserProfile;

import java.util.List;
import java.util.Map;

public interface UserService {

    void register(RegisterRequest registerRequest);

    Map<String, Object> verify(String token);

    boolean isEmailTaken(String email);

    boolean isPasswordConfirmed(RegisterRequest registerRequest);

    UserProfile getProfile(String token);

    List<UserInfo> getAll(Integer page, Integer size);

    UserInfo getById(String id);

    List<UserInfo> getByRole(Integer id, Integer page, Integer size);

}
