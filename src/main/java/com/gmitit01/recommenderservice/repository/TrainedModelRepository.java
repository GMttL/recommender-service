package com.gmitit01.recommenderservice.repository;

import com.gmitit01.recommenderservice.entity.TrainedModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface TrainedModelRepository extends MongoRepository<TrainedModel, UUID> {
}
