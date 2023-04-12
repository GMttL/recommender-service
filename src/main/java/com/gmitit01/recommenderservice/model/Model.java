package com.gmitit01.recommenderservice.model;


import com.gmitit01.recommenderservice.entity.*;
import com.gmitit01.recommenderservice.entity.DTO.OnboardingProfileDTO;
import com.gmitit01.recommenderservice.service.impl.ModelServiceImpl;
import com.gmitit01.recommenderservice.utils.CosineDistance;
import com.gmitit01.recommenderservice.utils.StandardScaler;
import com.gmitit01.recommenderservice.utils.TfIdfVectorizer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Mono;

import smile.clustering.CLARANS;
import smile.feature.extraction.PCA;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class Model {

    // Constants
    private final Integer K_CLUSTERS = 3;
    private final Integer MAX_NEIGHBOURS = 30;
    private final Integer PCA_COMPONENTS = 4;

    // Injections
    private final CosineDistance cosineDistance;
    private final ModelServiceImpl modelService;

    @Scheduled(cron = "0 0 0 * * *") // This cron expression represents a daily task at midnight
    public void retrainModel() {
        loadAndPreprocessData()
                .flatMap(this::trainModel)
                .flatMap(this::saveModel)
                .subscribe();
    }

    private Mono<List<ProcessedProfile>> loadAndPreprocessData() {
        return modelService.fetchAllUserData()
                .collectList()
                .map(this::preprocessData);
    }


    private List<ProcessedProfile> preprocessData(List<OnboardingProfileDTO> userSet) {

        // 1. Remove duplicates
        List<OnboardingProfileDTO> distinctUsers = userSet.stream()
                .distinct()
                .toList();


        // 3.1 Hot-One Encoding
        Set<String> uniqueGenders = distinctUsers.stream()
                .map(user -> user.getOnboardingProfile().getGender())
                .collect(Collectors.toSet());

        Set<String> uniqueOccupations = distinctUsers.stream()
                .map(user -> user.getOnboardingProfile().getOccupation())
                .collect(Collectors.toSet());


        // 4. Feature scaling: standardize age, budget, minTerm, and maxTerm using z-score
        double[] ageArray = distinctUsers.stream().mapToDouble(user -> {
                    OnboardingProfile userProfile = user.getOnboardingProfile();
                    return userProfile.getAge();
                }).toArray();

        double[] budgetArray = distinctUsers.stream().mapToDouble(user -> {
                    OnboardingSelf userSelf = user.getOnboardingSelf();
                    return userSelf.getBudget();
                }).toArray();

        double[] minTermArray = distinctUsers.stream().mapToDouble(user -> {
                    OnboardingSelf userSelf = user.getOnboardingSelf();
                    return userSelf.getMinTerm();
                }).toArray();

        double[] maxTermArray = distinctUsers.stream().mapToDouble(user -> {
                    OnboardingSelf userSelf = user.getOnboardingSelf();
                    return userSelf.getMaxTerm();
                }).toArray();

        StandardScaler scaler = new StandardScaler();
        scaler.fit(ageArray);
        scaler.fit(budgetArray);
        scaler.fit(minTermArray);
        scaler.fit(maxTermArray);

        // 5. TF-IDF encoding for Amenities column
        TfIdfVectorizer vectorizer = new TfIdfVectorizer();
        try {
            vectorizer.fit(distinctUsers.stream().map(user -> {
                        OnboardingSelf userSelf = user.getOnboardingSelf();
                        return userSelf.getAmenities();
                    }).collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Preprocess each user and store the result in a new ProcessedProfile object
        List<ProcessedProfile> processedUsers = distinctUsers.stream().map(user -> {
            ProcessedProfile processed = new ProcessedProfile();

            OnboardingSelf userSelf = user.getOnboardingSelf();
            OnboardingProfile userProfile = user.getOnboardingProfile();
            OnboardingPreferences userPreferences = user.getOnboardingPreferences();



            // 3. Feature encoding
            processed.setSmoker(userSelf.getSmoker() ? 1 : 0);
            processed.setAnyPets(userSelf.getPets() ? 1 : 0);
            processed.setSmokersOk(userPreferences.getSmokersOK() ? 1 : 0);
            processed.setPetsOk(userPreferences.getPetsOK() ? 1 : 0);

            // 3.1 Hot-One Encoding
            processed.setGenderEncoding(oneHotEncode(userProfile.getGender(), uniqueGenders));
            processed.setOccupationEncoding(oneHotEncode(userProfile.getOccupation(), uniqueOccupations));

            // 4. Feature scaling: standardize age, budget, minTerm, and maxTerm using z-score
            processed.setAge(scaler.transform(userProfile.getAge(), ageArray));
            processed.setBudget(scaler.transform(userSelf.getBudget(), budgetArray));
            processed.setMinTerm(scaler.transform(userSelf.getMinTerm(), minTermArray));
            processed.setMaxTerm(scaler.transform(userSelf.getMaxTerm(), maxTermArray));

            // 5. TF-IDF encoding for Amenities column
            try {
                processed.setAmenitiesTfIdf(vectorizer.transform(userSelf.getAmenities()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 6. Outlier detection is implemented in the StandardScaler class

            // 7. Interaction features
            processed.setTermRange(processed.getMaxTerm() - processed.getMinTerm());
            processed.setSmokerInteraction(processed.getSmoker() * processed.getSmokersOk());
            processed.setPetInteraction(processed.getAnyPets() * processed.getPetsOk());


            return processed;
        }).toList();

        return processedUsers;
    }


    private Mono<TrainedModel> trainModel(List<ProcessedProfile> preprocessedData) {
        // Convert preprocessedData to a 2D array
        double[][] data = preprocessedData.stream()
                .map(ProcessedProfile::toDoubleArray)
                .toArray(double[][]::new);

        // Apply PCA
        PCA pca = PCA.fit(data);
        PCA reducedPCA = pca.getProjection(PCA_COMPONENTS);
        double[][] reducedData = reducedPCA.apply(data);

        // Convert reducedData from double[][] to Double[][]
        Double[][] convertedData = Arrays.stream(reducedData)
                .map(doubleArray -> Arrays.stream(doubleArray)
                        .boxed()
                        .toArray(Double[]::new))
                .toArray(Double[][]::new);

        CLARANS<Double[]> clarans = CLARANS.fit(convertedData, cosineDistance, K_CLUSTERS, MAX_NEIGHBOURS);

        // Create and return the trained model
        TrainedModel trainedModel = new TrainedModel(reducedPCA, clarans);
        return Mono.just(trainedModel);
    }


    private Mono<Void> saveModel(TrainedModel trainedModel) {
        // Save the trained model

        // return Mono<Void> to indicate completion
        return Mono.empty();
    }

    private void isModelExpired() {
        // Check if the model is expired
    }

    private void isModelTrained() {
        // Check if the model is trained
    }


    // HELPER METHODS
    private static int[] oneHotEncode(String value, Set<String> uniqueValues) {
        int[] encoding = new int[uniqueValues.size()];
        int index = 0;
        for (String uniqueValue : uniqueValues) {
            if (uniqueValue.equals(value)) {
                encoding[index] = 1;
            } else {
                encoding[index] = 0;
            }
            index++;
        }
        return encoding;
    }

}
