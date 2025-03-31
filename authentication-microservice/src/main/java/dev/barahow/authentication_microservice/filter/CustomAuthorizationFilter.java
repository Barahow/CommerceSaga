package dev.barahow.authentication_microservice.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import dev.barahow.authentication_microservice.Service.UserAuthenticationService;
import dev.barahow.authentication_microservice.Service.UserService;
import dev.barahow.authentication_microservice.component.JwtTokenProvider;
import dev.barahow.authentication_microservice.security.CustomUserDetails;
import dev.barahow.core.dto.UserDTO;
import dev.barahow.core.types.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j

public class CustomAuthorizationFilter extends OncePerRequestFilter {



    private final JwtTokenProvider jwtTokenProvider;

    public CustomAuthorizationFilter( JwtTokenProvider jwtTokenProvider) {

        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info(requestURI);


        if ("/api/v1/login".equals(requestURI) || "/api/v1/registration".equals(requestURI) || "/api/v1/token/refresh".equals(requestURI)) {
            filterChain.doFilter(request, response);

            return;


        }



        try {
            String token = extractToken(request);
            if (token == null) {
                sendError(response, "Missing authorization token");
                return;
            }

            log.info("token "+token);
            DecodedJWT decodedJWT = jwtTokenProvider.verifyToken(token);


            authenticateRequest(decodedJWT);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            sendError(response, "Authorization failed: " + e.getMessage());
        }


    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) return null;
        return header.substring(7);
    }

    private void authenticateRequest(DecodedJWT decodedJWT) {
        String email = decodedJWT.getSubject();


        List<SimpleGrantedAuthority> authorities = decodedJWT.getClaim("roles")
                .asList(String.class)
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        Authentication auth = new UsernamePasswordAuthenticationToken(
               email,
                null,
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}


