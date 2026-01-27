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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenValidator validator;

    private final Set<String> adminUserIds;

    public JwtAuthenticationFilter(JwtTokenValidator validator, String adminIdsCsv) {
        this.validator = validator;
        this.adminUserIds = Arrays.stream(adminIdsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
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
        String userId = claims.getSubject();

        System.out.println("==== JWT DEBUG ====");
        System.out.println("SUB=" + userId);
        System.out.println("ADMINS=" + adminUserIds);
        System.out.println("IS_ADMIN=" + adminUserIds.contains(userId));
        System.out.println("===================");

        var authorities = adminUserIds.contains(userId)
                ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                : List.of(new SimpleGrantedAuthority("ROLE_USER"));

        var authentication =
                new UsernamePasswordAuthenticationToken(userId, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}