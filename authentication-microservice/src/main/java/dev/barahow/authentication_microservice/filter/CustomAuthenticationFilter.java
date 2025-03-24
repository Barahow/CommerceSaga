package dev.barahow.authentication_microservice.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import dev.barahow.authentication_microservice.Service.UserAuthenticationService;
import dev.barahow.authentication_microservice.component.JwtTokenProvider;
import dev.barahow.authentication_microservice.security.CustomUserDetails;
import dev.barahow.core.dto.UserDTO;
import dev.barahow.core.types.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private static final long LOCK_TIMEOUT_MINUTES = 5;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserAuthenticationService userAuthenticationService;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserAuthenticationService userAuthenticationService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userAuthenticationService = userAuthenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip filter for public endpoints
        if (isPublicEndpoint(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Process Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authorizationHeader.substring(7);


            DecodedJWT decodedJWT = jwtTokenProvider.verifyToken(token);
            if (decodedJWT == null) {
                sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }

            String email = decodedJWT.getSubject();
            UserDTO userDTO = userAuthenticationService.getUserByEmail(email);
            if (userDTO == null) {
                sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }

            // Check account lock status
            if (isAccountLocked(userDTO)) {
                sendJsonError(response, HttpServletResponse.SC_FORBIDDEN, "Account locked");
                return;
            }

            // Setup Spring Security authentication
            setupSpringAuthentication(decodedJWT, email);
            filterChain.doFilter(request, response);

        } catch (Exception e) {

            sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
        }
    }

    private boolean isPublicEndpoint(String path) {
        return path.equals("/api/v1/login") || path.equals("/api/v1/registration");
    }

    private boolean isAccountLocked(UserDTO userDTO) {
        if (userDTO.isLocked() == null || !userDTO.isLocked().isLocked()) {
            return false;
        }

        long minutesLocked = ChronoUnit.MINUTES.between(
                userDTO.isLocked().getLockTime(),
                LocalDateTime.now()
        );

        if (minutesLocked < LOCK_TIMEOUT_MINUTES) {
            return true;
        } else {
            userAuthenticationService.resetUserLock(userDTO.getEmail());
            return false;
        }
    }

    private void setupSpringAuthentication(DecodedJWT decodedJWT, String email) {
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        Set<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void sendJsonError(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        response.getWriter().write(String.format("{\"error\":\"%s\"}", message));
    }
}