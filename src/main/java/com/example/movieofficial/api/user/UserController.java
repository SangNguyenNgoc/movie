package com.example.movieofficial.api.user;

import com.example.movieofficial.api.user.dtos.RegisterRequest;
import com.example.movieofficial.api.user.dtos.UserInfo;
import com.example.movieofficial.api.user.dtos.UserInfoUpdate;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserModelAssembler userAssembler;

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
            summary = "Retrieve user profile",
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
        var token = request.getHeader("Authorization");
        var profile = userService.getProfile(token);
        userAssembler.linkToCrudUser(profile);
        return ResponseEntity.ok(profile);
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
        result.forEach(userAssembler::linkToGetUserInfo);
        var response = ListResponse.<UserInfo>builder()
                .data(result)
                .build();
        response.add(linkTo(UserController.class).withSelfRel().withType(HttpMethod.GET.name()));
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
        var result = userService.getById(id);
        userAssembler.linkToGetAllUser(result);
        return ResponseEntity.ok(result);
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
        result.forEach(userAssembler::linkToGetUserInfo);
        var response = ListResponse.<UserInfo>builder()
                .data(result)
                .build();
        response.add(linkTo(UserController.class)
                .slash("/role")
                .withSelfRel()
                .withType(HttpMethod.GET.name()));
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Send verification email",
            description = "Sends an email containing a verification URL to the user's email address. " +
                    "Use this endpoint to request a verification email when a new user registers or when a re-verification is requested."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sending url to email successfully",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters. The `email` parameters are incorrect.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found. User with email address not found.",
                    content = @Content(mediaType = "application/json")
            ),
    })
    @GetMapping("/verification-email")
    public ResponseEntity<String> sendUrlToVerify(@RequestParam("email") String email) {
        return ResponseEntity.ok(userService.sendToVerify(email));
    }


    @Operation(
            summary = "Update user information",
            description = "This API endpoint allows users to update their information. " +
                    "The information that can be changed includes: `fullName`, `phoneNumber`, `gender`, `dateOfBirth`.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Updating user information successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfile.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found.",
                    content = @Content(mediaType = "application/json")
            ),
    })
    @PutMapping("/information")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<UserProfile> updateInfo(
            @RequestBody UserInfoUpdate update,
            HttpServletRequest request
    ) {
        var token = request.getHeader("Authorization");
        var profile = userService.updateInfo(update, token);
        userAssembler.linkToCrudUser(profile);
        return ResponseEntity.ok(profile);
    }


    @Operation(
            summary = "Update user avatar",
            description = "This API endpoint allows users to update their avatar.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Updating user avatar successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfile.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found.",
                    content = @Content(mediaType = "application/json")
            ),
    })
    @PutMapping("/avatar")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<UserProfile> updateAvatar(
            @RequestParam(value = "avatar") MultipartFile image,
            HttpServletRequest request
    ) {
        var token = request.getHeader("Authorization");
        var profile = userService.updateAvatar(image, token);
        userAssembler.linkToCrudUser(profile);
        return ResponseEntity.ok(profile);
    }

}
