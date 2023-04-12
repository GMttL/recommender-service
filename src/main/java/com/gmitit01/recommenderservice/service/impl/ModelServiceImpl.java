package com.gmitit01.recommenderservice.service.impl;

import com.gmitit01.recommenderservice.entity.DTO.OnboardingProfileDTO;
import com.gmitit01.recommenderservice.entity.DTO.PagedResponseDTO;
import com.gmitit01.recommenderservice.service.ModelService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ModelServiceImpl implements ModelService {

    private final WebClient webClient;

    public ModelServiceImpl(WebClient webClientBuilder) {
        this.webClient = webClientBuilder.mutate().baseUrl("http://localhost:3000").build();
    }

    @Override
    public Flux<OnboardingProfileDTO> fetchAllUserData() {
        return fetchUserDataPage(0, 20)
                .expand(response -> {
                    if (response.getTotalElements() == response.getPageSize()) {
                        return fetchUserDataPage(response.getPageNumber() + 1, response.getPageSize());
                    }
                    return Mono.empty();
                })
                .flatMapIterable(PagedResponseDTO::getContent);
    }


    private Mono<PagedResponseDTO<OnboardingProfileDTO>> fetchUserDataPage(int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/browsing/profiles")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

}
