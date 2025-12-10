package org.example.xpaymentadapter.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.xpaymentadapter.client.XPaymentClient;
import org.example.xpaymentadapter.mapper.PaymentMapper;
import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiRequest;
import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiResponse;
import org.example.xpaymentadapter.rabbit.RabbitPaymentMessageService;
import org.example.xpaymentadapter.repository.PaymentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class DeadLetterQueueTracker {

    private final RabbitPaymentMessageService rabbitPaymentMessageService;
    private final XPaymentClient xPaymentClient;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Scheduled(fixedRateString = "${app.scheduler.dlq-processing-interval}")
    public void processDlqMessages() {

        int processed = 0;
        int successful = 0;
        List<XPaymentApiRequest> failedMessages = new ArrayList<>();

        XPaymentApiRequest message;
        while ((message = rabbitPaymentMessageService.receiveOneMessage()) != null) {
            processed++;

            try {
                XPaymentApiResponse response = xPaymentClient.createChargeWithoutRetry(message);

                if (isPaymentSuccessful(response)) {
                    paymentRepository.save(paymentMapper.toPaymentEntity(response, message));
                    successful++;
                    log.info("Payment created and saved to DB: {}", message.order());
                } else {
                    failedMessages.add(message);
                    log.info("Payment failed: {}", message.order());
                }

            } catch (Exception e) {
                failedMessages.add(message);
                log.info("Error processing: {}", e.getMessage());
            }
        }

        for (XPaymentApiRequest failedMessage : failedMessages) {
            rabbitPaymentMessageService.send(failedMessage);
        }
        log.info("DLQ processing successful/processed: {}/{} ", successful, processed);
    }

    private boolean isPaymentSuccessful(XPaymentApiResponse response) {
        return response != null && response.status() != null;
    }

}
