package com.payments.payment_service.repository;

import com.payments.payment_service.domain.AuditEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntry, Long> {
    List<AuditEntry> findByEntityIdOrderByOccurredAtAsc(String entityId);
}
