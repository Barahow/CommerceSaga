package dev.barahow.authentication_microservice.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.barahow.authentication_microservice.dao.UserEntity;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
@Log4j2
public class JwtTokenProvider {


    private final String secretKey;

    public JwtTokenProvider( @Value("${MY_APP_SECRET_KEY}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String generateToken(UserEntity userEntity){
        log.info("secret key {}", secretKey);
        Algorithm algorithm= Algorithm.HMAC256(secretKey.getBytes());

        return JWT.create().withSubject(userEntity.getEmail())
                .withClaim("id",userEntity.getId().toString())
                .withClaim("roles",userEntity.getRole().stream().
                        map(Enum::name).collect(Collectors.toList()))
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
