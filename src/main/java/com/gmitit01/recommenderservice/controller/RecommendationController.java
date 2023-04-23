package com.gmitit01.recommenderservice.controller;


import com.gmitit01.recommenderservice.entity.DTO.OnboardingProfileDTO;
import com.gmitit01.recommenderservice.entity.RecommendedUser;
import com.gmitit01.recommenderservice.service.RecommenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recommendation/")
@RequiredArgsConstructor
public class RecommendationController {

    private final WebClient webClient;
    private final RecommenderService recommenderService;

    // Accept request from Matches Service
    @GetMapping("/matches/{uid}")
    public ResponseEntity<List<RecommendedUser>> getMatches(@PathVariable UUID uid) {
        // TODO: The path needs to be changed once EUREKA is in place
        // Get User's Data from Onboarding Service
        OnboardingProfileDTO user = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/browsing/profile/{uid}").build(uid))
                .retrieve()
                .bodyToMono(OnboardingProfileDTO.class)
                .block();

        // Get User's Matches from Recommender Service
        List<RecommendedUser> matches = recommenderService.recommendUsers(user);

        return new ResponseEntity<>(matches, HttpStatus.OK);
    }
}