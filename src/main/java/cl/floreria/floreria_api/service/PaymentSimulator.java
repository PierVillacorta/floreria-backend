package cl.floreria.floreria_api.service;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentSimulator {

    private final double failureRate;

    public PaymentSimulator(@Value("${app.payment.failure-rate}") double failureRate) {
        this.failureRate = failureRate;
    }

    //  Devuelve true si el cobro funciono. 
    public boolean charge(BigDecimal amount) {
        return ThreadLocalRandom.current().nextDouble() >= failureRate;
    }
}