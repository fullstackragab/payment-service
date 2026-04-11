package com.payments.payment_service.batch;

import com.payments.payment_service.domain.Payment;
import com.payments.payment_service.domain.PaymentStatus;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentItemProcessor implements ItemProcessor<Payment, Payment> {

    @Override
    public Payment process(Payment payment) {
        System.out.println("Processing payment: " + payment.getId());

        if(payment.getAmount().compareTo(new BigDecimal("999.00")) == 0) {
            System.out.println("SKIPPING suspicious payment: " + payment.getId());
            throw new IllegalStateException("Suspicious amount detected: " + payment.getAmount());
        }

        payment.updateStatus(PaymentStatus.PROCESSING);

        return payment;
    }
}
