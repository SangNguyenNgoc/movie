package com.example.movieofficial.api.user;

import com.example.movieofficial.api.user.dtos.RegisterRequest;
import com.example.movieofficial.api.user.interfaces.UserService;
import com.example.movieofficial.utils.services.TokenService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final TokenService tokenService;

    @Value("${url.base-url}")
    private String baseUri;

    @GetMapping("/login")
    public String loginPage(
            @Value("${url.register-page-url}") String registerPageUrl,
            @RequestParam(name = "error", required = false) String error,
            Model model
    ) {
        if(error != null) {
            model.addAttribute("error", true);
        }
        model.addAttribute("register_page", registerPageUrl);
        return "login";
    }


    @GetMapping("/verify")
    public ResponseEntity<Void> verify(@RequestParam(name = "t") String token) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(userService.verify(token))
                .build();
    }

}
