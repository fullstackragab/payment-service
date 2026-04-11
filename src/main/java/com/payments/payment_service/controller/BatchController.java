package com.payments.payment_service.controller;

import com.payments.payment_service.dto.MismatchResponse;
import com.payments.payment_service.repository.ReconciliationMismatchRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/batch")
public class BatchController {
    private final JobLauncher jobLauncher;
    private final Job processPaymentsJob;
    private final Job reconciliationJob;
    private final ReconciliationMismatchRepository mismatchRepository;

    public BatchController(JobLauncher jobLauncher,
                           @Qualifier("processPaymentsJob") Job processPaymentsJob,
                           @Qualifier("reconciliationJob") Job reconciliationJob,
                           ReconciliationMismatchRepository mismatchRepository) {
        this.jobLauncher = jobLauncher;
        this.processPaymentsJob = processPaymentsJob;
        this.reconciliationJob = reconciliationJob;
        this.mismatchRepository = mismatchRepository;
    }

    @PostMapping("/run")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String runJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(processPaymentsJob, params);
        return "Batch job started";
    }

    @PostMapping("/reconcile")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String runReconciliation() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(reconciliationJob, params);
        return "Reconciliation job started";
    }

    @GetMapping("/mismatches")
    @ResponseStatus(HttpStatus.OK)
    public List<MismatchResponse> getMismatches() {
        return mismatchRepository.findByResolved(false)
                .stream()
                .map(m -> new MismatchResponse(
                        m.getPaymentId(),
                        m.getMismatchType(),
                        m.getInternalAmount(),
                        m.getNetworkAmount(),
                        m.getDetectedAt()
                )).toList();
    }
}
