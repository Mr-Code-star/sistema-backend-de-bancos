package com.example.sistemabackenddebancos.iam.infrastructure.security.filters;

import com.example.sistemabackenddebancos.iam.infrastructure.security.tokens.JwtTokenValidator;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenValidator validator;

    public JwtAuthenticationFilter(JwtTokenValidator validator) {
        this.validator = validator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring("Bearer ".length()).trim();

            validator.parseClaims(token).ifPresent(claims -> {
                setAuthFromClaims(claims);
            });
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthFromClaims(Claims claims) {
        // subject = userId que pusiste en JwtTokenService (subject(user.id))
        String userId = claims.getSubject();

        // opcional: usar status/email como authorities/claims
        // Por ahora: ROLE_USER fijo
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}