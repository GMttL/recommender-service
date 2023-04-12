package com.gmitit01.recommenderservice.controller;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@RestController
@RequestMapping("/api/recommendation/")
@RequiredArgsConstructor
public class RecommendationController {

    private final WebClient webClient;

    // Accept request from Matches Service
    @GetMapping("/matches/{uid}")
    public ResponseEntity<String> getMatches(@PathVariable UUID uid) {

        // call onboarding service and get user data
        String json = webClient.get()
                .uri("http://localhost:3000/api/browsing/profiles")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        // TODO: this
        // perform logic in the service layer that gets us the recommendations
        // with a compatibility score

        // the compatibility score is calculated on the current user against other users.
        // this is somewhat vague because I don't really know how the recommender system
        // will work at telling me who is close to who

        // for now, we'll just return a random number
        double compatibility = 0.7531;
        jsonObject.addProperty("compatibility", compatibility);

        String newJSON = jsonObject.toString();

        return new ResponseEntity<>(newJSON, HttpStatus.OK);
    }



}
