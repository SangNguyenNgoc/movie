package com.example.movieofficial.api.user.usecases.impl;

import com.example.movieofficial.api.user.entities.User;
import com.example.movieofficial.api.user.exceptions.UserNotFoundException;
import com.example.movieofficial.api.user.interfaces.UserRepository;
import com.example.movieofficial.api.user.usecases.ChangeEmailUseCase;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import com.example.movieofficial.utils.exceptions.UrlInvalidException;
import com.example.movieofficial.utils.mvc.MessageDto;
import com.example.movieofficial.utils.services.MailService;
import com.example.movieofficial.utils.services.RedisService;
import com.example.movieofficial.utils.services.TokenService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DefaultChangeEmail implements ChangeEmailUseCase {

    private final TokenService tokenService;
    private final RedisService<String> redisService;
    private final UserRepository userRepository;
    private final TemplateEngine templateEngine;
    private final MailService mailService;

    @Value("${url.base-url}")
    private String baseUri;

    @Value("${timeout.change_email}")
    private Long changeEmailTimeout;

    @Value("${url.change-email-url}")
    private String changeEmailUrl;

    @Override
    @Transactional
    public void updateEmail(String verifyToken) {
        if (tokenService.isTokenExpired(verifyToken)) {
            throw new UrlInvalidException("Thay đổi email");
        }
        User user = getByToken(verifyToken);
        var newEmail = redisService.getAndDeleteValue(user.getId(), new TypeReference<>() {});
        if (newEmail == null) {
            throw new UrlInvalidException("Thay đổi email");
        }
        redisService.deleteValue(user.getId());
        user.setEmail(newEmail);
    }

    @Override
    public void setUpUpdateEmail(String newEmail, String token) {
        if (isEmailTaken(newEmail)) {
            throw new InputInvalidException("Email taken", List.of("This email is already taken"));
        }
        User user = getByToken(token);
        try {
            String verifyToken = tokenService.generateVerifyToken(user, changeEmailTimeout);
            Context context = new Context();
            context.setVariables(Map.of(
                    "name", user.getFullName(),
                    "url", baseUri + changeEmailUrl + "?t=" + verifyToken
            ));
            String text = templateEngine.process("mail/mail-template", context);
            redisService.setValue(user.getId(), newEmail, changeEmailTimeout);
            mailService.sendEmailHtml(newEmail, "Xác minh địa chỉ email mới của bạn.", text);
        } catch (MessagingException | UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
    }

    private User getByToken(String token) {
        String userId = tokenService.extractSubject(token);
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Not found", List.of("User not found"))
        );
    }

    private boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

}
