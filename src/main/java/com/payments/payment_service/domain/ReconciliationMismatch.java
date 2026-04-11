package com.payments.payment_service.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reconcilation_mismatches")
public class ReconciliationMismatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "mismatch_type", nullable = false)
    private String mismatchType;

    @Column(name = "internal_amount", precision = 19, scale = 4)
    private BigDecimal internalAmount;

    @Column(name = "network_amount", precision = 19, scale = 4)
    private BigDecimal networkAmount;

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    @Column(name = "resolved", nullable = false)
    private boolean resolved;

    protected ReconciliationMismatch() {}

    public ReconciliationMismatch(String paymentId, String mismatchType,
                                  BigDecimal internalAmount, BigDecimal networkAmount) {
        this.paymentId = paymentId;
        this.mismatchType = mismatchType;
        this.internalAmount = internalAmount;
        this.networkAmount = networkAmount;
        this.detectedAt = LocalDateTime.now();
    }

    public String getPaymentId() { return this.paymentId; }
    public String getMismatchType() { return this.mismatchType; }
    public BigDecimal getInternalAmount() { return this.internalAmount; }
    public BigDecimal getNetworkAmount() { return this.networkAmount; }
    public LocalDateTime getDetectedAt() { return this.detectedAt; }
    public boolean isResolved() { return resolved; }

}
