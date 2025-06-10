package com.project.Ipubly.Services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiration.time.millis}")
    private Long expirationTimeMillis;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC256(secretKey);
        verifier = JWT.require(algorithm).withIssuer(issuer).withAudience("Ipubly")
                .build();
    }

    public String generateToken(String userId, String username) {
        try {
            return JWT.create()
                    .withIssuer(issuer)
                    .withAudience("Ipubly")
                    .withSubject(userId).withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeMillis))
                    .sign(this.algorithm);
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }


        }

    public DecodedJWT verifyToken(String token) {
        try {
            return  this.verifier.verify(token);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }



    }

    public String getUserIdFromToken(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }
}