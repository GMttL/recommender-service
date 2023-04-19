package com.gmitit01.recommenderservice.entity.DTO;


import com.gmitit01.recommenderservice.entity.OnboardingPreferences;
import com.gmitit01.recommenderservice.entity.OnboardingProfile;
import com.gmitit01.recommenderservice.entity.OnboardingSelf;
import lombok.Data;

@Data
public class OnboardingProfileDTO {

    private OnboardingProfile onboardingProfile;
    private OnboardingPreferences onboardingPreferences;
    private OnboardingSelf onboardingSelf;
}
