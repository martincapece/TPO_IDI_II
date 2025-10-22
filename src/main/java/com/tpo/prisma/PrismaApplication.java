package com.tpo.prisma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class PrismaApplication implements CommandLineRunner {

    @Autowired(required = false)
    private MongoTemplate mongoTemplate;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private Driver neo4jDriver;

    @Autowired(required = false)
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(PrismaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        validateMongoDB();

        validateNeo4j();

        validateRedis();

        validatePostgreSQL();

    }

    private void validateMongoDB() {
        System.out.print("MongoDB: ");
        try {
            if (mongoTemplate != null) {
                mongoTemplate.getCollectionNames();
                System.out.println("✅ CONECTADO");
            } else {
                System.out.println("❌ NO CONFIGURADO");
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR - " + e.getMessage());
        }
    }

    private void validateNeo4j() {
        System.out.print("Neo4j: ");
        try {
            if (neo4jDriver != null) {
                try (Session session = neo4jDriver.session()) {
                    Result result = session.run("RETURN 1");
                    if (result.hasNext()) {
                        System.out.println("✅ CONECTADO");
                    } else {
                        System.out.println("❌ ERROR - Sin respuesta");
                    }
                }
            } else {
                System.out.println("❌ NO CONFIGURADO");
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR - " + e.getMessage());
        }
    }

    private void validateRedis() {
		System.out.print("Redis: ");
		try {
			if (redisTemplate != null) {
				redisTemplate.setEnableDefaultSerializer(false);
				String pong = redisTemplate.getConnectionFactory().getConnection().ping();
				if ("PONG".equals(pong)) {
					System.out.println("✅ CONECTADO");
				} else {
					System.out.println("❌ ERROR - Respuesta inesperada: " + pong);
				}
			} else {
				System.out.println("❌ NO CONFIGURADO");
			}
		} catch (Exception e) {
			System.out.println("❌ ERROR - Unable to connect to Redis: " + e.getClass().getName() + " - " + e.getMessage());
			e.printStackTrace();
			if (e.getCause() instanceof io.netty.handler.ssl.NotSslRecordException) {
				System.out.println("⚠️ Posible problema con SSL/TLS. Verifica el puerto y la configuración.");
			}
		}
	}
	
    private void validatePostgreSQL() {
        System.out.print("PostgreSQL: ");
        try {
            if (dataSource != null) {
                try (Connection connection = dataSource.getConnection()) {
                    if (connection.isValid(5)) {
                        System.out.println("✅ CONECTADO");
                    } else {
                        System.out.println("❌ ERROR - Conexión no válida");
                    }
                }
            } else {
                System.out.println("❌ NO CONFIGURADO");
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR - " + e.getMessage());
        }
    }
}