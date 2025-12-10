package org.example.xpaymentadapter.repository;

import org.example.xpaymentadapter.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Queue;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {}
