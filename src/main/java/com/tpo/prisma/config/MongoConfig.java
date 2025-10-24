package com.tpo.prisma.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.BsonTimestamp;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableMongoRepositories(basePackages = "com.tpo.prisma.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        return "TPO_MongoDB";
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        
        // Configuración de Sharding Explícito: N=3, R=1, W=2
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                // N = 3 (réplicas en cluster Atlas)
                // R = 1 (leer del nodo más cercano para baja latencia)
                .readPreference(ReadPreference.nearest())
                // W = 2 (escribir en 2 nodos antes de confirmar - alta consistencia)
                .writeConcern(WriteConcern.W2
                        .withJournal(true)  // Persiste en journal para durabilidad
                        .withWTimeout(5000, TimeUnit.MILLISECONDS))  // Timeout de 5s
                .build();
        
        return MongoClients.create(settings);
    }

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new BsonTimestampToDateConverter());
        return new MongoCustomConversions(converters);
    }

    @Bean
    public BeanPostProcessor mappingMongoConverterConfigurer() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof MappingMongoConverter) {
                    MappingMongoConverter converter = (MappingMongoConverter) bean;
                    converter.setTypeMapper(new DefaultMongoTypeMapper(null));
                }
                return bean;
            }
        };
    }

    public static class BsonTimestampToDateConverter implements Converter<BsonTimestamp, Date> {
        @Override
        public Date convert(BsonTimestamp source) {
            return new Date(source.getTime() * 1000L);
        }
    }
}
