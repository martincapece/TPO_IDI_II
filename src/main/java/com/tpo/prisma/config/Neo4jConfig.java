package com.tpo.prisma.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableTransactionManagement
@EnableNeo4jRepositories(basePackages = "com.tpo.prisma.repository.repositoryNeo4j")
public class Neo4jConfig {

    @Value("${spring.neo4j.uri}")
    private String uri;

    @Value("${spring.neo4j.authentication.username}")
    private String username;

    @Value("${spring.neo4j.authentication.password}")
    private String password;

    @Bean
    public Driver neo4jDriver() {
        Config config = Config.builder()
                .withMaxConnectionLifetime(1, TimeUnit.HOURS)
                .withMaxConnectionPoolSize(50)
                .withConnectionAcquisitionTimeout(60, TimeUnit.SECONDS)
                .withConnectionTimeout(30, TimeUnit.SECONDS)
                .build();

        return GraphDatabase.driver(uri, AuthTokens.basic(username, password), config);
    }

    @Bean(name = "neo4jTransactionManager")
    public PlatformTransactionManager neo4jTransactionManager(Driver driver,
                                                              DatabaseSelectionProvider databaseSelectionProvider) {
        return new Neo4jTransactionManager(driver, databaseSelectionProvider);
    }
}
