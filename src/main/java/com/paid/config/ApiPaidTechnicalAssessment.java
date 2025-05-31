package com.paid.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaRepositories(basePackages = "com.paid.repository")
@EntityScan(basePackages = "com.paid.entity")
@SpringBootApplication(scanBasePackages = "com.paid")
@EnableScheduling
public class ApiPaidTechnicalAssessment {
    public static void main(String[] args) {
        SpringApplication.run(ApiPaidTechnicalAssessment.class, args);
    }
}
