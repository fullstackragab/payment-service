package com.payments.payment_service.batch;

import com.payments.payment_service.domain.Payment;
import com.payments.payment_service.domain.PaymentStatus;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class PaymentSettlementProcessor implements ItemProcessor<Payment, Payment> {

    @Override
    public Payment process(Payment payment) {
        System.out.println("Settling payment: " + payment.getId());
        payment.updateStatus(PaymentStatus.SETTLED);
        return payment;
    }
}
