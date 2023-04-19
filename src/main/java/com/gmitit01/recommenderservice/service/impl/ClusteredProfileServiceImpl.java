package com.gmitit01.recommenderservice.service.impl;

import com.gmitit01.recommenderservice.entity.ClusteredProfile;
import com.gmitit01.recommenderservice.repository.ClusteredProfileRepository;
import com.gmitit01.recommenderservice.service.ClusteredProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClusteredProfileServiceImpl implements ClusteredProfileService {

    private final ClusteredProfileRepository repository;

    @Override
    public ClusteredProfile createProfile(ClusteredProfile profile) {
        return repository.save(profile);
    }

    @Override
    public ClusteredProfile readProfile(UUID uid) {
        return repository.findById(uid).orElse(null);
    }

    @Override
    public List<ClusteredProfile> readProfiles() {
        return repository.findAll();
    }


    @Override
    public void deleteProfile(UUID uid) {
        repository.deleteById(uid);
    }

    @Override
    public List<ClusteredProfile> getProfilesByCluster(int inputUserCluster) {
        return repository.findByCluster(inputUserCluster);
    }
}
