package com.example.movieofficial.api.hall;

import com.example.movieofficial.api.hall.dtos.HallCreateRequest;
import com.example.movieofficial.api.hall.dtos.HallDetail;
import com.example.movieofficial.api.hall.dtos.HallResponse;
import com.example.movieofficial.api.hall.services.HallService;
import com.example.movieofficial.api.show.dtos.ShowDetail;
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
@RequestMapping("/api/v1/halls")
@RequiredArgsConstructor
public class HallController {

    private final HallService hallService;


    @Operation(
            summary = "Retrieve halls in a cinema",
            description = "This endpoint allows users (administrators) to retrieve list of halls in a cinema based on cinema ID " +
                    "The ID should be provided as a path variable.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list of halls.",
                    content = @Content(mediaType = "application/json")
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
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<HallResponse>> getHallByCinemaId(
            @RequestParam(value = "cinema") String cinemaId
    ) {
        return ResponseEntity.ok(hallService.getHallByCinema(cinemaId));
    }


    @Operation(
            summary = "Retrieve details of a hall",
            description = "This endpoint allows users (administrators) to retrieve details of a specific hall based on its ID. " +
                    "The ID should be provided as a path variable.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the hall details.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HallDetail.class))
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
                    description = "Hall not found. The provided ID does not match any existing hall.",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<HallDetail> getHallDetailById(
            @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(hallService.getHallById(id));
    }


    @Operation(
            summary = "Create one hall and its seat layout.",
            description = "This endpoint allows administrators to create a new hall in a cinema. " +
                    "The details of hall and seat layout should be provided in the request body. " +
                    "A successful creation returns information of the new hall " +
                    "and the HTTP status code 201 (Created).",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Hall created successfully.",
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
    public ResponseEntity<HallResponse> create(@RequestBody HallCreateRequest hallCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(hallService.create(hallCreateRequest));
    }


    @Operation(
            summary = "Update status of hall.",
            description = "This endpoint allows administrators to update status in hall based on hall id and status id. " +
                    "A successful changing returns information of the new hall " +
                    "and the HTTP status code 200 (Success).",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Hall status is changed successfully.",
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
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<HallResponse> updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") Long status
    ) {
        return ResponseEntity.ok(hallService.updateHallStatus(id, status));
    }


}
