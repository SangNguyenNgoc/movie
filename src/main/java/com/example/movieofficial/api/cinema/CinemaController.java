package com.example.movieofficial.api.cinema;

import com.example.movieofficial.api.cinema.dtos.CinemaDetail;
import com.example.movieofficial.api.cinema.dtos.CinemaInfoLanding;
import com.example.movieofficial.api.cinema.interfaces.CinemaService;
import com.example.movieofficial.api.show.ShowModelAssembler;
import com.example.movieofficial.utils.dtos.ListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/cinemas")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService cinemaService;
    private final CinemaModelAssembler cinemaAssembler;

    @Operation(
            summary = "Get all cinemas",
            description = "This API endpoint allows users to fetch all cinemas. " +
                    "The data is fetched from the Redis cache for faster access."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of cinemas fetched successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListResponse.class))
            )
    })
    @GetMapping("/home")
    public ResponseEntity<ListResponse<CinemaInfoLanding>> getCinemaForLanding() {
        List<CinemaInfoLanding> result = cinemaService.getCinemaForLanding();
        var response = ListResponse.<CinemaInfoLanding>builder()
                .data(result)
                .build();
        response.add(linkTo(methodOn(CinemaController.class)
                .getCinemaForLanding())
                .withSelfRel()
                .withType(HttpMethod.GET.name()));
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Get all cinemas and their shows",
            description = "This API endpoint fetches a list of all cinemas along with the shows they are currently screening. " +
                    "The shows are categorized by film, and the data is fetched from the Redis cache for faster access."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of cinemas and shows fetched successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListResponse.class))
            )
    })
    @GetMapping("/shows")
    public ResponseEntity<ListResponse<CinemaDetail>> getAllCinemaAndShows() {
        List<CinemaDetail> result = cinemaService.getAllCinemaAndShowsFromRedis();
        result.forEach(cinemaAssembler::linkToGetShowDetail);
        var response = ListResponse.<CinemaDetail>builder()
                .data(result)
                .build();
        response.add(linkTo(methodOn(CinemaController.class)
                .getAllCinemaAndShows())
                .withSelfRel()
                .withType(HttpMethod.GET.name()));
        return ResponseEntity.ok(response);
    }
}
