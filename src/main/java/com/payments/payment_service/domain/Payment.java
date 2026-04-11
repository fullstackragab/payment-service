package com.payments.payment_service.domain;

import com.payments.payment_service.service.PaymentService;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "fee", nullable = false, precision = 19, scale = 4)
    private BigDecimal fee;

    @Column(name = "tax", nullable = false, precision = 19, scale = 4)
    private BigDecimal tax;

    @Column(name = "total_charged", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalCharged;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "sender_account_id", nullable = false)
    private String senderAccountId;

    @Column(name = "receiver_account_id", nullable = false)
    private String receiverAccountId;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Payment() {}

    public Payment(String id, String idempotencyKey, BigDecimal amount, BigDecimal fee, BigDecimal tax,
                   BigDecimal totalCharged, String currency, String senderAccountId, String receiverAccountId,
                   String description, PaymentStatus status) {
        this.id = id;
        this.idempotencyKey = idempotencyKey;
        this.amount = amount;
        this.fee = fee;
        this.tax = tax;
        this.totalCharged = totalCharged;
        this.currency = currency;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.description = description;
        this.status = status;
    }

    public String getId() { return id;}
    public String getIdempotencyKey() { return idempotencyKey; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getFee() {return  fee;}
    public BigDecimal getTax() {return tax;}
    public BigDecimal getTotalCharged() {return  totalCharged;}
    public String getCurrency() { return currency; }
    public String getSenderAccountId() { return senderAccountId; }
    public String getReceiverAccountId() { return  receiverAccountId; }
    public String getDescription() { return  description; }
    public PaymentStatus getStatus() { return  status; }
    public LocalDateTime getCreatedAt() {return  createdAt;}

    public void updateStatus(PaymentStatus newStatus) {
        this.status = newStatus;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
