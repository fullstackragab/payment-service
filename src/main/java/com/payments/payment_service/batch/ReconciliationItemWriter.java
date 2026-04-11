package com.payments.payment_service.batch;

import com.payments.payment_service.domain.ReconciliationMismatch;
import com.payments.payment_service.repository.ReconciliationMismatchRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class ReconciliationItemWriter implements ItemWriter<ReconciliationMismatch> {

    private final ReconciliationMismatchRepository mismatchRepository;

    public ReconciliationItemWriter(ReconciliationMismatchRepository mismatchRepository) {
        this.mismatchRepository = mismatchRepository;
    }

    @Override
    public void write(Chunk<? extends ReconciliationMismatch> chunk) {
        mismatchRepository.saveAll(chunk.getItems());
        System.out.println("[RECON] Saved " + chunk.size() + " mismatches to database");
    }
}
