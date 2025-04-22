package dev.barahow.authentication_microservice.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.barahow.authentication_microservice.dao.UserEntity;
import dev.barahow.authentication_microservice.security.CustomUserDetails;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Log4j2
public class JwtTokenProvider {


    private final String secretKey;

    public JwtTokenProvider( @Value("${MY_APP_SECRET_KEY}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String generateToken(UserDetails userDetails){
        log.info("secret key {}", secretKey);
        Algorithm algorithm= Algorithm.HMAC256(secretKey.getBytes());
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        return JWT.create().withSubject(customUserDetails.getUsername())
                .withClaim("id",customUserDetails.getId().toString())
                .withClaim("roles",customUserDetails.getAuthorities().stream().
                        map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() +1000*60*60 ))// 1 hour expiration date

                .sign(algorithm);
    }


    public DecodedJWT verifyToken(String token){
        Algorithm algorithm=Algorithm.HMAC256(secretKey.getBytes());

        JWTVerifier jwtVerifier= JWT.require(algorithm).build();


        return jwtVerifier.verify(token);
    }


}
