package com.payments.payment_service.dto;

import java.math.BigDecimal;

public record PaymentResponse (
        String paymentId,
        String status,
        BigDecimal amount,
        BigDecimal fee,
        BigDecimal tax,
        BigDecimal totalCharged,
        String currency,
        String description
) {
}
