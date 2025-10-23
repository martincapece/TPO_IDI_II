package com.tpo.prisma.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManager;

@Configuration
public class JpaConfig {

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(EntityManager em) {
        return new JpaTransactionManager(em.getEntityManagerFactory());
    }
}