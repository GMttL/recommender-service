package com.gmitit01.recommenderservice.repository;

import com.gmitit01.recommenderservice.entity.PreProcessingMeta;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface PreProcessingMetaRepository extends MongoRepository<PreProcessingMeta, UUID> {

}
