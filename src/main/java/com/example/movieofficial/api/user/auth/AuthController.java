package com.example.movieofficial.api.user.auth;

import com.example.movieofficial.api.user.dtos.ResetPassRequest;
import com.example.movieofficial.api.user.exceptions.UserNotFoundException;
import com.example.movieofficial.api.user.interfaces.UserService;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import com.example.movieofficial.utils.exceptions.UrlInvalidException;
import com.example.movieofficial.utils.mvc.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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
        userService.updateEmail(token);
        var message = MessageDto.builder()
                .title("Thay đổi email")
                .subject("Cập nhật thành công !")
                .img("/images/tick.svg")
                .msg("Email của bạn đã được cập nhật thành công, giờ đây bạn có thể đăng nhập với email mới.")
                .build();
        model.addAttribute("message", message.getMsg());
        model.addAttribute("subject", message.getSubject());
        model.addAttribute("image", message.getImg());
        model.addAttribute("title", message.getTitle());
        model.addAttribute("home", homePageUrl);
        return "result";
    }

    @ExceptionHandler(UrlInvalidException.class)
    public ModelAndView handleException(UrlInvalidException e) {
        var modelAndView = new ModelAndView("result");
        var message = MessageDto.builder()
                    .subject("Đường dẫn không hợp lệ.")
                    .img("/images/error.svg")
                    .msg("Đường dẫn của bạn đã hết hạn hoặc không hợp lệ !")
                    .build();
        modelAndView.addObject("message", message.getMsg());
        modelAndView.addObject("subject", message.getSubject());
        modelAndView.addObject("image", message.getImg());
        modelAndView.addObject("title", e.getTitle());
        modelAndView.addObject("home", homePageUrl);
        return modelAndView;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleException(UserNotFoundException e) {
        return new ModelAndView("error");
    }

    @GetMapping("/forgot-password")
    public ModelAndView showResetPasswordForm() {
        return new ModelAndView("forgot-password");
    }

    @PostMapping("/reset-password-request")
    public ModelAndView handleResetPasswordRequest(@RequestParam("mail") String email) {
        ModelAndView modelAndView = new ModelAndView();

        try {
            userService.setUpToResetPassword(email);
            modelAndView.setViewName("result");
            modelAndView.addObject("title", "Yêu cầu đặt lại mật khẩu");
            modelAndView.addObject("subject", "Yêu cầu thành công!");
            modelAndView.addObject("image", "/images/tick.svg");
            modelAndView.addObject("message", "Chúng tôi đã gửi một đường liên kết đặt lại mật khẩu tới email của bạn. Vui lòng kiểm tra hộp thư!");
        } catch (Exception e) {
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }


    @GetMapping("/reset-password")
    public ModelAndView resetPasswordPage( @RequestParam(name = "t") String token
    ) {
        if (token == null || !userService.checkTokenResetPass(token)) {
            return new ModelAndView("error");
        }
        var modelAndView = new ModelAndView("reset-password");
        modelAndView.addObject("token", token);
        return modelAndView;
    }

    @PostMapping("/reset-password")
    public ModelAndView handleResetPassword(
            @RequestParam("password") String password,
            @RequestParam("confirm") String confirm,
            @RequestParam("token") String token
    ) {
        ModelAndView modelAndView = new ModelAndView("reset-password");

        if (!password.equals(confirm)) {
            modelAndView.addObject("error", "* Mật khẩu và xác nhận mật khẩu không khớp!");
            modelAndView.addObject("token", token); // Giữ lại token để hiển thị lại form
            return modelAndView;
        }
        try {
            var request = ResetPassRequest.builder()
                    .newPassword(password)
                    .confirmPassword(confirm)
                    .resetPasswordToken(token)
                    .build();
            userService.resetPassword(request);

            var message = MessageDto.builder()
                    .subject("Thay đổi thành công.")
                    .img("/images/tick.svg")
                    .msg("Mật khẩu của bạn đã được thay đổi thành công, giờ đây bạn có thể đăng nhập với mật khẩu mới")
                    .build();
            modelAndView.setViewName("result");
            modelAndView.addObject("message", message.getMsg());
            modelAndView.addObject("subject", message.getSubject());
            modelAndView.addObject("image", message.getImg());
            modelAndView.addObject("title", "Thay đổi thành công");
            modelAndView.addObject("home", homePageUrl);
        } catch (InputInvalidException e) {
            String err = e.getMessages().stream().reduce((a, b) -> a + "," + b).orElse("");
            modelAndView.addObject("error", "* " + err);
            modelAndView.addObject("token", token);
            return modelAndView;
        }
        return modelAndView;
    }


}
