package com.payments.payment_service.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TaxCalculator {

    public BigDecimal calculateTax(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.14")).setScale(2, RoundingMode.HALF_UP);
    }
}
