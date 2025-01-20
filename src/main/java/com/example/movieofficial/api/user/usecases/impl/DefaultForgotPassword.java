package com.example.movieofficial.api.user.usecases.impl;

import com.example.movieofficial.api.user.dtos.ResetPassRequest;
import com.example.movieofficial.api.user.entities.User;
import com.example.movieofficial.api.user.exceptions.UserNotFoundException;
import com.example.movieofficial.api.user.interfaces.UserRepository;
import com.example.movieofficial.api.user.usecases.ForgotPasswordUseCase;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import com.example.movieofficial.utils.exceptions.UrlInvalidException;
import com.example.movieofficial.utils.services.MailService;
import com.example.movieofficial.utils.services.ObjectsValidator;
import com.example.movieofficial.utils.services.RedisService;
import com.example.movieofficial.utils.services.TokenService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultForgotPassword implements ForgotPasswordUseCase {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final RedisService<String> redisService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectsValidator<ResetPassRequest> validator;

    @Value("${url.base-url}")
    private String baseUri;

    @Value("${url.reset-pass-url}")
    private String resetPasswordUrl;

    @Value("${timeout.reset_pass}")
    private Long resetPassTimeout;

    @Override
    public void setUpToResetPassword(String email) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new UserNotFoundException("Not found", List.of("User not found"))
            );
            var resetPassToken = tokenService.generateVerifyToken(user, resetPassTimeout);
            var url = baseUri + resetPasswordUrl + "?t=" + resetPassToken;
            redisService.setValue(user.getId() + "_reset-pass", resetPassToken, resetPassTimeout);
            mailService.sendEmail(user.getEmail(), "Đặt mật khẩu của bạn",
                    "Duới đây là đường dẫn để đặt lại mật khẩu của bạn. Vui lòng không gửi đường dẫn này cho bất cứ ai.\n " +
                            "Đường dẫn của bạn chỉ có hiệu lực trong 10 phút.\n" +
                            "Click vào để đặt lại mật khẩu: " +  url
            );
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkTokenResetPassword(String token) {
        var user = getByToken(token);
        var infoResetPass = redisService.getValue(user.getId() + "_reset-pass", new TypeReference<>() {});
        return infoResetPass != null;
    }

    @Override
    @Transactional
    public void resetPassword(ResetPassRequest resetPassRequest) {
        validator.validate(resetPassRequest);
        var user = getByToken(resetPassRequest.getResetPasswordToken());
        var infoResetPass = redisService.getValue(user.getId() + "_reset-pass", new TypeReference<>() {});
        if (!infoResetPass.equals(resetPassRequest.getResetPasswordToken())) {
            throw new UrlInvalidException("Đặt lại mật khẩu");
        }
        if (!resetPassRequest.getConfirmPassword().equals(resetPassRequest.getNewPassword())) {
            throw new InputInvalidException("Input invalid", List.of("Mật khẩu xác nhận chưa đúng."));
        }
        user.setPassword(passwordEncoder.encode(resetPassRequest.getNewPassword()));

    }

    private User getByToken(String resetPasswordToken) {
        //Không phải là token đăng nhập nên không cần check Bearer
        String userId = tokenService.extractSubject(resetPasswordToken);
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Not found", List.of("User not found"))
        );
    }
}
