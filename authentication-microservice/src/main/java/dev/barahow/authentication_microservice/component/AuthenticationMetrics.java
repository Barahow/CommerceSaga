package dev.barahow.authentication_microservice.component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
@Component
public class AuthenticationMetrics {
    private final Counter succesfulAccessCounter;
    private final Counter unAuthorizedAccessCounter;

    public AuthenticationMetrics(MeterRegistry meterRegistry) {
        this.succesfulAccessCounter =  Counter.builder("successful_access_total")
                .description("Total number of successful authentication attempts")
                .register(meterRegistry);
        this.unAuthorizedAccessCounter= Counter.builder("unAuthorized_access_total")
                .description("Total number of unauthorized access atempts")
                .register(meterRegistry);
    }

    public void incrementSuccessfulAccess(){
        succesfulAccessCounter.increment();
    }
    public void incrementUnauthorizedAccess(){
        unAuthorizedAccessCounter.increment();
    }
}
