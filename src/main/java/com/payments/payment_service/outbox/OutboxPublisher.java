package com.payments.payment_service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.payments.payment_service.domain.OutboxEvent;
import com.payments.payment_service.event.PaymentEvent;
import com.payments.payment_service.event.PaymentEventPublisher;
import com.payments.payment_service.repository.OutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(OutboxEventRepository outboxEventRepository,
                           PaymentEventPublisher paymentEventPublisher) {
        this.outboxEventRepository = outboxEventRepository;
        this.paymentEventPublisher = paymentEventPublisher;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Scheduled(fixedDelay = 10000) // runs every 1 second
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pending =
                outboxEventRepository.findByPublishedFalseOrderByCreatedAtAsc();

        if (pending.isEmpty()) return;

        System.out.println("[OUTBOX] Found " + pending.size() + " pending events");

        for (OutboxEvent outboxEvent : pending) {
            try {
                PaymentEvent event = objectMapper.readValue(
                        outboxEvent.getPayload(), PaymentEvent.class);

                paymentEventPublisher.publish(event);
                outboxEvent.markPublished();
                outboxEventRepository.save(outboxEvent);

                System.out.println("[OUTBOX] Published and marked: "
                        + outboxEvent.getEventType()
                        + " for payment " + outboxEvent.getPaymentId());

            } catch (Exception e) {
                System.err.println("[OUTBOX] Failed to publish event "
                        + outboxEvent.getId() + ": " + e.getMessage());
                // Don't rethrow — skip this event, try again next cycle
            }
        }
    }
}