package com.gmitit01.recommenderservice.entity;

import com.gmitit01.recommenderservice.entity.DTO.OnboardingProfileDTO;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document
public class ClusteredProfile extends UuidIdentifiedEntity {

    private OnboardingProfileDTO onboardingProfile;
    private ProcessedProfile processedProfile;
    private int cluster;
}
