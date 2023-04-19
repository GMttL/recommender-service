package com.gmitit01.recommenderservice.service;


import com.gmitit01.recommenderservice.entity.DTO.OnboardingProfileDTO;
import com.gmitit01.recommenderservice.entity.RecommendedUser;

import java.util.List;

public interface RecommenderService {

    List<RecommendedUser> recommendUsers(OnboardingProfileDTO inputUser);
}
