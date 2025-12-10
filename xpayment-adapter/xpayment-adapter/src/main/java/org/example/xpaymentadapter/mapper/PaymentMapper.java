package org.example.xpaymentadapter.mapper;

import org.example.xpaymentadapter.entity.Payment;
import org.example.xpaymentadapter.pojo.kafka.consumer.PaymentAdapterRequest;
import org.example.xpaymentadapter.pojo.kafka.producer.PaymentAdapterResponse;
import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiRequest;
import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, implementationName = "PaymentMapperImpl") //
public interface PaymentMapper {

    @Mapping(source = "originalRequest.guid", target = "guid")
    @Mapping(source = "originalRequest.inquiryRefId", target = "inquiryRefId")
    @Mapping(source = "xPaymentApiResponse.amount", target = "amount")
    @Mapping(source = "xPaymentApiResponse.currency", target = "currency")
    @Mapping(source = "xPaymentApiResponse.id", target = "transactionRefId")
    @Mapping(source = "xPaymentApiResponse.status", target = "status")
    PaymentAdapterResponse toPaymentAdapterResponse(PaymentAdapterRequest originalRequest, XPaymentApiResponse xPaymentApiResponse);

    @Mapping(source = "guid", target = "guid")
    @Mapping(source = "inquiryRefId", target = "inquiryRefId")
    @Mapping(source = "amount", target = "amount", qualifiedByName = "bigDecimalToString")
    @Mapping(source = "currency", target = "currency")
    @Mapping(source = "inquiryRefId", target = "transactionRefId")
    @Mapping(source = "status", target = "status")
    PaymentAdapterResponse toPaymentAdapterResponse(Payment payment);

    @Mapping(source = "xPaymentApiResponse.id", target = "paymentId")
    @Mapping(source = "request.guid", target = "guid")
    @Mapping(source = "request.inquiryRefId", target = "inquiryRefId")
    @Mapping(source = "xPaymentApiResponse.amount", target = "amount", qualifiedByName = "stringToBigDecimal")
    @Mapping(source = "xPaymentApiResponse.currency", target = "currency")
    @Mapping(source = "xPaymentApiResponse.status", target = "status")
    Payment toPaymentEntity(XPaymentApiResponse xPaymentApiResponse, PaymentAdapterRequest request);

    @Mapping(source = "response.id", target = "paymentId")
    @Mapping(source = "paymentRequest.metadata", target = "guid", qualifiedByName = "metadataToGuid")
    @Mapping(source = "paymentRequest.metadata", target = "inquiryRefId", qualifiedByName = "metadataToInquiryRefId")
    @Mapping(source = "response.amount", target = "amount", qualifiedByName = "stringToBigDecimal")
    @Mapping(source = "response.currency", target = "currency")
    @Mapping(source = "response.status", target = "status")
    Payment toPaymentEntity(XPaymentApiResponse response, XPaymentApiRequest paymentRequest);

    @Mapping(source = "amount", target = "amount", qualifiedByName = "bigDecimalToString")
    @Mapping(source = "guid", target = "order")
    @Mapping(source = "currency", target = "currency")
    @Mapping(source = "customer", target = "customer")
    @Mapping(source = "receiptEmail", target = "receiptEmail")
    @Mapping(target = "metadata", expression = "java(createMetadata(paymentAdapterRequest))")
    XPaymentApiRequest toXPaymentApiRequest(PaymentAdapterRequest paymentAdapterRequest);

    default Map<String, String> createMetadata(PaymentAdapterRequest paymentAdapterRequest) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("inquiryRefId", paymentAdapterRequest.inquiryRefId().toString());
        metadata.put("paymentServiceGuid", paymentAdapterRequest.guid().toString());
        return metadata;
    }

    @Named("metadataToGuid")
    default UUID metadataToGuid(Map<String, String> metadata) {
        try {
            return metadata != null && metadata.containsKey("paymentServiceGuid") ? UUID.fromString(metadata.get("paymentServiceGuid")) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("metadataToInquiryRefId")
    default UUID metadataToInquiryRefId(Map<String, String> metadata) {
        try {
            return metadata != null && metadata.containsKey("inquiryRefId") ? UUID.fromString(metadata.get("inquiryRefId")) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("stringToBigDecimal")
    default BigDecimal stringToBigDecimal(String amount) {
        try {
            return amount != null ? new BigDecimal(amount) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Named("bigDecimalToString")
    default String bigDecimalToString(BigDecimal amount) {
        return amount != null ? amount.toString() : null;
    }
}
