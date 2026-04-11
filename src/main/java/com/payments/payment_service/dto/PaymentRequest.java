package com.payments.payment_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest (

    @NotBlank(message = "IdempotencyKey is required")
    String idempotencyKey,

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    BigDecimal amount,

    @NotBlank(message = "Currency is required")
    String currency,

    String description,

    @NotBlank(message = "Sender account is required")
    String senderAccountId,

    @NotBlank(message = "Receiver account is required")
    String receiverAccountId

    ){}
