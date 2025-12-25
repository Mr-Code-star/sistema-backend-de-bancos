package com.example.sistemabackenddebancos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.sistemabackenddebancos")
public class SistemaBackendDeBancosApplication {
    public static void main(String[] args) {
        SpringApplication.run(SistemaBackendDeBancosApplication.class, args);
    }
}