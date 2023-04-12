package com.gmitit01.recommenderservice.service;

import com.gmitit01.recommenderservice.entity.DTO.OnboardingProfileDTO;
import reactor.core.publisher.Flux;

public interface ModelService {

    public Flux<OnboardingProfileDTO> fetchAllUserData();
}
