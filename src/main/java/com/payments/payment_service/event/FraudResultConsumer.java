package com.payments.payment_service.event;

import com.payments.payment_service.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FraudResultConsumer {

    private final PaymentService paymentService;

    public FraudResultConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(
            topics = "fraud.results",
            groupId = "payment-service-saga",
            containerFactory = "fraudResultKafkaListenerContainerFactory"
    )
    public void onFraudResult(FraudResultEvent event) {
        System.out.println("[SAGA] Received fraud result: "
                + event.eventType()
                + " for payment: " + event.paymentId()
                + " risk=" + event.riskLevel());

        paymentService.processfraudResult(event.paymentId(), event.eventType());
    }
}