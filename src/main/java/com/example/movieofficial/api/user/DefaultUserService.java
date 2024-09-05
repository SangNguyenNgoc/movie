package com.example.movieofficial.api.user;

import com.example.movieofficial.api.user.dtos.RegisterRequest;
import com.example.movieofficial.api.user.dtos.UserInfo;
import com.example.movieofficial.api.user.dtos.UserInfoUpdate;
import com.example.movieofficial.api.user.dtos.UserProfile;
import com.example.movieofficial.api.user.entities.Gender;
import com.example.movieofficial.api.user.entities.Role;
import com.example.movieofficial.api.user.entities.User;
import com.example.movieofficial.api.user.exceptions.RoleNotFoundException;
import com.example.movieofficial.api.user.exceptions.UserNotFoundException;
import com.example.movieofficial.api.user.interfaces.RoleRepository;
import com.example.movieofficial.api.user.interfaces.UserMapper;
import com.example.movieofficial.api.user.interfaces.UserRepository;
import com.example.movieofficial.api.user.interfaces.UserService;
import com.example.movieofficial.utils.exceptions.AppException;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import com.example.movieofficial.utils.services.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;
    private final AuthorizationCodeService authorizationCodeService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ObjectsValidator<RegisterRequest> registerValidator;
    private final ObjectsValidator<UserInfoUpdate> userInfoUpdateValidator;
    private final TemplateEngine templateEngine;
    private final MailService mailService;
    private final S3Service s3Service;

    @Value("${url.avatar}")
    private String baseAvatar;

    @Value("${url.base-url}")
    private String baseUri;

    @Value("${url.verify-url}")
    private String verifyUrl;

    @Override
    public String register(RegisterRequest registerRequest) {
        registerValidator.validate(registerRequest);

        if (isEmailTaken(registerRequest.getEmail())) {
            throw new AppException("Email taken!", HttpStatus.CONFLICT, List.of("Email already exists"));
        }

        if (!isPasswordConfirmed(registerRequest)) {
            throw new InputInvalidException("Invalid confirming password", List.of("Invalid confirming password"));
        }

        Role role = roleRepository.findById(3L).orElseThrow(
                () -> new RoleNotFoundException("Server error", List.of("Role not found"))
        );
        User user = User.builder()
                .email(registerRequest.getEmail())
                .fullName(registerRequest.getFullName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(role)
                .verify(false)
                .gender(Gender.UNKNOWN)
                .avatar(baseAvatar)
                .build();
        userRepository.save(user);
        sendToVerify(user);
        return "Success";
    }

    @Override
    @Transactional
    public URI verify(String token) {
        if (tokenService.isTokenExpired(token)) {
            return UriComponentsBuilder.fromUriString(baseUri)
                    .path("/redirect") // /redirect to client page to handler, fe will get parameter and call token from be
                    .queryParam("expired_url", true)
                    .build()
                    .toUri();
        }
        String userId = tokenService.extractSubject(token);
        User user = userRepository.findByIdAndVerifyFalse(userId).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
        Role role = roleRepository.findById(2L).orElseThrow(
                () -> new RoleNotFoundException("Server error", List.of("Server error"))
        );
        user.setRole(role);
        user.setVerify(true);
        try {
            String codeVerified = authorizationCodeService.generateCodeVerifier();
            String authCode = authorizationCodeService.generateAuthorizationCode(user, codeVerified).getTokenValue();
            return UriComponentsBuilder.fromUriString(baseUri)
                    .path("/redirect") // /redirect to client page to handler, fe will get parameter and call token from be
                    .queryParam("code", codeVerified)
                    .queryParam("code_verified", authCode)
                    .build()
                    .toUri();
        } catch (NoSuchAlgorithmException e) {
            throw new AppException("Server error.", HttpStatus.INTERNAL_SERVER_ERROR, List.of("Internal server error"));
        }
    }

    @Override
    public String sendToVerify(String email) {
        User user = userRepository.findByEmailAndVerifyFalse(email).orElseThrow(
                () -> new UserNotFoundException("Not found", List.of("User not found"))
        );
        sendToVerify(user);
        return "Success";
    }

    @Override
    public void sendToVerify(User user) {
        try {
            String verifyToken = tokenService.generateVerifyToken(user);
            Context context = new Context();
            context.setVariables(Map.of(
                    "name", user.getFullName(),
                    "url", baseUri + verifyUrl + "?t=" + verifyToken
            ));
            String text = templateEngine.process("mail/mail-template", context);
            mailService.sendEmailHtml(user.getEmail(), "Xác minh địa chỉ email của bạn", text);
        } catch (MessagingException | UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean isPasswordConfirmed(RegisterRequest registerRequest) {
        return registerRequest.getPassword().equals(registerRequest.getConfirmPassword());
    }

    @Override
    public UserProfile getProfile(String token) {
        token = tokenService.validateTokenBearer(token);
        String userId = tokenService.extractSubject(token);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Not found", List.of("User not found"))
        );
        return userMapper.toProfile(user);
    }

    @Override
    public List<UserInfo> getAll(Integer page, Integer size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<User> users = userRepository.findByOrderByCreateDateDesc(pageable);
        return users.stream().map(userMapper::toUserInfo).collect(Collectors.toList());
    }

    @Override
    public UserInfo getById(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Not found", List.of("User not found"))
        );
        return userMapper.toUserInfo(user);
    }

    @Override
    public List<UserInfo> getByRole(Integer id, Integer page, Integer size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<User> users = userRepository.findByRoleIdOrderByCreateDateDesc(id, pageable);
        return users.stream().map(userMapper::toUserInfo).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserProfile updateInfo(UserInfoUpdate update, String token) {
        userInfoUpdateValidator.validate(update);
        User user = getByToken(token);
        User updatedUser = userMapper.partialUpdate(update, user);
        return userMapper.toProfile(updatedUser);
    }

    @Override
    @Transactional
    public UserProfile updateAvatar(MultipartFile image, String token) {
        User user = getByToken(token);
        String url = s3Service.uploadFile(image, user.getId(), "avatar");
        user.setAvatar(url);
        return userMapper.toProfile(user);
    }

    private User getByToken(String token) {
        token = tokenService.validateTokenBearer(token);
        String userId = tokenService.extractSubject(token);
        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Not found", List.of("User not found"))
        );
    }
}
