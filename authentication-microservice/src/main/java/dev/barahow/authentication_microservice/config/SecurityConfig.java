package dev.barahow.authentication_microservice.config;

import dev.barahow.authentication_microservice.Service.UserAuthenticationService;
import dev.barahow.authentication_microservice.filter.CustomAuthenticationFilter;
import dev.barahow.authentication_microservice.filter.CustomAuthorizationFilter;
import dev.barahow.core.types.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity

public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    private  final UserAuthenticationService userAuthenticationService;
    private final AuthenticationConfiguration authenticationConfiguration;
private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserDetailsService userDetailsService, UserAuthenticationService userAuthenticationService, AuthenticationConfiguration authenticationConfiguration, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.userAuthenticationService = userAuthenticationService;
        this.authenticationConfiguration = authenticationConfiguration;
        this.passwordEncoder = passwordEncoder;
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
        return new CustomAuthenticationFilter(authenticationManager(), userAuthenticationService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf-> csrf.disable())
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth-> auth
                                .requestMatchers("/api/v1/login/**","/api/token/refresh").permitAll()
                                      // allow users to view,update or delete their account our custom logic

                                .requestMatchers(HttpMethod.GET,"/api/v1/user").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.POST,"/api/v1/user/register").permitAll()
                                .requestMatchers(HttpMethod.DELETE,"/api/v1/user/**").hasAuthority("ADMIN")

                                .anyRequest() .authenticated()

                        )
                .authenticationProvider(daoAuthenticationProvider())
                // register the custom authentication filter
        // it should executre before usernmaePasswordAuthenticationFIlter
                .addFilterBefore(customAuthenticationFilter(),UsernamePasswordAuthenticationFilter.class)
       // Register your custom authorization filter as needed.

                .addFilterBefore(new CustomAuthorizationFilter(userAuthenticationService), UsernamePasswordAuthenticationFilter.class);



        return http.build();

    }


    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


}
