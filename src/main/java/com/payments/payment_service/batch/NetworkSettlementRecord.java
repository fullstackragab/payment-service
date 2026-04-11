package com.payments.payment_service.batch;

import java.math.BigDecimal;

public record NetworkSettlementRecord(
        String paymentId,
        BigDecimal amount,
        String currency,
        String status
) {
}
