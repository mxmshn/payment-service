package com.iprody.crm.paymentservice.factory;

import com.iprody.crm.paymentservice.dto.request.CreatePaymentRequest;
import com.iprody.crm.paymentservice.dto.request.PaymentUpdateRequest;
import com.iprody.crm.paymentservice.dto.response.PaymentResponse;
import com.iprody.crm.paymentservice.model.entity.Payment;
import com.iprody.crm.paymentservice.model.enums.Status;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class PaymentTestFactory {

    private final UUID GUID = UUID.fromString("513fcecf-06a6-4574-920d-6af0478279e9");
    private final UUID INQUIRY_REF_ID = UUID.fromString("e888cda3-ae2f-432a-9ba9-63532f07c454");

    public CreatePaymentRequest createRequest() {
        return new CreatePaymentRequest(
                INQUIRY_REF_ID,
                BigDecimal.valueOf(1000),
                "USD",
                "Test payment");
    }

    public PaymentResponse paymentResponse() {
        return new PaymentResponse(
                GUID,
                INQUIRY_REF_ID,
                BigDecimal.valueOf(1000),
                "USD",
                Status.PENDING,
                "Test payment",
                LocalDateTime.now());
    }

    public Payment savedPayment() {
        Payment payment = payment();
        payment.setGuid(GUID);
        payment.setInquiryRefId(INQUIRY_REF_ID);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        return payment;
    }

    public PaymentUpdateRequest updateRequest() {
        return new PaymentUpdateRequest(
                BigDecimal.valueOf(500),
                "USD",
                Status.APPROVED,
                "Updated note");
    }

    public List<Payment> paymentsOfSize(int size) {
        List<Payment> payments = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            payments.add(payment());
        }
        return payments;
    }

    public Payment payment() {
        Payment payment = new Payment();

        payment.setInquiryRefId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("1000"));
        payment.setCurrency("USD");
        payment.setStatus(Status.PENDING);
        payment.setNote("Test payment");
        return payment;
    }
}
