package com.example.movieofficial.api.movie;

import com.example.movieofficial.api.movie.dtos.MovieInfoLanding;
import com.example.movieofficial.api.movie.interfaces.services.FormatService;
import com.example.movieofficial.utils.dtos.ListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/formats")
@RequiredArgsConstructor
public class FormatController {

    private final FormatService formatService;

    @GetMapping
    public ResponseEntity<ListResponse<MovieInfoLanding.FormatDto>> getAllFormats() {
        var result = formatService.getAllFormats();
        var response = ListResponse.<MovieInfoLanding.FormatDto>builder()
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }
}
