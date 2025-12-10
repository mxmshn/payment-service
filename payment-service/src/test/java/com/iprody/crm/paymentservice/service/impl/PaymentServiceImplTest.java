package com.iprody.crm.paymentservice.service.impl;

import com.iprody.crm.paymentservice.dto.request.CreatePaymentRequest;
import com.iprody.crm.paymentservice.dto.request.PaymentFilterRequest;
import com.iprody.crm.paymentservice.dto.request.PaymentUpdateRequest;
import com.iprody.crm.paymentservice.dto.response.PaymentResponse;
import com.iprody.crm.paymentservice.exception.PaymentNotFoundException;
import com.iprody.crm.paymentservice.factory.PaymentTestFactory;
import com.iprody.crm.paymentservice.mapper.PaymentMapper;
import com.iprody.crm.paymentservice.model.entity.Payment;
import com.iprody.crm.paymentservice.model.enums.Status;
import com.iprody.crm.paymentservice.repository.PaymentRepository;
import com.iprody.crm.paymentservice.service.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private Validator<Pageable> pageableValidator;

    @Spy
    private PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void createPayment_ShouldReturnResponseWithPendingStatus() {
        CreatePaymentRequest request = PaymentTestFactory.createRequest();
        Payment savedPayment = PaymentTestFactory.savedPayment();

        when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(savedPayment);
        PaymentResponse response = paymentService.create(request);

        assertAll(
                () -> verify(paymentMapper).toEntity(request),
                () -> verify(paymentRepository).save(Mockito.any(Payment.class)),
                () -> assertEquals(request.inquiryRefId(), response.inquiryRefId()),
                () -> assertEquals(Status.PENDING, response.status())
        );
    }

    @Test
    void updatePayment_ShouldUpdateExistingPayment() {
        UUID guid = UUID.randomUUID();
        PaymentUpdateRequest updateRequest = PaymentTestFactory.updateRequest();
        Payment existing = PaymentTestFactory.payment();

        when(paymentRepository.findById(guid)).thenReturn(Optional.of(existing));
        when(paymentRepository.save(existing)).thenReturn(existing);

        PaymentResponse response = paymentService.update(guid, updateRequest);

        assertAll(
                () -> verify(paymentRepository).findById(guid),
                () -> verify(paymentRepository).save(existing),
                () -> verify(paymentMapper).updateEntity(updateRequest, existing),
                () -> assertEquals(updateRequest.amount(), response.amount()),
                () -> assertEquals(updateRequest.currency(), response.currency()),
                () -> assertEquals(updateRequest.status(), response.status()),
                () -> assertEquals(updateRequest.note(), response.note())
        );
    }

    @Test
    void updatePayment_WhenNotFound_ShouldThrow() {
        UUID guid = UUID.randomUUID();
        PaymentUpdateRequest updateRequest = new PaymentUpdateRequest(
                BigDecimal.valueOf(100), "USD", Status.PENDING, "Note"
        );

        when(paymentRepository.findById(guid)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.update(guid, updateRequest));
    }

    @Test
    void findByGuid_ShouldReturnPaymentResponse() {
        UUID guid = UUID.randomUUID();
        Payment payment = PaymentTestFactory.payment();

        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));

        paymentService.findByGuid(guid);

        assertAll(
                () -> verify(paymentRepository).findById(guid),
                () -> verify(paymentMapper).toResponse(payment)
        );
    }

    @Test
    void findByGuid_WhenNotFound_ShouldThrow() {
        UUID guid = UUID.randomUUID();
        when(paymentRepository.findById(guid)).thenReturn(Optional.empty());
        assertThrows(PaymentNotFoundException.class, () -> paymentService.findByGuid(guid));
    }

    @Test
    void findAllPayments_ShouldReturnMappedResponses() {
        PaymentFilterRequest filter = new PaymentFilterRequest(null, null, null, null, null, null);
        PageRequest pageRequest = PageRequest.of(0, 5);

        List<Payment> payments = PaymentTestFactory.paymentsOfSize(3);
        Page<Payment> paymentPage = new PageImpl<>(payments);

        when(paymentRepository
                .findAll(Mockito.<Specification<Payment>>any(), Mockito.eq(pageRequest)))
                .thenReturn(paymentPage);

        paymentService.findAll(pageRequest, filter);
        verify(paymentRepository).findAll(Mockito.<Specification<Payment>>any(), Mockito.eq(pageRequest));
    }
}
