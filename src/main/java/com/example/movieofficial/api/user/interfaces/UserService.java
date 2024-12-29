package com.example.movieofficial.api.user.interfaces;

import com.example.movieofficial.api.user.dtos.*;
import com.example.movieofficial.api.user.entities.User;
import com.example.movieofficial.utils.dtos.PageResponse;
import com.example.movieofficial.utils.mvc.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

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

    PageResponse<UserInfo> getAll(Integer page, Integer size);

    UserInfo getById(String id);

    PageResponse<UserInfo> getByRole(Integer id, Integer page, Integer size);

    UserProfile updateInfo(UserInfoUpdate update, String token);

    UserProfile updateAvatar(MultipartFile image, String token);

    //verify token is not start with 'Bearer'
    MessageDto updateEmail(String verifyToken);

    void setUpUpdateEmail(String newEmail, String token);

    void setUpUpdatePassword(ChangePassRequest request, String token);

    void changePassword(String otp, String token);

    void logout(String token);
}
