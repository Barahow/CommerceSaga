package dev.barahow.authentication_microservice.config;

import dev.barahow.authentication_microservice.Service.UserAuthenticationService;
import dev.barahow.authentication_microservice.component.JwtTokenProvider;
import dev.barahow.authentication_microservice.filter.CustomAuthenticationFilter;
import dev.barahow.authentication_microservice.filter.CustomAuthorizationFilter;
import dev.barahow.authentication_microservice.filter.CustomPermissionEvaluator;
import dev.barahow.authentication_microservice.security.CustomAccessDeniedHandler;
import dev.barahow.core.types.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.print.attribute.standard.Media;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    private final UserDetailsService userDetailsService;

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final CustomPermissionEvaluator customPermissionEvaluator;

    public SecurityConfig(UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder, CustomPermissionEvaluator customPermissionEvaluator) {
        this.userDetailsService = userDetailsService;

        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.customPermissionEvaluator = customPermissionEvaluator;
    }

    // configure method security expressions
    // this should make sure we get our custom permission
    // because registered as a bean
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(customPermissionEvaluator);
        return handler;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }




    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,AuthenticationManager authManager) throws Exception {


        CustomAuthenticationFilter custumAuthFilter = new CustomAuthenticationFilter(authManager, jwtTokenProvider);


            http
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/v1/login", "/api/v1/registration","/actuator/prometheus","/actuator/health","/actuator/info").permitAll()
                            .anyRequest().authenticated()
                    )
                    .addFilterBefore(custumAuthFilter, UsernamePasswordAuthenticationFilter.class)
                  .addFilterBefore(new CustomAuthorizationFilter(jwtTokenProvider), CustomAuthenticationFilter.class); // Add this line

        return http.build();
        }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}
