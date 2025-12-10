package com.iprody.crm.paymentservice.api.controller;

import com.iprody.crm.paymentservice.api.PaymentApi;
import com.iprody.crm.paymentservice.config.AppConfigurationProperties;
import com.iprody.crm.paymentservice.dto.request.CreatePaymentRequest;
import com.iprody.crm.paymentservice.dto.request.PaymentFilterRequest;
import com.iprody.crm.paymentservice.dto.response.PaymentResponse;
import com.iprody.crm.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;
    private final AppConfigurationProperties configurationProperties;

    @Override
    @GetMapping("/{guid}")
    public ResponseEntity<PaymentResponse> findByGuid(
            @PathVariable @NotNull UUID guid) {
        return ResponseEntity.ok(paymentService.findByGuid(guid));
    }

    @Override
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid CreatePaymentRequest request) {

        return new ResponseEntity<>(
                paymentService.create(request),
                HttpStatus.CREATED);
    }

    @Override
    @GetMapping
    public Page<PaymentResponse> findPayments(
            @Valid PaymentFilterRequest filter,
            Pageable pageable) {
        return paymentService.findAll(pageable, filter);
    }
}
