package com.payments.payment_service.batch;

import com.payments.payment_service.domain.Payment;
import com.payments.payment_service.domain.PaymentStatus;
import com.payments.payment_service.repository.PaymentRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class ReconciliationItemReader implements ItemReader<Payment> {

    private final PaymentRepository paymentRepository;
    private Iterator<Payment> iterator;

    public ReconciliationItemReader(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment read() {
        if(iterator == null) {
            List<Payment> settled = paymentRepository.findByStatus(PaymentStatus.SETTLED);
            System.out.println("[RECON] Found " + settled.size() + " SETTLED payments to reconcile");
            iterator = settled.iterator();
        }
        return iterator.hasNext() ? iterator.next() : null;
    }
}
