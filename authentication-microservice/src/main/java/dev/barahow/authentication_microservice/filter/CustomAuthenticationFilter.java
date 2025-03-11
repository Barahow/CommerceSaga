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

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserAuthenticationService userAuthenticationService;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserAuthenticationService userAuthenticationService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userAuthenticationService = userAuthenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        // extract token form the request, usually from the
        //Authorization header
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // removes the "bearer"+space 7 letters
            String token = authorizationHeader.substring(7);

            // get user details based on the token
            // Verify the token
            logger.info("expected token "+ token);

                DecodedJWT decodedJWT = jwtTokenProvider.verifyToken(token.substring(7));

        

            if (decodedJWT != null) {
              String loggedInUSerEmail= decodedJWT.getSubject();
                //check if the user is locked
                UserDTO userDTO = userAuthenticationService.getUserByEmail(loggedInUSerEmail);

                if (userDTO != null && userDTO.isLocked().isLocked()) {
                    // if the user is locked we check
                    // the time left
                    LocalDateTime lockTime = userDTO.isLocked().getLockTime();
                    long minutesSinceLock = ChronoUnit.MINUTES.between(lockTime, LocalDateTime.now());

                    if (minutesSinceLock < 10) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("user account is locked, try again later");
                        return;
                    } else {
                        // rest lock if timeout has passed
                        // it should work
                        userAuthenticationService.resetUserLock(userDTO.getEmail());
                    }
                }
            }

            // authenticate the user using the authenticationManager
            // Get the email (or other identifier) from the token
            String loggedInUserEmail = userAuthenticationService.getLoggedInUser(token);

            // Create a CustomUserDetails object using the JWT inf
            assert decodedJWT != null;
           List<String> rolesString = decodedJWT.getClaim("roles").asList(String.class);
            Set<Role> roles =rolesString.stream()
                    .map(Role::valueOf)  // Assumes the string exactly matches the enum name
                    .collect(Collectors.toSet());

            CustomUserDetails userDetails = new CustomUserDetails(loggedInUserEmail,roles);

            // Create an authentication token without credentials
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // once authentication is sueccesful, store the authentication
            SecurityContextHolder.getContext().setAuthentication(authentication);


        }

        filterChain.doFilter(request, response);

    }
}
