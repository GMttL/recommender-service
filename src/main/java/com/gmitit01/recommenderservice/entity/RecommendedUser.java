package com.gmitit01.recommenderservice.entity;


import com.gmitit01.recommenderservice.entity.DTO.OnboardingProfileDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Data
public class RecommendedUser {
    private final OnboardingProfileDTO onboardingProfile;
    private final double combatibilityScore;
}
