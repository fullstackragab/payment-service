package com.payments.payment_service.external;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ExchangeRateService {

    // Simulates call counter to trigger failures
    private final AtomicInteger callCount = new AtomicInteger(0);

    @CircuitBreaker(name = "exchangeRateService", fallbackMethod = "getDefaultRate")
    @Retry(name = "exchangeRateService")
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        int count = callCount.incrementAndGet();

        // Simulate: every 3rd and 4th call fails (mimics flaky external service)
        if (count % 5 == 3 || count % 5 == 4) {
            System.out.println("[EXCHANGE RATE] Simulating failure on call #" + count);
            throw new RuntimeException("Exchange rate service unavailable");
        }

        // Simulate successful response
        BigDecimal rate = new BigDecimal("1.085"); // USD to EUR
        System.out.println("[EXCHANGE RATE] Rate fetched successfully on call #"
                + count + ": " + rate);
        return rate;
    }

    // Fallback — called when circuit is open or call fails
    public BigDecimal getDefaultRate(String fromCurrency, String toCurrency,
                                     Throwable throwable) {
        System.out.println("[EXCHANGE RATE] Circuit open or call failed — "
                + "using default rate. Reason: " + throwable.getMessage());
        return new BigDecimal("1.000"); // safe fallback
    }
}