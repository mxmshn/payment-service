package org.example.xpaymentadapter.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.xpaymentadapter.client.XPaymentClient;
import org.example.xpaymentadapter.mapper.PaymentMapper;
import org.example.xpaymentadapter.pojo.kafka.consumer.PaymentAdapterRequest;
import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiResponse;
import org.example.xpaymentadapter.repository.PaymentRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentProcessorServiceImpl implements PaymentProcessorService{
    private final XPaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Async
    @Override
    public void createPayment(PaymentAdapterRequest paymentAdapterRequest) {
            XPaymentApiResponse xPaymentApiResponse = paymentClient.createCharge(paymentMapper.toXPaymentApiRequest(paymentAdapterRequest));
            if (xPaymentApiResponse != null && xPaymentApiResponse.id() != null) {
                paymentRepository.save(paymentMapper.toPaymentEntity(xPaymentApiResponse, paymentAdapterRequest));
                log.info("Started tracking payment: {}",  xPaymentApiResponse.id());
            }
    }

}