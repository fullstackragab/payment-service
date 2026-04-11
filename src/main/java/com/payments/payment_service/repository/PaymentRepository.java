package com.payments.payment_service.repository;

import com.payments.payment_service.domain.Payment;
import com.payments.payment_service.domain.PaymentStatus;
import com.payments.payment_service.dto.PaymentResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
    List<Payment> findByStatus(PaymentStatus status);
}
