package com.example.movieofficial.api.user.auth;

import com.example.movieofficial.api.user.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Value("${url.home-page-url}")
    private String homePageUrl;

    @GetMapping("/login")
    public String loginPage(
            @Value("${url.register-page-url}") String registerPageUrl,
            @RequestParam(name = "error", required = false) String error,
            Model model
    ) {
        if (error != null) {
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

    @GetMapping("/email")
    public String email(
            @RequestParam(name = "t") String token,
            Model model
    ) {
        var message = userService.updateEmail(token);
        model.addAttribute("message", message.getMsg());
        model.addAttribute("subject", message.getSubject());
        model.addAttribute("image", message.getImg());
        model.addAttribute("title", message.getTitle());
        model.addAttribute("home", homePageUrl);
        return "success";
    }

}
