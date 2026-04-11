package com.payments.payment_service.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
public class AuditEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "beforeState")
    private String beforeState;

    @Column(name = "afterState")
    private String afterState;

    @Column(name = "triggered_by", nullable = false)
    private String triggeredBy;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    protected AuditEntry() {}

    public AuditEntry(
            String entityType, String entityId, String action,
            String beforeState, String afterState, String triggeredBy
    ) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.beforeState = beforeState;
        this.afterState = afterState;
        this.triggeredBy = triggeredBy;
        this.occurredAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getEntityType() { return entityType; }
    public String getEntityId() { return entityId; }
    public String getAction() { return action; }
    public String getBeforeState() { return beforeState; }
    public String getAfterState() { return afterState; }
    public String getTriggeredBy() { return triggeredBy; }
    public LocalDateTime getOccurredAt() { return occurredAt; }

}
