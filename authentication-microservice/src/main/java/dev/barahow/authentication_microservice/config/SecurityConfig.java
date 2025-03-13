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

    private  final UserAuthenticationService userAuthenticationService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final CustomPermissionEvaluator customPermissionEvaluator;

    public SecurityConfig(UserDetailsService userDetailsService, UserAuthenticationService userAuthenticationService, AuthenticationConfiguration authenticationConfiguration, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder, CustomPermissionEvaluator customPermissionEvaluator) {
        this.userDetailsService = userDetailsService;
        this.userAuthenticationService = userAuthenticationService;
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.customPermissionEvaluator = customPermissionEvaluator;
    }

    // configure method security expressions
    // this should make sure we get our custom permission
    // because registered as a bean
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(){
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
    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        return new CustomAuthenticationFilter(authenticationManager(), jwtTokenProvider,userAuthenticationService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf-> csrf.disable())
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth-> auth
                        // Public endpoints
                        .requestMatchers("/api/v1/login/**", "/api/token/refresh", "/api/v1/registration").permitAll()

                        // Admin-only endpoints
                        .requestMatchers(HttpMethod.GET, "/api/v1/user").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/user/**").hasAuthority("ADMIN")

                        // Public registration endpoint
                        .requestMatchers(HttpMethod.POST, "/api/v1/user/register").permitAll()


                        // everything else need to be authenticated for now
                        .anyRequest() .authenticated())


                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                // Handle authentication failures (401 Unauthorized)
                                .authenticationEntryPoint((request, response, authException) -> {
                                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                    response.getWriter().write("{\"error\": \"Authentication required\"}");
                                })
                                // Handle authorization failures (403 Forbidden)
                                .accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                //add custom authentication provider
                .authenticationProvider(daoAuthenticationProvider())

                // register the custom authentication filter
        // it should executre before usernmaePasswordAuthenticationFIlter
               //custom filters

       // Register your custom authorization filter as needed.
                .addFilterBefore(customAuthenticationFilter(),UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new CustomAuthorizationFilter(userAuthenticationService), BasicAuthenticationFilter.class);



        return http.build();

    }


    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}
