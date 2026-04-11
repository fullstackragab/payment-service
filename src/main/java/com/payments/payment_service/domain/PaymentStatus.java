package com.payments.payment_service.domain;

import java.util.Set;

public enum PaymentStatus {
    INITIATED (Set.of()),
    VALIDATED (Set.of(INITIATED)),
    CANCELLED (Set.of(INITIATED, VALIDATED)),
    PROCESSING (Set.of(VALIDATED)),
    SETTLED (Set.of(PROCESSING)),
    FAILED (Set.of(PROCESSING, VALIDATED)),
    REVERSED (Set.of(SETTLED));

    private final Set<PaymentStatus> allowedPredecessors;

    PaymentStatus(Set<PaymentStatus> allowedPredecessors) {
        this.allowedPredecessors = allowedPredecessors;
    }

    public boolean canTransitionFrom(PaymentStatus current) {
        return allowedPredecessors.contains(current);
    }
}
