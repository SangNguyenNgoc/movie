package com.example.movieofficial.api.user;

import com.example.movieofficial.api.user.dtos.RegisterRequest;
import com.example.movieofficial.api.user.dtos.UserInfo;
import com.example.movieofficial.api.user.dtos.UserProfile;
import com.example.movieofficial.api.user.entities.Gender;
import com.example.movieofficial.api.user.entities.Role;
import com.example.movieofficial.api.user.entities.User;
import com.example.movieofficial.api.user.exceptions.RoleNotFoundException;
import com.example.movieofficial.api.user.exceptions.UnauthorizedException;
import com.example.movieofficial.api.user.exceptions.UserNotFoundException;
import com.example.movieofficial.api.user.interfaces.RoleRepository;
import com.example.movieofficial.api.user.interfaces.UserMapper;
import com.example.movieofficial.api.user.interfaces.UserRepository;
import com.example.movieofficial.api.user.interfaces.UserService;
import com.example.movieofficial.utils.services.AuthorizationCodeService;
import com.example.movieofficial.utils.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.*;
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

    @Value("${url.avatar}")
    private String baseAvatar;

    @Override
    public void register(RegisterRequest registerRequest) {
        Role role = roleRepository.findById(3L).orElseThrow(
                () -> new RoleNotFoundException("Server error", List.of("Server error"))
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
        //TODO: send Email
    }

    @Override
    @Transactional
    public Map<String, Object> verify(String token) {
        if (tokenService.isTokenExpired(token)) {
            throw new JwtException("Token is expired");
        }
        String userId = tokenService.extractSubject(token);
        User user = userRepository.findByIdAndVerifyFalse(UUID.fromString(userId)).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
        Role role = roleRepository.findById(2L).orElseThrow(
                () -> new RoleNotFoundException("Server error", List.of("Server error"))
        );
        user.setRole(role);
        user.setVerify(true);
        try {
            String codeVerified = authorizationCodeService.generateCodeVerifier();
            String authCode = authorizationCodeService.generateAuthorizationCode(user, codeVerified).getTokenValue();
            Map<String, Object> param = new HashMap<>();
            param.put("codeVerified", codeVerified);
            param.put("authorizationCode", authCode);
            return param;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi không xác định.");
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
}
