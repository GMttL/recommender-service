package com.gmitit01.recommenderservice.service;

import com.gmitit01.recommenderservice.entity.DTO.OnboardingProfileDTO;

import java.util.List;

public interface ModelService {

    public List<OnboardingProfileDTO> fetchAllUserData();
}
