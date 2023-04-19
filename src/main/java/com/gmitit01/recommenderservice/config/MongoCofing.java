package com.gmitit01.recommenderservice.config;


import com.gmitit01.recommenderservice.entity.UuidIdentifiedEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;

import java.util.UUID;

@Configuration
public class MongoCofing {

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
