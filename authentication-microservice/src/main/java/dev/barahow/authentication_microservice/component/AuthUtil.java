package dev.barahow.authentication_microservice.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.barahow.authentication_microservice.Service.CustomUserDetailsService;
import dev.barahow.authentication_microservice.security.CustomUserDetails;
import dev.barahow.core.exceptions.AuthenticationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Component
public class AuthUtil {

    private  final CustomUserDetailsService customUserDetailsService;

    public AuthUtil(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    //extract customerId from jwt
    public UUID extractCustomerIdFromJWT(){
       Authentication auth= SecurityContextHolder.getContext().getAuthentication();

        log.info("Authentication type: {}", auth.getClass().getSimpleName());
        // Check if it's a JwtAuthenticationToken (post-login)
        if (auth instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            String customerIdString = jwtAuthenticationToken
                    .getToken()
                    .getClaim("id"); // Extract 'id' claim from JWT token

            log.info("Extracted customerId: {}", customerIdString);
            return UUID.fromString(customerIdString);
        }

        // Check if it's a UsernamePasswordAuthenticationToken (during authentication)
        if (auth instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
String email =(String) usernamePasswordAuthenticationToken.getPrincipal();
log.info("principal email: {}", email);

            //load CustomUserDetails based on email
            // to extract its id
            CustomUserDetails cu = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);
            log.info("customerId from CustomUserDetailsService {}",cu.getId());
            return cu.getId();
        }

        throw new AuthenticationException("Authentication or customer ID is missing");
    }

}
