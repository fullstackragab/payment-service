package com.payments.payment_service.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentEvent (
    String eventType,
    String paymentId,
    String idempotencyKey,
    BigDecimal amount,
    String currency,
    String senderAccountId,
    String receiverAccountId,
    String status,
    LocalDateTime occurredAt
    ) {}
