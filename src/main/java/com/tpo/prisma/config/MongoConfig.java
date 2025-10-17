package com.tpo.prisma.config;

import org.bson.BsonTimestamp;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
public class MongoConfig {

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
                    // ✅ Elimina el campo "_class"
                    converter.setTypeMapper(new DefaultMongoTypeMapper(null));
                }
                return bean;
            }
        };
    }

    // Conversor de BsonTimestamp a Date
    public static class BsonTimestampToDateConverter implements Converter<BsonTimestamp, Date> {
        @Override
        public Date convert(BsonTimestamp source) {
            return new Date(source.getTime() * 1000L);
        }
    }
}
