package com.payments.payment_service.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FeeCalculator {
    public BigDecimal calculateFee(BigDecimal amount) {
        // 1.5% processing fee
        return amount.multiply(new BigDecimal("0.015"))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
