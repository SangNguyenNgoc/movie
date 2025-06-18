package com.example.movieofficial.api.bill;

import com.example.movieofficial.api.bill.dtos.AddConcessionToBill;
import com.example.movieofficial.api.bill.dtos.BillCreate;
import com.example.movieofficial.api.bill.dtos.BillDetail;
import com.example.movieofficial.api.bill.dtos.BillSession;
import com.example.movieofficial.api.bill.interfaces.services.BillService;
import com.example.movieofficial.utils.dtos.ListResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @Operation(
            summary = "Create bill and return URL to payment.",
            description = "This API endpoint allows users to create a new bill and returns a redirect URL for payment. " +
                    "Requires 'ROLE_ADMIN' or 'ROLE_USER' authority.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "302",
                    description = "Bill created successfully and redirect URL returned."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid bill creation request.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access, authentication required.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Data not found, not found user, seat, show,... .",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<BillSession> createSession(
            @RequestBody BillCreate billCreate,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization");
        var result = billService.createSession(billCreate, token);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/{billId}/concessions")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<String> addConcessionToBill(
            @PathVariable(name = "billId") String billId,
            @RequestBody AddConcessionToBill addConcessionToBill,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization");
        var result = billService.addConcessionToBill(billId, addConcessionToBill, token);
        return ResponseEntity.ok(result);
    }


    @Operation(
            summary = "Get user bills",
            description = "This API endpoint allows users to fetch all the bills they have purchased. " +
                    "Requires 'ROLE_ADMIN' or 'ROLE_USER' authority.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of bills fetched successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access, authentication required.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Data not found, user not found.",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ListResponse<BillDetail>> getBillByUser(
            HttpServletRequest request,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("status") String status
    ) {
        String token = request.getHeader("Authorization");
        List<BillDetail> billDetails = billService.getBillByUser(token, page - 1, size, status);
        var response = ListResponse.<BillDetail>builder()
                .data(billDetails)
                .build();
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Get one bill detail",
            description = "This API endpoint allows users to fetch one bill detail by `ID` that they have purchased. " +
                    "Requires 'ROLE_ADMIN' or 'ROLE_USER' authority.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bill info was fetched successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access, authentication required.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Data not found, user not found.",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{billId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<BillDetail> getBillById(
            @PathVariable(name = "billId") String billId,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization");
        return ResponseEntity.ok(billService.getBillDetail(billId, token));
    }


    @DeleteMapping("/{billId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Void> deleteSession(
            @PathVariable(name = "billId") String billId,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization");
        billService.deleteSession(token, billId);
        return ResponseEntity.ok().build();
    }
}
