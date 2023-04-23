package com.gmitit01.recommenderservice.config;

import com.gmitit01.recommenderservice.converter.CLARANSReadConverter;
import com.gmitit01.recommenderservice.converter.CLARANSWriteConverter;
import com.gmitit01.recommenderservice.converter.PCAPropReadConverter;
import com.gmitit01.recommenderservice.converter.PCAPropWriteConverter;
import com.gmitit01.recommenderservice.entity.utils.UuidIdentifiedEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;

import java.util.Arrays;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class MongoConfig {

    private final MongoDatabaseFactory mongoDatabaseFactory;

    @Bean
    public MappingMongoConverter mappingMongoConverter() {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory);
        MongoMappingContext mongoMappingContext = new MongoMappingContext(); // Create a new instance of MongoMappingContext
        MappingMongoConverter mongoConverter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        // Configure a replacement for dots in map keys
        mongoConverter.setMapKeyDotReplacement("___"); // DO NOT CHANGE THIS VALUE

        // Set the custom conversions to the MappingMongoConverter
        mongoConverter.setCustomConversions(customConversions());

        return mongoConverter;
    }


    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new CLARANSReadConverter(),
                new PCAPropReadConverter()
        ));
    }

    @Bean
    MongoTransactionManager transactionManager() {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }

    @Bean
    public BeforeConvertCallback<UuidIdentifiedEntity> beforeSaveCallback() {
        return (entity, collection) -> {
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
            }
            return entity;
        };
    }
}