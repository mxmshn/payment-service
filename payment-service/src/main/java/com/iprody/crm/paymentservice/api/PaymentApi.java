package com.iprody.crm.paymentservice.api;

import com.iprody.crm.paymentservice.dto.request.CreatePaymentRequest;
import com.iprody.crm.paymentservice.dto.request.PaymentFilterRequest;
import com.iprody.crm.paymentservice.dto.response.ErrorResponse;
import com.iprody.crm.paymentservice.dto.response.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Payments",
        description = "Payment management API")
public interface PaymentApi {

    @Operation(
            operationId = "findByGuid",
            summary = "Get payment details by GUID",
            description = "Returns payment details by unique GUID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description =
                            "Successful operation. Returns payment details",
                    content = @Content(schema = @Schema(
                            implementation = PaymentResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data.",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class)))
    })
    ResponseEntity<PaymentResponse> findByGuid(
            @Parameter(description = "Payment ID", required = true) UUID guid
    );

    @Operation(
            operationId = "createPayment",
            summary = "Create a new payment",
            description = "Creates a new payment for an inquiry")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Payment created successfully",
                    content = @Content(schema = @Schema(
                            implementation = PaymentResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class)))
    })
    ResponseEntity<PaymentResponse> createPayment(CreatePaymentRequest req);

    @Operation(
            summary = "Get payments in a paginated format",
            description = """
                    Retrieves a paginated list of payments based
                    on the provided filters.
                    Rules:
                    - createdAt: exact date, cannot be
                    combined with createdFrom/createdTo
                    - createdFrom / createdTo: date range,
                     createdFrom <= createdTo
                    - Dates cannot be in the future"""
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Successfully operations. Returns
                            payments in paginated list""",
                    content = @Content(
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(
                            implementation = ErrorResponse.class))
            )
    })
    @Parameters({
            @Parameter(
                    name = "page",
                    in = ParameterIn.QUERY,
                    description = "Zero-based page index (0..N)",
                    schema = @Schema(type = "integer",
                            defaultValue = "0", minimum = "0")
            ),
            @Parameter(
                    name = "size",
                    in = ParameterIn.QUERY,
                    description = "Page size. Allowed values: 10, 25, 50",
                    schema = @Schema(type = "integer",
                            defaultValue = "10",
                            allowableValues = {"10", "25", "50"})
            ),
            @Parameter(
                    name = "sort",
                    in = ParameterIn.QUERY,
                    description = """
                            Sorting criteria: property,(asc|desc).
                            Allowed properties: createdAt, status.
                            Example: createdAt,desc""",
                    schema = @Schema(type = "string")
            )
    })
    Page<PaymentResponse> findPayments(
            @ParameterObject
            PaymentFilterRequest filter,
            @Parameter(hidden = true)
            Pageable pageable);
}
