package com.example.sistemabackenddebancos.iam.interfaces.security;

import com.example.sistemabackenddebancos.iam.infrastructure.security.filters.JwtAuthenticationFilter;
import com.example.sistemabackenddebancos.iam.infrastructure.security.tokens.JwtTokenValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtTokenValidator validator) throws Exception {

        var jwtFilter = new JwtAuthenticationFilter(validator);

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Público
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/health",
                                "/api/v1/iam/auth/**",
                                "/api/v1/iam/auth/mfa/**"
                        ).permitAll()

                        // Protegido con JWT
                        .requestMatchers("/api/v1/profiles/**").authenticated()
                        .requestMatchers("/api/v1/accounts/**").authenticated()

                        // (opcional) el resto por ahora permitido
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}