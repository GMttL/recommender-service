package com.gmitit01.recommenderservice.serviceLayer;

import com.gmitit01.recommenderservice.entity.ClusteredProfile;
import com.gmitit01.recommenderservice.entity.DTO.OnboardingProfileDTO;
import com.gmitit01.recommenderservice.entity.OnboardingPreferences;
import com.gmitit01.recommenderservice.repository.ClusteredProfileRepository;
import com.gmitit01.recommenderservice.service.impl.ClusteredProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ClusteredProfileServiceTest {

    @InjectMocks
    private ClusteredProfileServiceImpl clusteredProfileService;

    @Mock
    private ClusteredProfileRepository clusteredProfileRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProfile() {
        ClusteredProfile profile = new ClusteredProfile();
        when(clusteredProfileRepository.save(profile)).thenReturn(profile);

        ClusteredProfile createdProfile = clusteredProfileService.createProfile(profile);

        assertEquals(profile, createdProfile);
        verify(clusteredProfileRepository, times(1)).save(profile);
    }

    @Test
    void readProfile() {
        UUID id = UUID.randomUUID();
        ClusteredProfile profile = new ClusteredProfile();
        when(clusteredProfileRepository.findById(id)).thenReturn(Optional.of(profile));

        ClusteredProfile foundProfile = clusteredProfileService.readProfile(id);

        assertEquals(profile, foundProfile);
        verify(clusteredProfileRepository, times(1)).findById(id);
    }

    @Test
    void readProfiles() {
        ClusteredProfile profile1 = new ClusteredProfile();
        ClusteredProfile profile2 = new ClusteredProfile();
        List<ClusteredProfile> profiles = Arrays.asList(profile1, profile2);
        when(clusteredProfileRepository.findAll()).thenReturn(profiles);

        List<ClusteredProfile> foundProfiles = clusteredProfileService.readProfiles();

        assertEquals(profiles, foundProfiles);
        verify(clusteredProfileRepository, times(1)).findAll();
    }


    @Test
    void deleteProfile() {
        UUID id = UUID.randomUUID();
        doNothing().when(clusteredProfileRepository).deleteById(id);

        clusteredProfileService.deleteProfile(id);

        verify(clusteredProfileRepository, times(1)).deleteById(id);
    }
}
