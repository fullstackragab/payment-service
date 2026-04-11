package com.payments.payment_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MismatchResponse(
        String paymentId,
        String mismatchType,
        BigDecimal internalAmount,
        BigDecimal networkAmount,
        LocalDateTime detectedAt
) {
}
