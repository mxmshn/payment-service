package com.iprody.crm.paymentservice.kafka.consumer;

import com.iprody.crm.paymentservice.event.PaymentAdapterEvent;
import com.iprody.crm.paymentservice.exception.error.ResourceNotFoundException;
import com.iprody.crm.paymentservice.kafka.producer.PaymentInquiryProducer;
import com.iprody.crm.paymentservice.mapper.PaymentMapper;
import com.iprody.crm.paymentservice.model.enums.Status;
import com.iprody.crm.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.iprody.crm.paymentservice.model.enums.Status.DECLINED;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentAdapterConsumer {

    private final PaymentRepository paymentRepository;
    private final PaymentInquiryProducer inquiryProducer;
    private final PaymentMapper paymentMapper;

    @KafkaListener(
            topics = "${spring.kafka.topics.payment-execution-response}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void onPaymentResponse(PaymentAdapterEvent response) {
        log.info("Received response from Adapter: {}", response);

        paymentRepository.findById(response.guid()).ifPresentOrElse(
                payment -> {
                    try {
                        switch (response.status()) {
                            case APPROVED -> {
                                if (payment.getStatus() != Status.APPROVED) {
                                    paymentRepository.updateStatus(
                                            payment.getGuid(),
                                            Status.APPROVED);
                                    inquiryProducer.send(
                                            paymentMapper.toEvent(payment));
                                } else {
                                    log.debug(
                                            "Payment {} already APPROVED,"
                                                    + " skipping duplicate",
                                            payment.getGuid()
                                    );
                                }
                            }
                            case DECLINED -> {
                                if (payment.getStatus() != DECLINED) {
                                    paymentRepository.updateStatus(
                                            payment.getGuid(), DECLINED);
                                } else {
                                    log.debug(
                                            "Payment {} already DECLINED,"
                                                    + " skipping duplicate",
                                            payment.getGuid()
                                    );
                                }
                            }
                            default -> log.warn(
                                    "Unprocessed status={} for guid={}",
                                    response.status(),
                                    response.guid()
                            );
                        }
                    } catch (Exception e) {
                        log.error(
                                "Failed to process response"
                                        + " for payment guid={}",
                                response.guid(),
                                e
                        );
                        throw e;
                    }
                }, () -> {
                    log.error(
                            "Payment with guid={} not found in database",
                            response.guid()
                    );
                    throw new ResourceNotFoundException(
                            "Payment not found in database",
                            response.guid()
                    );
                });
    }
}
