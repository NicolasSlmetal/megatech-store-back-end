package com.megatech.store.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.megatech.store.domain.Role;
import com.megatech.store.domain.User;
import com.megatech.store.exceptions.TokenErrorException;
import com.megatech.store.model.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    @Value("api.key")
    private String apiKey;

    public String generateToken(UserModel user) throws TokenErrorException {
        try {
            return JWT.create()
                    .withIssuer(user.getEmail())
                    .withClaim("id", user.getId())
                    .withClaim("role", user.getRole().name())
                    .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                    .sign(Algorithm.HMAC512(apiKey));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new TokenErrorException("Failed to generate token");

        }
    }

    public User verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(apiKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            User user = new User();
            user.setEmail(decodedJWT.getIssuer());
            user.setId(decodedJWT.getClaim("id").asLong());
            user.setRole(Role.valueOf(decodedJWT.getClaim("role").asString()));
            return user;
        } catch (Exception e) {
            throw new TokenErrorException("Token has expired or is invalid");
        }
    }
}
