package com.gmitit01.recommenderservice.entity.DTO;


import com.gmitit01.recommenderservice.entity.OnboardingPreferences;
import com.gmitit01.recommenderservice.entity.OnboardingProfile;
import com.gmitit01.recommenderservice.entity.OnboardingSelf;
import com.gmitit01.recommenderservice.entity.ProcessedProfile;
import lombok.Data;

@Data
public class OnboardingProfileDTO {

    private OnboardingProfile onboardingProfile;
    private OnboardingPreferences onboardingPreferences;
    private OnboardingSelf onboardingSelf;
}
