package com.example.movieofficial.api.movie;

import com.example.movieofficial.api.movie.dtos.MovieDetail;
import com.example.movieofficial.api.movie.dtos.MovieInfoAdmin;
import com.example.movieofficial.api.movie.dtos.MovieInfoLanding;
import com.example.movieofficial.api.movie.dtos.StatusInfo;
import com.example.movieofficial.api.movie.interfaces.services.MovieService;
import com.example.movieofficial.utils.dtos.ListResponse;
import com.example.movieofficial.utils.dtos.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final MovieModelAssembler movieAssembler;

    @Operation(
            summary = "Get movies to landing page",
            description = "This API endpoint get movies to render in landing page, " +
                    "the movies are categorized by status coming soon or showing now"
    )
    @GetMapping("/home")
    public ResponseEntity<ListResponse<StatusInfo>> getMoviesToLanding() {
        List<StatusInfo> statusInfoList = movieService.getMovieToLandingFromRedis();
        statusInfoList.forEach(statusInfo -> {
            movieAssembler.linkToGetMovieStatus(statusInfo);
            statusInfo.getMovies().forEach(movieAssembler::linkToGetMovieDetail);
        });
        var response = ListResponse.<StatusInfo>builder()
                .data(statusInfoList)
                .build();
        response.add(linkTo(methodOn(MovieController.class)
                .getMoviesToLanding())
                .withSelfRel()
                .withType(HttpMethod.GET.name()));
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Get all showing now movies",
            description = "This API endpoint get showing now  movies to render in landing page. "
    )
    @GetMapping("/showing-now")
    public ResponseEntity<PageResponse<MovieInfoLanding>> getMoviesShowingNow(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ) {
        PageResponse<MovieInfoLanding> response = movieService.getMoviesByStatusFromRedis("showing-now", page - 1, size);
        response.add(linkTo(MovieController.class)
                .slash("showing-now")
                .withSelfRel()
                .withType(HttpMethod.GET.name()));
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Get all coming soon movies",
            description = "This API endpoint get coming soon  movies to render in landing page. "
    )
    @GetMapping("/coming-soon")
    public ResponseEntity<PageResponse<MovieInfoLanding>> getMoviesByComingSoon(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ) {
        PageResponse<MovieInfoLanding> response = movieService.getMoviesByStatusFromRedis("coming-soon", page - 1, size);
        response.add(linkTo(MovieController.class)
                .slash("coming-soon")
                .withSelfRel()
                .withType(HttpMethod.GET.name()));
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Get movies information",
            description = "This API endpoint fetches a list of movies information to admin page. " +
                    "Requires 'ROLE_ADMIN' authority."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of cinemas and shows fetched successfully.",
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
            )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PageResponse<MovieInfoAdmin>> getAll(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ) {
        PageResponse<MovieInfoAdmin> response = movieService.getAll(page - 1, size);
        response.add(linkTo(MovieController.class).withSelfRel().withType(HttpMethod.GET.name()));
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Get a movie information by slug",
            description = "This API endpoint fetches a movie information to admin page, require movie slug to fetching. " +
                    "Requires 'ROLE_ADMIN' authority."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of cinemas and shows fetched successfully.",
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
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MovieInfoAdmin> getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(movieService.getById(id));
    }


    @Operation(
            summary = "Get movie and its shows",
            description = "This API endpoint fetches a movie by slug with the shows are currently being screened. " +
                    "The shows are categorized by cinemas, and the data is fetched from the Redis cache for faster access."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of cinemas and shows fetched successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Data not found, movie not found.",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{slug}/shows")
    public ResponseEntity<MovieDetail> getMovieDetail(@PathVariable String slug) {
        MovieDetail result = movieService.getMovieAndShowsFromRedis(slug);
        movieAssembler.linkToGetShowDetail(result);
        return ResponseEntity.ok(result);
    }

}
