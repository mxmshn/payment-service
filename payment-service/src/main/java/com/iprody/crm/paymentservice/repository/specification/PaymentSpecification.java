package com.iprody.crm.paymentservice.repository.specification;

import com.iprody.crm.paymentservice.dto.request.PaymentFilterRequest;
import com.iprody.crm.paymentservice.model.entity.Payment;
import com.iprody.crm.paymentservice.model.enums.Status;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class PaymentSpecification {

    private final String GUID = "guid";
    private final String INQUIRY_REF_ID = "inquiryRefId";
    private final String STATUS = "status";
    private final String CREATED_AT = "createdAt";

    public Specification<Payment> findAll(PaymentFilterRequest request) {

        Specification<Payment> specification = Specification
                .<Payment>unrestricted()
                .and(hasGuid(request.guid()))
                .and(hasInquiryRefId(request.inquiryRefId()))
                .and(hasStatus(request.status()));

        if (request.createdAt() != null) {
            specification = specification
                    .and(equalCreatedDate(request.createdAt()));
        } else {
            specification = specification
                    .and(createdAfter(request.createdFrom()))
                    .and(createdBefore(request.createdTo()));
        }
        return specification;
    }

    public Specification<Payment> hasGuid(UUID guid) {
        return (root, query, cb) ->
                guid == null
                        ? cb.conjunction()
                        : cb.equal(root.get(GUID), guid);
    }

    public Specification<Payment> hasInquiryRefId(UUID inquiryRefId) {
        return (root, query, cb) ->
                inquiryRefId == null
                        ? cb.conjunction()
                        : cb.equal(root.get(INQUIRY_REF_ID), inquiryRefId);
    }

    public Specification<Payment> hasStatus(Status status) {
        return (root, query, cb) ->
                status == null
                        ? cb.conjunction()
                        : cb.equal(root.get(STATUS), status);
    }

    public Specification<Payment> createdBefore(LocalDateTime end) {
        return (root, query, cb) ->
                end == null
                        ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get(CREATED_AT), end);
    }

    public Specification<Payment> createdAfter(LocalDateTime start) {
        return (root, query, cb) ->
                start == null
                        ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get(CREATED_AT), start);
    }

    public Specification<Payment> equalCreatedDate(LocalDateTime date) {
        return (root, query, cb) ->
                date == null
                        ? cb.conjunction()
                        : cb.equal(root.get(CREATED_AT), date);
    }
}
