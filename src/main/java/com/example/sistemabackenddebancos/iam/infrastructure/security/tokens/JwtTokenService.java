package com.example.sistemabackenddebancos.iam.infrastructure.security.tokens;

import com.example.sistemabackenddebancos.iam.application.security.tokens.TokenService;
import com.example.sistemabackenddebancos.iam.domain.model.aggregates.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenService implements TokenService {

    private final SecretKey key;
    private final long expirationMillis;

    public JwtTokenService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-minutes:60}") long expirationMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMinutes * 60_000L;
    }

    @Override
    public String generate(User user) {
        var now = new Date();
        var exp = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(user.id().value().toString())
                .claim("email", user.email().value())
                .claim("status", user.status().name())
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }
}