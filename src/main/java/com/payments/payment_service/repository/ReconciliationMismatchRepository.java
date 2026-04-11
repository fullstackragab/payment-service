package com.payments.payment_service.repository;

import com.payments.payment_service.domain.ReconciliationMismatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReconciliationMismatchRepository extends JpaRepository<ReconciliationMismatch, Long> {
    List<ReconciliationMismatch> findByResolved(boolean resolved);
}
