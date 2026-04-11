package com.payments.payment_service.batch;

import com.payments.payment_service.domain.Payment;
import com.payments.payment_service.domain.PaymentStatus;
import com.payments.payment_service.repository.PaymentRepository;
import org.springframework.batch.item.ItemReader;

import java.util.Iterator;
import java.util.List;

public class PaymentItemReader implements ItemReader<Payment> {
    private final PaymentRepository paymentRepository;
    private Iterator<Payment> iterator;

    public PaymentItemReader(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment read() {
        if(iterator == null) {
            List<Payment> payments = paymentRepository.findByStatus(PaymentStatus.INITIATED);
            System.out.println("Batch job found " + payments.size() + " INITIATED payments to process");
            iterator = payments.iterator();
        }
        return iterator.hasNext() ? iterator.next() : null;
    }
}
