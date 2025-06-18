package com.example.movieofficial.api.concession;

import com.example.movieofficial.api.concession.dtos.ConcessionInfo;
import com.example.movieofficial.api.concession.services.ConcessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/concessions")
@RequiredArgsConstructor
public class ConcessionController {

    private final ConcessionService concessionService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<ConcessionInfo>> getConcessionByCinema(@RequestParam(name = "cinemaId")String cinemaId) {
        return ResponseEntity.ok(concessionService.getConcessionByCinema(cinemaId));
    }
}
