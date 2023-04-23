package com.gmitit01.recommenderservice.service.impl;

import com.gmitit01.recommenderservice.entity.*;
import com.gmitit01.recommenderservice.entity.DTO.OnboardingProfileDTO;
import com.gmitit01.recommenderservice.logic.Model;
import com.gmitit01.recommenderservice.service.ClusteredProfileService;
import com.gmitit01.recommenderservice.service.RecommenderService;
import com.gmitit01.recommenderservice.service.TrainedModelService;
import com.gmitit01.recommenderservice.utils.CosineDistance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smile.feature.extraction.PCA;
import smile.math.matrix.Matrix;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecommenderServiceImpl implements RecommenderService {

    private final Model model;
    private final CosineDistance cosineDistance;
    private final ClusteredProfileService clusteredProfileService;
    private final TrainedModelService trainedModelService;


    /***
     *  Takes in a profile in the form of OnboardingProfileDTO
     *  and returns a list of compatible users in the form of RecommendedUser
     *  SORTED by their compatibility score (descending).
     *
     * @param inputUser -- The user for whom recommendations are to be made
     *
     * @return
     */
    public List<RecommendedUser> recommendUsers(OnboardingProfileDTO inputUser) {
        // Preprocess the input user's data
        ProcessedProfile processedInputUser = model.preProcessUserData(inputUser);

        // Fetch the latest trained model
        TrainedModel trainedModel = trainedModelService.getLatestModel();

        // Project the input user's data using PCA from the trained model
        PCAProperties pcaProperties = trainedModel.getPca();
        // Create a Matrix object for projection, which is the identity matrix since we're not applying any transformations
        Matrix projectionMatrix = Matrix.eye(pcaProperties.getLoadings().getMatrix().nrow(), pcaProperties.getLoadings().getMatrix().ncol());

        PCA pca = new PCA(pcaProperties.getMean(), pcaProperties.getEigvalues(), pcaProperties.getLoadings().getMatrix(), projectionMatrix);

        double[] inputUserReduced = pca.getProjection(model.getPCA_COMPONENTS()).apply(processedInputUser.toDoubleArray());
        Double[] inputUserReducedDouble = Arrays.stream(inputUserReduced).
                boxed().
                toArray(Double[]::new);

        // Predict the cluster for the input user
        int inputUserCluster = trainedModel.getClarans().predict(inputUserReducedDouble);

        // Fetch all the users belonging to the input user's cluster
        List<ClusteredProfile> clusteredProfiles = clusteredProfileService.getProfilesByCluster(inputUserCluster);

        // Calculate compatibility scores for each user in the cluster
        return clusteredProfiles.stream().map(clusteredProfile -> {
            ProcessedProfile processedProfile = clusteredProfile.getProcessedProfile();
            double[] profileReduced = pca.getProjection(model.getPCA_COMPONENTS()).apply(processedProfile.toDoubleArray());
            Double[] profileReducedDouble = Arrays.stream(profileReduced).
                    boxed().
                    toArray(Double[]::new);

            double compatibilityScore = cosineDistance.cosineSimilarity(inputUserReducedDouble, profileReducedDouble);

            return new RecommendedUser(clusteredProfile.getOnboardingProfile(), compatibilityScore);
        })
                .sorted(Comparator.comparingDouble(RecommendedUser::getCombatibilityScore).reversed())
                .toList();
    }
}
