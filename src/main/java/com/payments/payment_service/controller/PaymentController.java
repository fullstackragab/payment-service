package com.payments.payment_service.controller;

import com.payments.payment_service.domain.AuditEntry;
import com.payments.payment_service.domain.PaymentStatus;
import com.payments.payment_service.dto.PaymentRequest;
import com.payments.payment_service.dto.PaymentResponse;
import com.payments.payment_service.repository.AuditRepository;
import com.payments.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final AuditRepository auditRepository;

    public PaymentController(PaymentService paymentService, AuditRepository auditRepository) {
        this.paymentService = paymentService;
        this.auditRepository = auditRepository;
    }

//    @GetMapping("/calculate")
//    public String calculate(@RequestParam String amount) {
//        BigDecimal total = paymentService.process(new BigDecimal(amount));
//        return "Total charged: " + total;
//    }

    @GetMapping("/{id}/audit")
    @ResponseStatus(HttpStatus.OK)
    public List<AuditEntry> getAuditTrial(@PathVariable String id) {
        return auditRepository.findByEntityIdOrderByOccurredAtAsc(id);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse updateStatus(@PathVariable String id,
                                        @RequestParam PaymentStatus newStatus) {
        return paymentService.updatePaymentStatus(id, newStatus);
    }

    @GetMapping("/test-rollback")
    public void testRollback() {
        paymentService.testRollback();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse getPayment(@PathVariable String id) {
        return paymentService.getPayment(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse initiatePayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.process(request);
    }
}
