package com.payments.payment_service.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventConsumer {

    @KafkaListener(topics = "payment.events", groupId = "notification-service")
    public void onPaymentEvent(PaymentEvent event) {

        if (event.eventType().equals("PAYMENT_INITIATED")) {
            System.out.println("[NOTIFICATION] SMS sent to sender for payment "
                    + event.paymentId() + ": "
                    + event.amount() + " "
                    + event.currency() + " initiated");
        }
    }
}
