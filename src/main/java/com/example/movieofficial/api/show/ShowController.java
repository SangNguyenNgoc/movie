package com.example.movieofficial.api.show;

import com.example.movieofficial.api.show.dtos.ShowAutoCreate;
import com.example.movieofficial.api.show.dtos.ShowCreate;
import com.example.movieofficial.api.show.dtos.ShowDetail;
import com.example.movieofficial.api.show.dtos.ShowInfo;
import com.example.movieofficial.api.show.interfaces.ShowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;
    private final ShowModelAssembler showAssembler;


    @Operation(
            summary = "Create a new show",
            description = "This endpoint allows administrators to create a new show. " +
                    "The details of the show should be provided in the request body. " +
                    "A successful creation returns a created show information and the HTTP status code 201 (Created).",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Show created successfully.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body. The provided data is incorrect or incomplete.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden access. The user does not have the required role.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access. The request is missing a valid Bearer token.",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ShowInfo> createShow(@RequestBody ShowCreate showCreate) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(showService.create(showCreate));
    }


    @Operation(
            summary = "Schedule list of show for 1 one of the cinemas.",
            description = "This endpoint allows administrators to create a new show. " +
                    "The details of the show should be provided in the request body. " +
                    "A successful creation returns list of scheduled show information " +
                    "and the HTTP status code 201 (Created).",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Show scheduled successfully.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body. The provided data is incorrect or incomplete.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden access. The user does not have the required role.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access. The request is missing a valid Bearer token.",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/schedule")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ShowInfo>> scheduleShow(@RequestBody ShowAutoCreate showAutoCreate) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(showService.scheduleShow(showAutoCreate));
    }


    @Operation(
            summary = "Retrieve details of a show",
            description = "This endpoint allows users (both administrators and regular users) to retrieve details of a specific show based on its ID. " +
                    "The ID should be provided as a path variable.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the show details.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ShowDetail.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access. The request is missing a valid Bearer token.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden access. The user does not have the required role or permissions.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Show not found. The provided ID does not match any existing show.",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ShowDetail> getShowDetail(@PathVariable String id) {
        var showDetail = showService.getShowDetail(id);
        showAssembler.linkToCreateBill(showDetail);
        return ResponseEntity.ok(showDetail);
    }
}
