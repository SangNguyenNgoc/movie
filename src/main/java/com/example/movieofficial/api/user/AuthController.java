package com.example.movieofficial.api.user;

import com.example.movieofficial.api.user.dtos.RegisterRequest;
import com.example.movieofficial.api.user.interfaces.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Value("${url.base-url}")
    private String baseUri;

    @GetMapping("/login")
    public String loginPage(
            @Value("${url.base-url}") String baseUrl,
            @Value("${url.register-page-url}") String registerPageUrl,
            @Value("${url.registered-url}") String path,
            @RequestParam(name = "error", required = false) String error,
            Model model
    ) throws UnsupportedEncodingException {
        if(error != null) {
            model.addAttribute("error", true);
        }
        String registeredUri = baseUrl + path;
        model.addAttribute("register_page", registerPageUrl);
        model.addAttribute("registered_uri", URLEncoder.encode(registeredUri, StandardCharsets.UTF_8));
        return "login";
    }


    @GetMapping("/register")
    public String registerPage(
            @ModelAttribute("register") RegisterRequest register,
            @RequestParam(name = "redirect_uri") String redirectUri,
            Model model
    ) {
        model.addAttribute("redirect_uri", redirectUri);
        return "register";
    }


    @PostMapping("/register")
    public String register(
            @RequestParam(name = "redirect_uri") String redirectUri,
            HttpSession httpSession,
            Model model,
            @Valid @ModelAttribute("register") RegisterRequest registerRequest,
            BindingResult result
    ) {
        model.addAttribute("redirect_uri", redirectUri);
        if (result.hasErrors()) {
            return "register";
        }
        if (userService.isEmailTaken(registerRequest.getEmail())) {
            model.addAttribute("emailTaken", true);
            return "register";
        }
        if (!userService.isPasswordConfirmed(registerRequest)) {
            model.addAttribute("confirmInvalid", true);
            return "register";
        }
        userService.register(registerRequest);
        redirectUri = URLDecoder.decode(redirectUri, StandardCharsets.UTF_8);
        httpSession.setAttribute("registerSuccess", true);
        return "redirect:" + redirectUri;
    }

    @GetMapping("/registered")
    public String registerSuccessPage(HttpSession httpSession) {
        Boolean isValidRequest = (Boolean) httpSession.getAttribute("registerSuccess");
        if (isValidRequest == null || !isValidRequest) {
            return "error";
        }
        httpSession.removeAttribute("registerSuccess");
        return "success";
    }

    @GetMapping("/mail")
    public String mail() {
        return "mail/mail-template";
    }


    @GetMapping("/verify")
    public ResponseEntity<Void> verify(
            @RequestParam(name = "t") String token
    ) {
        Map<String, Object> result = userService.verify(token);
        URI uri = UriComponentsBuilder.fromUriString(baseUri)
                .path("/redirect") // /redirect to client page to handler
                .queryParam("code", result.get("authorizationCode"))
                .queryParam("code_verified", result.get("codeVerified"))
                .build()
                .toUri();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(uri)
                .build();
    }

}
