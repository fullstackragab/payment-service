package com.payments.payment_service.batch;

import com.payments.payment_service.domain.Payment;
import com.payments.payment_service.domain.ReconciliationMismatch;
import com.payments.payment_service.repository.PaymentRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class PaymentBatchConfig {

    @Bean
    @StepScope
    public PaymentItemReader paymentItemReader(PaymentRepository paymentRepository) {
        return new PaymentItemReader(paymentRepository);
    }

    @Bean
    @StepScope
    public SettlementItemReader settlementItemReader(PaymentRepository paymentRepository) {
        return new SettlementItemReader(paymentRepository);
    }

    @Bean
    @StepScope
    public ReconciliationItemReader reconciliationItemReader(PaymentRepository paymentRepository) {
        return new ReconciliationItemReader(paymentRepository);
    }

    @Bean
    public Step reconciliationStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   ReconciliationItemReader reader,
                                   ReconciliationProcessor processor,
                                   ReconciliationItemWriter writer) {
        return new StepBuilder("reconciliationStep", jobRepository)
                .<Payment, ReconciliationMismatch>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job reconciliationJob(JobRepository jobRepository,
                                  Step reconciliationStep,
                                  PaymentJobListener listener) {
        return new JobBuilder("reconciliationJob", jobRepository)
                .listener(listener)
                .start(reconciliationStep)
                .build();
    }

    @Bean
    public Step processPaymentsStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    PaymentItemReader reader,
                                    PaymentItemProcessor processor,
                                    PaymentItemWriter writer,
                                    PaymentStepListener listener
                                    ) {
        return new StepBuilder("processPaymentsStep", jobRepository)
                .<Payment, Payment>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(IllegalStateException.class)
                .skipLimit(10)
                .listener(listener)
                .build();
    }

    @Bean
    public Step settlementStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               SettlementItemReader reader,
                               PaymentSettlementProcessor processor,
                               PaymentItemWriter writer) {
        return new StepBuilder("settlementStep", jobRepository)
                .<Payment, Payment>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retry(org.springframework.dao.TransientDataAccessException.class)
                .retryLimit(3)
                .build();
    }

    @Bean
    public Job processPaymentsJob(JobRepository jobRepository,
                                  Step processPaymentsStep,
                                  Step settlementStep,
                                  PaymentJobListener listener) {
        return new JobBuilder("processPaymentsJob", jobRepository)
                .listener(listener)
                .start(processPaymentsStep)
                .next(settlementStep)
                .build();
    }
}
