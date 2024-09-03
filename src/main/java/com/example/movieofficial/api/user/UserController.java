package com.example.movieofficial.api.user;

import com.example.movieofficial.api.user.dtos.RegisterRequest;
import com.example.movieofficial.api.user.dtos.UserInfo;
import com.example.movieofficial.api.user.dtos.UserProfile;
import com.example.movieofficial.api.user.interfaces.UserService;
import com.example.movieofficial.utils.dtos.ListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided registration details. Returns a confirmation message upon successful registration. " +
                    "Then send a verification URL to the email that the user just used to register."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request, e.g., missing required fields or mismatched passwords",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error occurred during registration process",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }


    @Operation(
            summary = "Fetch User Profile",
            description = "This endpoint allows authenticated users to view their profile information. " +
                    "Access is restricted to authorized users. " +
                    "The request must include a valid Bearer token in the Authorization header.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully fetched the user profile.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfile.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access. The request is missing a valid Bearer token.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User is not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<UserProfile> getProfile(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getProfile(token));
    }


    @Operation(
            summary = "Retrieve all users",
            description = "This endpoint allows administrators to retrieve a paginated list of all users. The `page` and `size` parameters determine the pagination of the result.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of users.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access. The request is missing a valid Bearer token.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden access. The user does not have the required role.",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ListResponse<UserInfo>> getAll(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ) {
        List<UserInfo> result = userService.getAll(page - 1, size);
        var response = ListResponse.<UserInfo>builder()
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Retrieve user by ID",
            description = "This endpoint allows administrators to retrieve a user's information based on their ID. The ID should be provided as a path variable.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the user information.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfo.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access. The request is missing a valid Bearer token.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden access. The user does not have the required role.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found. The provided ID does not match any user.",
                    content = @Content(mediaType = "application/json")
            ),
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserInfo> getById(
            @PathVariable("id") String id
    ) {
        return ResponseEntity.ok(userService.getById(id));
    }


    @Operation(
            summary = "Retrieve users by role",
            description = "This endpoint allows administrators to retrieve a paginated list of users who have a specific role. The `role` parameter specifies the role to filter by, and `page` and `size` parameters control pagination.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of users with the specified role.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters. The `role`, `page`, or `size` parameters are incorrect.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access. The request is missing a valid Bearer token.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden access. The user does not have the required role.",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/role")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ListResponse<UserInfo>> getByRole(
            @RequestParam("role") Integer id,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ) {
        List<UserInfo> result = userService.getByRole(id, page - 1, size);
        var response = ListResponse.<UserInfo>builder()
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }
}
