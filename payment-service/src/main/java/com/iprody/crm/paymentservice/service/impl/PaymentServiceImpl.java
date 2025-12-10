package com.iprody.crm.paymentservice.service.impl;

import com.iprody.crm.paymentservice.dto.request.CreatePaymentRequest;
import com.iprody.crm.paymentservice.dto.request.PaymentFilterRequest;
import com.iprody.crm.paymentservice.dto.request.PaymentUpdateRequest;
import com.iprody.crm.paymentservice.dto.response.PaymentResponse;
import com.iprody.crm.paymentservice.exception.PaymentNotFoundException;
import com.iprody.crm.paymentservice.mapper.PaymentMapper;
import com.iprody.crm.paymentservice.model.entity.Payment;
import com.iprody.crm.paymentservice.model.enums.Status;
import com.iprody.crm.paymentservice.repository.PaymentRepository;
import com.iprody.crm.paymentservice.repository.specification.PaymentSpecification;
import com.iprody.crm.paymentservice.service.PaymentService;
import com.iprody.crm.paymentservice.service.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final Validator<Pageable> pageableValidator;

    @Override
    @Transactional
    public PaymentResponse create(CreatePaymentRequest request) {

        Payment paymentToSave = paymentMapper.toEntity(request);
        paymentToSave.setStatus(Status.PENDING);

        Payment savedPayment = paymentRepository.save(paymentToSave);
        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    @Transactional
    public PaymentResponse update(UUID guid, PaymentUpdateRequest req) {
        return paymentRepository.findById(guid)
                .map(payment -> paymentMapper.updateEntity(req, payment))
                .map(paymentRepository::save)
                .map(paymentMapper::toResponse)
                .orElseThrow(() -> new PaymentNotFoundException(guid));
    }

    @Override
    public PaymentResponse findByGuid(UUID guid) {
        return paymentRepository.findById(guid)
                .map(paymentMapper::toResponse)
                .orElseThrow(() -> new PaymentNotFoundException(guid));
    }

    @Override
    public Page<PaymentResponse> findAll(
            Pageable page,
            PaymentFilterRequest filter) {
        pageableValidator.validate(page);
        return paymentRepository
                .findAll(PaymentSpecification.findAll(filter), page)
                .map(paymentMapper::toResponse);
    }
}
