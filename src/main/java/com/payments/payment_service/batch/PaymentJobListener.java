package com.payments.payment_service.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class PaymentJobListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("=== BATCH JOB STARTING "
                        + jobExecution.getJobInstance().getJobName()
                        + " | Run ID: " + jobExecution.getJobId() + " ==="
                );
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Long durationMs = null;
        if (jobExecution.getStartTime() != null && jobExecution.getEndTime() != null) {
            durationMs = Duration.between(
                    jobExecution.getStartTime(),
                    jobExecution.getEndTime()
            ).toMillis();
        }

        System.out.println("=== BATCH JOB FINISHED: "
                + jobExecution.getJobInstance().getJobName()
                + " | Status: " + jobExecution.getStatus()
                + " | Duration: "
                + (durationMs != null ? durationMs + "ms" : "N/A")
                + " ===");


        if(jobExecution.getStatus().isUnsuccessful()) {
            System.out.println("ALERT: Batch job failed - ops team notified");
        }
    }
}
