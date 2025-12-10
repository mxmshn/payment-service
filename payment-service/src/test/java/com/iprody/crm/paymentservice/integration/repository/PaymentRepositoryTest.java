package com.iprody.crm.paymentservice.integration.repository;

import com.iprody.crm.paymentservice.dto.request.PaymentFilterRequest;
import com.iprody.crm.paymentservice.factory.PaymentTestFactory;
import com.iprody.crm.paymentservice.integration.config.BaseIntegrationTest;
import com.iprody.crm.paymentservice.model.entity.Payment;
import com.iprody.crm.paymentservice.model.enums.Status;
import com.iprody.crm.paymentservice.repository.PaymentRepository;
import com.iprody.crm.paymentservice.repository.specification.PaymentSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class PaymentRepositoryTest extends BaseIntegrationTest {

    private static final int TEST_PAYMENT_COUNT = 10;

    @Autowired
    private PaymentRepository paymentRepository;

    private List<Payment> payments;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        payments = PaymentTestFactory.paymentsOfSize(TEST_PAYMENT_COUNT);
        paymentRepository.saveAll(payments);
        paymentRepository.flush();
    }

    @Test
    void initialDataLoaded() {
        long count = paymentRepository.count();
        assertEquals(payments.size(), count);
    }

    @Test
    void save_ShouldSavePayment() {
        Payment newPayment = PaymentTestFactory.payment();
        newPayment.setAmount(new BigDecimal("999.99"));
        newPayment.setCurrency("USD");
        newPayment.setStatus(Status.PENDING);
        newPayment.setNote("New test payment");

        Payment saved = paymentRepository.save(newPayment);
        paymentRepository.flush();

        Payment fromDb = paymentRepository.findById(saved.getGuid()).orElseThrow();

        assertAll(
                () -> assertNotNull(saved.getGuid()),
                () -> assertEquals(new BigDecimal("999.99"), fromDb.getAmount()),
                () -> assertEquals(Status.PENDING, fromDb.getStatus())
        );
    }

    @Test
    void save_ShouldUpdatePayment() {
        Payment paymentToUpdate = payments.get(0);
        paymentToUpdate.setStatus(Status.APPROVED);
        paymentToUpdate.setNote("Updated note");

        paymentRepository.save(paymentToUpdate);
        paymentRepository.flush();

        Payment fromDb = paymentRepository.findById(paymentToUpdate.getGuid()).orElseThrow();

        assertAll(
                () -> assertEquals(Status.APPROVED, fromDb.getStatus()),
                () -> assertEquals("Updated note", fromDb.getNote())
        );
    }

    @Test
    void deleteById_ShouldDeletePayment() {
        Payment paymentToDelete = payments.get(2);
        paymentRepository.deleteById(paymentToDelete.getGuid());
        paymentRepository.flush();

        assertThat(paymentRepository.existsById(paymentToDelete.getGuid())).isFalse();
    }

    @Test
    void findById_ShouldFindByGuid_ReturnsExpectedRecord() {
        Payment payment = payments.get(0);
        Payment fromDb = paymentRepository.findById(payment.getGuid()).orElseThrow();

        assertAll(
                () -> assertEquals(payment.getAmount(), fromDb.getAmount()),
                () -> assertEquals(payment.getCurrency(), fromDb.getCurrency()),
                () -> assertEquals(payment.getStatus(), fromDb.getStatus()),
                () -> assertNotNull(fromDb.getCreatedAt()),
                () -> assertNotNull(fromDb.getUpdatedAt())
        );
    }

    @Test
    void findAll_ShouldFindByStatus_PendingCount() {
        payments.get(0).setStatus(Status.PENDING);
        payments.get(1).setStatus(Status.PENDING);
        paymentRepository.saveAll(payments);
        paymentRepository.flush();

        PaymentFilterRequest filter = new PaymentFilterRequest(
                null,
                null,
                Status.PENDING,
                null,
                null,
                null
        );

        List<Payment> results = paymentRepository.findAll(PaymentSpecification.findAll(filter));
        assertThat(results).allMatch(p -> p.getStatus() == Status.PENDING);
    }

    @Test
    void findAll_ShouldFindTopByStatus_AndSortedByUpdatedAt() {
        Payment p1 = payments.get(0);
        Payment p2 = payments.get(1);
        Payment p3 = payments.get(2);

        p1.setStatus(Status.NOT_SENT);
        p2.setStatus(Status.NOT_SENT);
        p3.setStatus(Status.NOT_SENT);

        paymentRepository.saveAll(List.of(p1, p2, p3));
        paymentRepository.flush();

        PaymentFilterRequest filter = new PaymentFilterRequest(
                null,
                null,
                Status.NOT_SENT,
                null,
                null,
                null
        );

        var pageRequest = PageRequest.of(0, 50, Sort.by("updatedAt").ascending());

        var results = paymentRepository.findAll(PaymentSpecification.findAll(filter), pageRequest);

        assertAll(
                () -> assertThat(results.getContent())
                        .isSortedAccordingTo(Comparator.comparing(Payment::getUpdatedAt)),
                () -> assertThat(results.getContent())
                        .allMatch(p -> p.getStatus() == Status.NOT_SENT)
        );
    }
}
