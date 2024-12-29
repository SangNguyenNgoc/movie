package com.example.movieofficial.api.user.usecases.impl;

import com.example.movieofficial.api.user.dtos.ChangePassRequest;
import com.example.movieofficial.api.user.entities.User;
import com.example.movieofficial.api.user.exceptions.UserNotFoundException;
import com.example.movieofficial.api.user.interfaces.UserRepository;
import com.example.movieofficial.api.user.usecases.ChangePassUseCase;
import com.example.movieofficial.utils.exceptions.DataNotFoundException;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import com.example.movieofficial.utils.services.*;
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
public class DefaultChangePass implements ChangePassUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UtilsService utilsService;
    private final ObjectsValidator<ChangePassRequest> changePassValidator;
    private final RedisService<String> redisService;
    private final MailService mailService;

    @Value("${timeout.change_pass}")
    private Long changePasswordTimeout;

    @Override
    public void setUpUpdatePassword(ChangePassRequest request, String token) {
        var user = getByToken(token);
        changePassValidator.validate(request);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InputInvalidException("Input invalid", List.of("Invalid old password"));
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InputInvalidException("Input invalid", List.of("Invalid confirm password"));
        }
        var otp = utilsService.generateOtp(6);
        redisService.setValue(user.getId() + "_change-pass", otp + " " + passwordEncoder.encode(request.getNewPassword()), changePasswordTimeout);
        try {
            mailService.sendEmail(user.getEmail(), "Thay đổi mật khẩu của bạn",
                    "Duới đây là mã OTP để thay đổi mật khẩu của bạn. Vui lòng không gửi mã này cho bất cứ ai.\n" +
                            "Mã OTP của bạn là: " + otp
                    );
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reSendOtp(String token) {
        var user = getByToken(token);
        var infoChangePass = redisService.getValue(user.getId() + "_change-pass", new TypeReference<>() {});
        if (infoChangePass == null) {
            throw new DataNotFoundException("Not found", List.of("Not found request to change password"));
        }
        var newOtp = utilsService.generateOtp(6);
        redisService.setValue(user.getId() + "_change-pass", newOtp + " " + infoChangePass.split(" ")[1], changePasswordTimeout);
        try {
            mailService.sendEmail(user.getEmail(), "Thay đổi mật khẩu của bạn",
                    "Duới đây là mã OTP để thay đổi mật khẩu của bạn. Vui lòng không gửi mã này cho bất cứ ai.\n" +
                            "Mã OTP của bạn là: " + newOtp
            );
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void changePassword(String otp, String token) {
        var user = getByToken(token);
        var infoChangePass = redisService.getValue(user.getId() + "_change-pass", new TypeReference<>() {});
        if (infoChangePass == null) {
            throw new DataNotFoundException("Not found", List.of("Not found request to change password"));
        }
        var data = infoChangePass.split(" ");
        if (!otp.equals(data[0])) {
            throw new InputInvalidException("Input invalid", List.of("Invalid OTP"));
        }
        user.setPassword(data[1]);
        redisService.deleteValue(user.getId() + "_change-pass");
    }

    private User getByToken(String token) {
        token = tokenService.validateTokenBearer(token);
        String userId = tokenService.extractSubject(token);
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Not found", List.of("User not found"))
        );
    }


}
