package com.example.movieofficial.api.user.interfaces;

import com.example.movieofficial.api.user.dtos.*;
import com.example.movieofficial.api.user.entities.User;
import com.example.movieofficial.utils.dtos.PageResponse;
import com.example.movieofficial.utils.mvc.MessageDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

public interface UserService {

    //There functions are used in registering and verifying
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

    //There functions are used in updating email feature
    //verify token is not start with 'Bearer'
    void updateEmail(String verifyToken);
    void setUpUpdateEmail(String newEmail, String token);

    //There functions are used in changing password feature
    void setUpUpdatePassword(ChangePassRequest request, String token);
    void changePassword(String otp, String token);

    //There functions are used in resetting password
    void setUpToResetPassword(String email);
    boolean checkTokenResetPass(String token);
    void resetPassword(ResetPassRequest request);
}
