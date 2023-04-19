package com.gmitit01.recommenderservice.service;

import com.gmitit01.recommenderservice.entity.ClusteredProfile;

import java.util.List;
import java.util.UUID;

public interface ClusteredProfileService {

    // Save Profile
    ClusteredProfile createProfile(ClusteredProfile profile);

    // Read Profile
    ClusteredProfile readProfile(UUID uid);

    List<ClusteredProfile> readProfiles();

    // Delete Profile
    void deleteProfile(UUID uid);

    List<ClusteredProfile> getProfilesByCluster(int inputUserCluster);
}
