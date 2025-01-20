package com.example.movieofficial.api.cinema;

import com.example.movieofficial.api.cinema.dtos.*;
import com.example.movieofficial.api.cinema.interfaces.CinemaService;
import com.example.movieofficial.api.movie.dtos.MovieInfoAdmin;
import com.example.movieofficial.api.movie.dtos.MovieUpdate;
import com.example.movieofficial.api.show.ShowModelAssembler;
import com.example.movieofficial.utils.dtos.ListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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


    @Operation(
            summary = "Get one cinemas by slug and its shows",
            description = "This API endpoint fetches a cinemas by slug with the shows it is currently screening. " +
                    "The shows are categorized by film, and the data is fetched from the Redis cache for faster access."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of cinemas and shows fetched successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cinema not found.",
                    content = @Content(mediaType = "application/json")
            ),
    })
    @GetMapping("/shows/{slug}")
    public ResponseEntity<CinemaDetail> getAllCinemaAndShows(@PathVariable(value = "slug") String slug) {
        var result = cinemaService.getCinemaAndShowsFromRedis(slug);
        cinemaAssembler.linkToGetShowDetail(result);
        return ResponseEntity.ok(result);
    }


    @Operation(
            summary = "Create new cinema",
            description = "This API endpoint allow admin to create a new cinema" +
                    "Requires 'ROLE_ADMIN' authority."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Create successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Data not found, related data not found.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CinemaInfo> create(@RequestBody CinemaCreate cinemaCreate) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cinemaService.createCinema(cinemaCreate));
    }


    @Operation(
            summary = "Update cinema information",
            description = "This API endpoint allow admin to update cinema information" +
                    "Requires 'ROLE_ADMIN' authority."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Update successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Data not found, movie not found.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CinemaInfo> updateMovieInfo(
            @RequestBody CinemaUpdate cinemaUpdate,
            @PathVariable String id
    ) {
        return ResponseEntity.ok(cinemaService.updateCinema(cinemaUpdate, id));
    }
}
