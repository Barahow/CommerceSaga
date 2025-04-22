package dev.barahow.order_microservice.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoderInitializationException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JwtUtils {

    //extract customerId from jwt
    public UUID extractCustomerIdFromJWT(){
        JwtAuthenticationToken authenticationToken=(JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authenticationToken!= null){
            String token = authenticationToken.getToken().getTokenValue();
            DecodedJWT decodedJWT= JWT.decode(token);
            String customerIdString= decodedJWT.getClaim("id").asString();

            return UUID.fromString(customerIdString);// Return the customerId as UUID

        }


        return null;
    }
}
