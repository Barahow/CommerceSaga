package dev.barahow.authentication_microservice.config;

import dev.barahow.authentication_microservice.filter.CustomAuthorizationFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class MockSecurityConfig {
    @Bean
    @Primary
    public CustomAuthorizationFilter mockAuthorizationFilter() {
        return mock(CustomAuthorizationFilter.class); // Neutralize real filter
    }
}
