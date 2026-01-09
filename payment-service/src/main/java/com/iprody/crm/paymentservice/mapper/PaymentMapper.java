package com.iprody.crm.paymentservice.mapper;

import com.iprody.crm.paymentservice.dto.request.CreatePaymentRequest;
import com.iprody.crm.paymentservice.dto.request.PaymentUpdateRequest;
import com.iprody.crm.paymentservice.dto.response.PaymentResponse;
import com.iprody.crm.paymentservice.event.PaymentAdapterEvent;
import com.iprody.crm.paymentservice.event.PaymentCreatedEvent;
import com.iprody.crm.paymentservice.model.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy =
                NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

    PaymentResponse toResponse(Payment entity);

    @Mapping(target = "guid", ignore = true)
    @Mapping(target = "transactionRefId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "inquiryRefId", ignore = true)
    Payment updateEntity(PaymentUpdateRequest dto,
                         @MappingTarget Payment entity);

    @Mapping(target = "guid", ignore = true)
    @Mapping(target = "transactionRefId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toEntity(CreatePaymentRequest dto);

    @Mapping(target = "guid", source = "entity.guid")
    @Mapping(target = "inquiryRefId", source = "entity.inquiryRefId")
    @Mapping(target = "amount", source = "entity.amount")
    @Mapping(target = "currency", source = "entity.currency")
    @Mapping(target = "customer", source = "dto.customer")
    @Mapping(target = "order", source = "dto.order")
    @Mapping(target = "receiptEmail", source = "dto.receiptEmail")
    PaymentCreatedEvent toEvent(Payment entity, CreatePaymentRequest dto);

    PaymentAdapterEvent toEvent(Payment entity);
}
