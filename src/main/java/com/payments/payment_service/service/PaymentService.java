package com.payments.payment_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.payment_service.audit.Audited;
import com.payments.payment_service.domain.OutboxEvent;
import com.payments.payment_service.domain.Payment;
import com.payments.payment_service.domain.PaymentStatus;
import com.payments.payment_service.dto.PaymentRequest;
import com.payments.payment_service.dto.PaymentResponse;
import com.payments.payment_service.event.PaymentEvent;
import com.payments.payment_service.repository.OutboxEventRepository;
import com.payments.payment_service.repository.PaymentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    private final FeeCalculator feeCalculator;
    private final TaxCalculator taxCalculator;
    private final PaymentRepository paymentRepository;

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public PaymentService(FeeCalculator feeCalculator, TaxCalculator taxCalculator,
                          PaymentRepository paymentRepository,
                          OutboxEventRepository outboxEventRepository) {
        this.feeCalculator = feeCalculator;
        this.taxCalculator = taxCalculator;
        this.paymentRepository = paymentRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Transactional
    public void processfraudResult(String paymentId, String fraudResult) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        if (!payment.getStatus().equals(PaymentStatus.INITIATED)) {
            System.out.println("[SAGA] Payment " + paymentId
                    + " already processed — skipping. Status: " + payment.getStatus());
            return; // idempotency — already transitioned
        }

        if (fraudResult.equals("FRAUD_CLEARED")) {
            payment.updateStatus(PaymentStatus.PROCESSING);
            paymentRepository.save(payment);
            System.out.println("[SAGA] Payment " + paymentId
                    + " → PROCESSING (fraud cleared)");

            // Write PAYMENT_PROCESSING event to outbox
            writeOutboxEvent(payment, "PAYMENT_PROCESSING");

        } else if (fraudResult.equals("FRAUD_FLAGGED")) {
            payment.updateStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            System.out.println("[SAGA] Payment " + paymentId
                    + " → FAILED (fraud flagged)");

            // Write PAYMENT_FAILED event to outbox
            writeOutboxEvent(payment, "PAYMENT_FAILED");
        }
    }

    private void writeOutboxEvent(Payment payment, String eventType) {
        try {
            PaymentEvent event = new PaymentEvent(
                    eventType,
                    payment.getId(),
                    payment.getIdempotencyKey(),
                    payment.getAmount(),
                    payment.getCurrency(),
                    payment.getSenderAccountId(),
                    payment.getReceiverAccountId(),
                    payment.getStatus().name(),
                    LocalDateTime.now()
            );
            String payload = objectMapper.writeValueAsString(event);
            outboxEventRepository.save(
                    new OutboxEvent(payment.getId(), eventType, payload)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to write outbox event: " + eventType, e);
        }
    }

    @Audited(action = "PAYMENT_CREATED", entityType = "Payment")
    @Transactional
    public PaymentResponse process(PaymentRequest request) {

        Optional<Payment> existing = paymentRepository.findByIdempotencyKey(request.idempotencyKey());
        if(existing.isPresent()) {
            System.out.println("Duplicate request detected for key: " + request.idempotencyKey() + " - returning original response");

            Payment p = existing.get();
            return new PaymentResponse(
                    p.getId(),
                    p.getStatus().name(),
                    p.getAmount(),
                    p.getFee(),
                    p.getTax(),
                    p.getTotalCharged(),
                    p.getCurrency(),
                    p.getDescription()
            );
        }


        BigDecimal amount = request.amount();
        BigDecimal fee = feeCalculator.calculateFee(amount);
        BigDecimal tax = taxCalculator.calculateTax(amount);
        BigDecimal total = amount.add(fee).add(tax);

        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                request.idempotencyKey(),
                amount,fee, tax, total,
                request.currency(),
                request.senderAccountId(),
                request.receiverAccountId(),
                request.description(),
                PaymentStatus.INITIATED
        );
        try {
            paymentRepository.save(payment);

        } catch (DataIntegrityViolationException e) {
            return paymentRepository.findByIdempotencyKey(request.idempotencyKey()).map(this::toResponse).orElseThrow(() -> new RuntimeException("Unexpected state"));
        }


        System.out.println("Processing payment: " + payment.getId());
        System.out.println("Amount: " + amount);
        System.out.println("Fee: " + fee);
        System.out.println("Tax: " + tax);
        System.out.println("Total: " + total);

        try {
            PaymentEvent event = new PaymentEvent(
                    "PAYMENT_INITIATED",
                    payment.getId(),
                    payment.getIdempotencyKey(),
                    payment.getAmount(),
                    payment.getCurrency(),
                    payment.getSenderAccountId(),
                    payment.getReceiverAccountId(),
                    payment.getStatus().name(),
                    LocalDateTime.now()
            );

            String payload = objectMapper.writeValueAsString(event);
            outboxEventRepository.save(new OutboxEvent(
                    payment.getId(), "PAYMENT_INITIATED", payload));

            System.out.println("Payment saved with outbox event: " + payment.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to write outbox event", e);
        }

        System.out.println("New payment created: " + payment.getId());

        return toResponse(payment);
    }

    @Audited(action = "PAYMENT_RETRIEVED", entityType = "Payment")
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String id) {
        return paymentRepository.findById(id).map(this::toResponse).orElseThrow(() -> new RuntimeException("Payment not found: " + id));
    }

    @Transactional
    public void testRollback() {
        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                "rollback-test-key",
                new BigDecimal("100.00"),
                new BigDecimal("1.50"),
                new BigDecimal("14.00"),
                new BigDecimal("115.50"),
                "USD", "account-A", "account-B", "rollback test",
                PaymentStatus.INITIATED
        );

        paymentRepository.save(payment);
        System.out.println("Payment saved - about to throw...");

        if(true) throw new RuntimeException("Something went wrong!");

        System.out.println("This never prints");
    }

    @Audited(action = "STATUS_CHANGE", entityType = "Payment")
    @Transactional
    public PaymentResponse updatePaymentStatus(String id, PaymentStatus newStatus) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found: " + id));

        payment.updateStatus(newStatus);
        paymentRepository.save(payment);

        return new PaymentResponse(
                payment.getId(),
                payment.getStatus().name(),
                payment.getAmount(),
                payment.getFee(),
                payment.getTax(),
                payment.getTotalCharged(),
                payment.getCurrency(),
                payment.getDescription()
        );
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getStatus().name(),
                p.getAmount(),
                p.getFee(),
                p.getTax(),
                p.getTotalCharged(),
                p.getCurrency(),
                p.getDescription()
        );
    }
}
