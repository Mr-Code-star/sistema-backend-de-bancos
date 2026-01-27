package com.example.sistemabackenddebancos.iam.interfaces.security;

import com.example.sistemabackenddebancos.iam.infrastructure.security.filters.JwtAuthenticationFilter;
import com.example.sistemabackenddebancos.iam.infrastructure.security.tokens.JwtTokenValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {


    @Bean
    SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtTokenValidator validator,
            @Value("${security.admin.user-ids:}") String adminIdsCsv
    ) throws Exception {

        var jwtFilter = new JwtAuthenticationFilter(validator, adminIdsCsv);

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/health",
                                "/api/v1/iam/auth/**",
                                "/api/v1/iam/auth/mfa/**",
                                "/api/v1/merchant/**"
                        ).permitAll()

                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        .requestMatchers(
                                "/api/v1/profiles/**",
                                "/api/v1/accounts/**",
                                "/api/v1/transfers/**",
                                "/api/v1/ledger/**",
                                "/api/v1/notifications/**",
                                "/api/v1/payments/**",
                                "/api/v1/statements/**"
                        ).authenticated()

                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}