package com.payments.payment_service.batch;

import com.payments.payment_service.domain.Payment;
import com.payments.payment_service.domain.ReconciliationMismatch;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class ReconciliationProcessor implements ItemProcessor<Payment, ReconciliationMismatch> {

    private Map<String, NetworkSettlementRecord> networkRecords;

    @BeforeStep
    public void loadNetworkRecords(StepExecution stepExecution) {
        networkRecords = new HashMap<>();

        try {
            var resource = new ClassPathResource("network-settlement.csv");
            var reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream())
            );

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if(firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                String paymentId = parts[0].trim();
                BigDecimal amount = new BigDecimal(parts[1].trim());
                String currency = parts[2].trim();
                String status = parts[3].trim();

                networkRecords.put(paymentId, new NetworkSettlementRecord(paymentId, amount, currency, status));
            }

            System.out.println("[RECON] Loaded " + networkRecords.size()
                            + " records from network file");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load network settlement file", e);
        }
    }

    @Override
    public ReconciliationMismatch process(Payment payment) {
        NetworkSettlementRecord networkRecord = networkRecords.get(payment.getId());

        if (networkRecord == null) {
            System.out.println("[RECON] MISSING IN NETWORK: " + payment.getId());

            return new ReconciliationMismatch(payment.getId(), "MISSING_IN_NETWORK",
                    payment.getTotalCharged(), null);
        }

        if (payment.getTotalCharged().compareTo(networkRecord.amount()) != 0) {
            System.out.println("[RECON] AMOUNT MISMATCH: " + payment.getId()
                    + " internal=" + payment.getTotalCharged()
                    + " network=" + networkRecord.amount());

            return new ReconciliationMismatch(payment.getId(), "AMOUNT_MISMATCH", payment.getTotalCharged(), networkRecord.amount());
        }

        System.out.println("[RECON] MATCHED: " + payment.getId());
        return null;
    }
}
