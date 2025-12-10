package org.example.xpaymentadapter.client;


import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiRequest;
import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiResponse;

import java.util.Optional;
import java.util.UUID;


public interface XPaymentClient {
    XPaymentApiResponse createCharge(XPaymentApiRequest request);

    XPaymentApiResponse createChargeWithoutRetry(XPaymentApiRequest request);

    XPaymentApiResponse getCharge(UUID chargeId);

    XPaymentApiResponse createChargeFallback(Exception e, XPaymentApiRequest request);
}
