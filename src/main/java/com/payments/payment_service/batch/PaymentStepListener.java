package com.payments.payment_service.batch;

import com.payments.payment_service.domain.PaymentStatus;
import com.payments.payment_service.repository.PaymentRepository;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentStepListener implements StepExecutionListener {

    private final PaymentRepository paymentRepository;

    public PaymentStepListener(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        long count = paymentRepository.findByStatus(PaymentStatus.INITIATED).size();
        System.out.println("[ProcessStep] Starting - will read " + count + " INITIATED payments");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("[processStep] Complete - Written: "
                + stepExecution.getWriteCount()
                + " | Skipped: " + stepExecution.getSkipCount()
        );

        return stepExecution.getExitStatus();
    }
}
