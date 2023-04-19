package com.gmitit01.recommenderservice.service.impl;

import com.gmitit01.recommenderservice.entity.ClusteredProfile;
import com.gmitit01.recommenderservice.entity.DTO.OnboardingProfileDTO;
import com.gmitit01.recommenderservice.entity.ProcessedProfile;
import com.gmitit01.recommenderservice.entity.RecommendedUser;
import com.gmitit01.recommenderservice.entity.TrainedModel;
import com.gmitit01.recommenderservice.logic.Model;
import com.gmitit01.recommenderservice.service.ClusteredProfileService;
import com.gmitit01.recommenderservice.service.PreProcessingMetaService;
import com.gmitit01.recommenderservice.service.TrainedModelService;
import com.gmitit01.recommenderservice.utils.CosineDistance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecommenderServiceImpl {

    private final Model model;
    private final CosineDistance cosineDistance;
    private final ClusteredProfileService clusteredProfileService;
    private final PreProcessingMetaService preProcessingMetaService;
    private final TrainedModelService trainedModelService;

    public List<RecommendedUser> recommendUsers(OnboardingProfileDTO inputUser) {
        // Preprocess the input user's data
        ProcessedProfile processedInputUser = model.preprocessData(List.of(inputUser)).get(0);

        // Fetch the latest trained model
        TrainedModel trainedModel = trainedModelService.getLatestModel();

        // Project the input user's data using PCA from the trained model
        double[] inputUserReduced = trainedModel.getPca().getProjection(model.getPCA_COMPONENTS()).apply(processedInputUser.toDoubleArray());

        // Predict the cluster for the input user
        int inputUserCluster = trainedModel.getClarans().predict(inputUserReduced);

        // Fetch all the users belonging to the input user's cluster
        List<ClusteredProfile> clusteredProfiles = clusteredProfileService.getProfilesByCluster(inputUserCluster);

        // Calculate compatibility scores for each user in the cluster
        List<RecommendedUser> recommendedUsers = clusteredProfiles.stream().map(clusteredProfile -> {
            ProcessedProfile processedProfile = model.preprocessData(List.of(clusteredProfile.getOnboardingProfile())).get(0);
            double[] profileReduced = trainedModel.getPca().getProjection(model.getPCA_COMPONENTS()).apply(processedProfile.toDoubleArray());
            double compatibilityScore = cosineDistance.d(inputUserReduced, profileReduced);

            return new RecommendedUser(clusteredProfile.getOnboardingProfile(), compatibilityScore);
        }).collect(Collectors.toList());

        // Sort the recommended users by their compatibility scores (descending)
        recommendedUsers.sort(Comparator.comparingDouble(RecommendedUser::getCombatibilityScore).reversed());

        return recommendedUsers;
    }
}
