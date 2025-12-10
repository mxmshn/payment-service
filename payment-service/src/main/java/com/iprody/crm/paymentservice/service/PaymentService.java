package com.iprody.crm.paymentservice.service;

import com.iprody.crm.paymentservice.dto.request.CreatePaymentRequest;
import com.iprody.crm.paymentservice.dto.request.PaymentFilterRequest;
import com.iprody.crm.paymentservice.dto.request.PaymentUpdateRequest;
import com.iprody.crm.paymentservice.dto.response.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse findByGuid(UUID guid);

    PaymentResponse create(CreatePaymentRequest request);

    PaymentResponse update(UUID guid, PaymentUpdateRequest request);

    Page<PaymentResponse> findAll(
            Pageable page,
            PaymentFilterRequest filter);
}
