package com.payments.payment_service.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FraudResultEvent(
        String eventType,
        String paymentId,
        String riskLevel,
        int riskScore,
        String reason,
        BigDecimal amount,
        String currency,
        LocalDateTime occurredAt
) {}