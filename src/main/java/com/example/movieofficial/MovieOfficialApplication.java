package com.example.movieofficial;

import com.example.movieofficial.utils.services.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@SpringBootApplication
public class MovieOfficialApplication {
    public static void main(String[] args) {
        SpringApplication.run(MovieOfficialApplication.class, args);
    }
}
