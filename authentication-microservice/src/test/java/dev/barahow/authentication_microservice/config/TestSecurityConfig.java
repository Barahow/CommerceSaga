package dev.barahow.authentication_microservice.config;

import dev.barahow.authentication_microservice.filter.CustomPermissionEvaluator;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
@EnableMethodSecurity(prePostEnabled = true)
public class TestSecurityConfig {
    @Bean
    @Primary
    public CustomPermissionEvaluator customPermissionEvaluator() {
        CustomPermissionEvaluator mock = mock(CustomPermissionEvaluator.class);
        // Default to granting permission unless overridden in tests
        when(mock.hasPermission(any(), any(), any(), any())).thenReturn(true);
        return mock;
    }


    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return mock(UserDetailsService.class);
    }

}
