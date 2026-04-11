package com.payments.payment_service.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentEventConsumer {

    @KafkaListener(topics = "payment.events", groupId = "fraud-scoring-service")
    public void onPaymentEvent(PaymentEvent event) {
        System.out.println("=== [FRAUD SERVICE] Received: "
                + event.eventType()
                + " | Payment: " + event.paymentId()
                + " | Amount: " + event.amount() + " " + event.currency());

        if (event.eventType().equals("PAYMENT_INITIATED")) {
            boolean suspicious = event.amount().compareTo(new BigDecimal("10000")) > 0;
            if (suspicious) {
                System.out.println("=== [FRAUD SERVICE] HIGH RISK payment flagged: " + event.paymentId());
            } else {
                System.out.println("=== [FRAUD SERVICE] Payment cleared: " + event.paymentId());
            }
        }
    }
}
