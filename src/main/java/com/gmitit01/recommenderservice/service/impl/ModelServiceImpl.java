package com.gmitit01.recommenderservice.service.impl;

import com.gmitit01.recommenderservice.entity.DTO.OnboardingProfileDTO;
import com.gmitit01.recommenderservice.entity.DTO.PagedResponseDTO;
import com.gmitit01.recommenderservice.service.ModelService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModelServiceImpl implements ModelService {

    private final WebClient webClient;

    public ModelServiceImpl(WebClient webClientBuilder) {
        this.webClient = webClientBuilder.mutate().baseUrl("http://localhost:3000").build();
    }

    @Override
    public List<OnboardingProfileDTO> fetchAllUserData() {
        List<OnboardingProfileDTO> allUserData = new ArrayList<>();
        int page = 0;
        int size = 20;
        PagedResponseDTO<OnboardingProfileDTO> pagedResponse;

        do {
            pagedResponse = fetchUserDataPage(page, size);
            allUserData.addAll(pagedResponse.getContent());
            page++;
        } while (pagedResponse.getTotalElements() == pagedResponse.getPageSize());

        return allUserData;
    }

    private PagedResponseDTO<OnboardingProfileDTO> fetchUserDataPage(int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/browsing/profiles")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PagedResponseDTO<OnboardingProfileDTO>>() {
                })
                .block();
    }
}
