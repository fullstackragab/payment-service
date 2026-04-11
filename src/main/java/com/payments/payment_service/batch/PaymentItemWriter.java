package com.payments.payment_service.batch;

import com.payments.payment_service.domain.Payment;
import com.payments.payment_service.repository.PaymentRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class PaymentItemWriter implements ItemWriter<Payment> {

    private final PaymentRepository paymentRepository;

    public PaymentItemWriter(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void write(Chunk<? extends Payment> chunk) {
        System.out.println("Writing chunk of " + chunk.size() + " payments");
        paymentRepository.saveAll(chunk.getItems());
    }

}
