package com.iprody.crm.paymentservice.event.listener;

import com.iprody.crm.paymentservice.event.PaymentCreatedEvent;
import com.iprody.crm.paymentservice.kafka.producer.PaymentAdapterProducer;
import com.iprody.crm.paymentservice.model.enums.Status;
import com.iprody.crm.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCreatedEventListener {

    private final PaymentRepository paymentRepository;
    private final PaymentAdapterProducer executionProducer;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentCreatedEvent event) {
        try {
            SendResult<String, PaymentCreatedEvent> sendResult =
                    executionProducer.send(event).get();
            log.info(
                    "Sent payment {} successfully, offset={}",
                    event.guid(),
                    sendResult.getRecordMetadata().offset()
            );
            paymentRepository.updateStatus(event.guid(), Status.PENDING);
        } catch (ExecutionException e) {
            log.error("Kafka send failed", e);
        } catch (InterruptedException e) {
            log.error("Kafka send interrupted", e);
        }
    }
}
