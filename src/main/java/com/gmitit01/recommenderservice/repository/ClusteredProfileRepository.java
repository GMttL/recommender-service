package com.gmitit01.recommenderservice.repository;

import com.gmitit01.recommenderservice.entity.ClusteredProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ClusteredProfileRepository extends MongoRepository<ClusteredProfile, UUID> {
}
