package com.gmitit01.recommenderservice.logic;


import com.gmitit01.recommenderservice.entity.*;
import com.gmitit01.recommenderservice.entity.DTO.OnboardingProfileDTO;
import com.gmitit01.recommenderservice.entity.utils.MatrixWrapper;
import com.gmitit01.recommenderservice.service.ClusteredProfileService;
import com.gmitit01.recommenderservice.service.PreProcessingMetaService;
import com.gmitit01.recommenderservice.service.TrainedModelService;
import com.gmitit01.recommenderservice.service.impl.ModelServiceImpl;
import com.gmitit01.recommenderservice.utils.CosineDistance;
import com.gmitit01.recommenderservice.utils.StandardScaler;
import com.gmitit01.recommenderservice.utils.TfIdfVectorizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;
import smile.clustering.CLARANS;
import smile.feature.extraction.PCA;
import smile.math.MathEx;
import smile.math.matrix.Matrix;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Component
@Slf4j
public class Model {

    // Constants
    private final int K_CLUSTERS = 2;
    private final int MAX_NEIGHBOURS = 2; // TODO: Change back to 30 or a more appropriate value
    private final int PCA_COMPONENTS = 4;
    private static final int VALID_HOURS = 336;

    // Injections
    private final CosineDistance cosineDistance;
    private final ModelServiceImpl modelService;
    private final TrainedModelService trainedModelService;
    private final PreProcessingMetaService preProcessingMetaService;
    private final ClusteredProfileService clusteredProfileService;


    /***
     * This method is scheduled to run daily at midnight.
     *
     * Once more than 1 RecommederService Instance is running,
     * we MUST implement a distributed lock or a leader election mechanism
     * so that only one instance is running this method per 24h.
     */
    @Scheduled(cron = "0 0 0 * * *") // This cron expression represents a daily task at midnight
    public void retrainModel() throws IOException {
        log.info("Starting Model Training...");

        List<OnboardingProfileDTO> userData = mockLoadData();
        List<ProcessedProfile> preprocessedData = preprocessData(userData);
        TrainedModel trainedModel = trainModel(preprocessedData);

        // Save ClusteredProfiles
        saveClusteredProfiles(userData, preprocessedData, trainedModel);

        // Save Model
        saveModel(trainedModel);

        log.info("Finished Model Training...");
    }

    /***
     * @return the number of PCA components used in the model.
     */
    public Integer getPCA_COMPONENTS() {
        return PCA_COMPONENTS;
    }

    /***
     * Process a single user's data
     *
     * @param user
     *
     * @return a feature vector called ProcessedProfile.
     */
    public ProcessedProfile preProcessUserData(OnboardingProfileDTO user) {
        PreProcessingMeta meta = preProcessingMetaService.readMetas().get(0);

        return preprocessDataWithMeta(List.of(user), meta).get(0);
    }


    /***
     * Checks if the model is expired.
     *
     * Expired means it has been more than VALID_HOURS hours since the model was last trained.
     *
     * @return boolean
     */
    public boolean isModelExpired() {
        // Get the list of all models
        List<TrainedModel> models = trainedModelService.readModels();

        // Check if there are any models
        if (models.isEmpty()) {
            return true;
        }

        // Get the latest model
        TrainedModel latestModel = models.get(0);

        // Convert the latest model's date to LocalDateTime
        LocalDateTime modelDate = Instant.ofEpochMilli(latestModel.getDate().getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // Check if the latest model is older than 24 hours
        LocalDateTime now = LocalDateTime.now();
        long hoursSinceLastTraining = ChronoUnit.HOURS.between(modelDate, now);

        return hoursSinceLastTraining >= VALID_HOURS;
    }





    // ----------------------------- PRIVATE METHODS ------------------------------------

    /***
     * Mock data for testing purposes.
     *
     * @return
     */
    private List<OnboardingProfileDTO> mockLoadData() {
        List<OnboardingProfileDTO> mockData = new ArrayList<>();

        // Example 1
        OnboardingProfileDTO example1 = new OnboardingProfileDTO();
        OnboardingProfile profile1 = new OnboardingProfile("John", "Anon", 22, "MALE", "straight", "english", "student", "english");
        OnboardingSelf self1 = new OnboardingSelf(false, false, 900, "double", Set.of("Central London", "Clapham", "Battersea & Wandsworth", "Zone 1", "Zone 2 - North of the River", "Zone 2 - South of the River"), 1, 54, Set.of("furnished room", "en-suite"), Set.of("cooking", "socialising", "fitness", "brunch", "weekends away"));
        OnboardingPreferences preferences1 = new OnboardingPreferences(true, false, "no preference", 18, 37, "no preference", "As we enter the new year...");
        example1.setOnboardingProfile(profile1);
        example1.setOnboardingSelf(self1);
        example1.setOnboardingPreferences(preferences1);
        mockData.add(example1);

        // Example 2
        OnboardingProfileDTO example2 = new OnboardingProfileDTO();
        OnboardingProfile profile2 = new OnboardingProfile("Wibessh", "N/A", 19, "MALE", "GAY","english", "student", "N/A");
        OnboardingSelf self2 = new OnboardingSelf(false, false, 400, "double", Set.of("Docklands"), 1, 54, Set.of("furnished room", "en-suite", "broadband", "washing machine"), Set.of("cooking", "reading", "sports", "socialising", "walking", "photography", "fitness", "fashion", "culture", "comedy", "health", "meeting new people", "cleaning", "learning"));
        OnboardingPreferences preferences2 = new OnboardingPreferences(true, true, "no preference", 18, 30, "no preference", "My name my Wibessh...");
        example2.setOnboardingProfile(profile2);
        example2.setOnboardingSelf(self2);
        example2.setOnboardingPreferences(preferences2);
        mockData.add(example2);

        // Example 3
        OnboardingProfileDTO example3 = new OnboardingProfileDTO();
        OnboardingProfile profile3 = new OnboardingProfile("Sarah", "Doe", 28, "FEMALE", "GAY", "english", "other", "english");
        OnboardingSelf self3 = new OnboardingSelf(false, false, 800, "double", Set.of("Central London", "Zone 1", "Zone 2 - North of the River"), 3, 24, Set.of("furnished room", "en-suite", "broadband"), Set.of("cooking", "movies", "fitness", "reading", "travel"));
        OnboardingPreferences preferences3 = new OnboardingPreferences(true, true, "no preference", 20, 35, "no preference", "Hi, I'm Sarah...");
        example3.setOnboardingProfile(profile3);
        example3.setOnboardingSelf(self3);
        example3.setOnboardingPreferences(preferences3);
        mockData.add(example3);

        // Example 4
        OnboardingProfileDTO example4 = new OnboardingProfileDTO();
        OnboardingProfile profile4 = new OnboardingProfile("James", "Smith", 34, "MALE", "straight", "english", "professional", "english");
        OnboardingSelf self4 = new OnboardingSelf(true, true, 1200, "double", Set.of("Zone 2 - South of the River", "Battersea & Wandsworth", "Clapham"), 6, 48, Set.of("furnished room", "en-suite", "broadband", "garden"), Set.of("gaming", "programming", "socialising", "sports", "technology"));
        OnboardingPreferences preferences4 = new OnboardingPreferences(true, false, "no preference", 25, 40, "no preference", "Hey there! I'm James...");
        example4.setOnboardingProfile(profile4);
        example4.setOnboardingSelf(self4);
        example4.setOnboardingPreferences(preferences4);
        mockData.add(example4);

        // Example 5
        OnboardingProfileDTO example5 = new OnboardingProfileDTO();
        OnboardingProfile profile5 = new OnboardingProfile("Anna", "Johnson", 26, "FEMALE", "straight", "english", "professional", "english");
        OnboardingSelf self5 = new OnboardingSelf(false, true, 750, "double", Set.of("Zone 1", "Central London", "Zone 2 - North of the River"), 6, 36, Set.of("furnished room", "en-suite", "broadband", "garden", "parking"), Set.of("cooking", "reading", "fitness", "culture", "art"));
        OnboardingPreferences preferences5 = new OnboardingPreferences(false, true, "no preference", 22, 35, "no preference", "Hello! I'm Anna...");
        example5.setOnboardingProfile(profile5);
        example5.setOnboardingSelf(self5);
        example5.setOnboardingPreferences(preferences5);
        mockData.add(example5);

        return mockData;
    }





    /***
     *  Gets data from our OnboardingService and loads it.
     *
     * @return list of complete user profiles (OnboardingProfileDTO).
     */
    private List<OnboardingProfileDTO> loadData() {
        return modelService.fetchAllUserData();
    }

    /***
     * Uses our user data to create and save the metadata needed for preprocessing.
     * Then it uses it to preprocess the data.
     *
     * @param userSet
     *
     * @return a list of ProcessedProfile objects. (Feature Vectors)
     */
    private List<ProcessedProfile> preprocessData(List<OnboardingProfileDTO> userSet) throws IOException {
        // Create and Save PreProcessMetadata
        PreProcessingMeta preProcessingMeta = createPreProcessMetadata(userSet);
        preProcessingMetaService.createMeta(preProcessingMeta);

        // Preprocess data using the created metadata
        return preprocessDataWithMeta(userSet, preProcessingMeta);
    }

    /***
     * Creates the metadata needed for preprocessing based on the whole dataset.
     *
     * @param userSet
     *
     * @return a PreProcessingMeta object.
     */
    private PreProcessingMeta createPreProcessMetadata(List<OnboardingProfileDTO> userSet) throws IOException {
        log.info("Creating PreProcessing Metadata...");

        List<OnboardingProfileDTO> distinctUsers = userSet.stream()
                .distinct()
                .toList();

        Set<String> uniqueGenders = distinctUsers.stream()
                .map(user -> user.getOnboardingProfile().getGender())
                .collect(Collectors.toSet());

        Set<String> uniqueOccupations = distinctUsers.stream()
                .map(user -> user.getOnboardingProfile().getOccupation())
                .collect(Collectors.toSet());

        double[] ageArray = distinctUsers.stream().mapToDouble(user -> user.getOnboardingProfile().getAge()).toArray();
        double[] budgetArray = distinctUsers.stream().mapToDouble(user -> user.getOnboardingSelf().getBudget()).toArray();
        double[] minTermArray = distinctUsers.stream().mapToDouble(user -> user.getOnboardingSelf().getMinTerm()).toArray();
        double[] maxTermArray = distinctUsers.stream().mapToDouble(user -> user.getOnboardingSelf().getMaxTerm()).toArray();

        StandardScaler ageScaler = new StandardScaler();
        StandardScaler budgetScaler = new StandardScaler();
        StandardScaler minTermScaler = new StandardScaler();
        StandardScaler maxTermScaler = new StandardScaler();

        ageScaler.fit(ageArray);
        budgetScaler.fit(budgetArray);
        minTermScaler.fit(minTermArray);
        maxTermScaler.fit(maxTermArray);

        TfIdfVectorizer vectorizer = new TfIdfVectorizer();
        try {
            vectorizer.fit(distinctUsers.stream().map(user -> user.getOnboardingSelf().getAmenities()).collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        PreProcessingMeta preProcessingMeta = new PreProcessingMeta();
        preProcessingMeta.setUniqueGenders(uniqueGenders);
        preProcessingMeta.setUniqueOccupations(uniqueOccupations);
        preProcessingMeta.setAgeScalerMean(ageScaler.getMean());
        preProcessingMeta.setAgeScalerScale(ageScaler.getStdDev());
        preProcessingMeta.setBudgetScalerMean(budgetScaler.getMean());
        preProcessingMeta.setBudgetScalerScale(budgetScaler.getStdDev());
        preProcessingMeta.setMinTermScalerMean(minTermScaler.getMean());
        preProcessingMeta.setMinTermScalerScale(minTermScaler.getStdDev());
        preProcessingMeta.setMaxTermScalerMean(maxTermScaler.getMean());
        preProcessingMeta.setMaxTermScalerScale(maxTermScaler.getStdDev());
        preProcessingMeta.setAmenitiesVectorizerVocabulary(vectorizer.getVocabulary());
        preProcessingMeta.setSerializedIndex(vectorizer.serializeIndex());


        log.info("Finished creating PreProcessing Metadata...");
        return preProcessingMeta;
    }

    /***
     * Where the actual preprocessing happens.
     *
     * @param userSet -- user or multiple users to be preprocessed
     * @param preProcessingMeta -- the metadata to be used in preprocessing
     *
     * @return a list equal in length to the provided userSet comprised of Feature Vectors (ProcessedProfile)
     */
    private List<ProcessedProfile> preprocessDataWithMeta(List<OnboardingProfileDTO> userSet, PreProcessingMeta preProcessingMeta) {
        log.info("Preprocessing " + userSet.size() + " users...");

        // 1. Remove duplicates
        List<OnboardingProfileDTO> distinctUsers = userSet.stream()
                .distinct()
                .toList();

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
            processed.setGenderEncoding(oneHotEncode(userProfile.getGender(), preProcessingMeta.getUniqueGenders()));
            processed.setOccupationEncoding(oneHotEncode(userProfile.getOccupation(), preProcessingMeta.getUniqueOccupations()));

            // 4. Feature scaling: standardize age, budget, minTerm, and maxTerm using z-score
            processed.setAge(standardize(userProfile.getAge(), preProcessingMeta.getAgeScalerMean(), preProcessingMeta.getAgeScalerScale()));
            processed.setBudget(standardize(userSelf.getBudget(), preProcessingMeta.getBudgetScalerMean(), preProcessingMeta.getBudgetScalerScale()));
            processed.setMinTerm(standardize(userSelf.getMinTerm(), preProcessingMeta.getMinTermScalerMean(), preProcessingMeta.getMinTermScalerScale()));
            processed.setMaxTerm(standardize(userSelf.getMaxTerm(), preProcessingMeta.getMaxTermScalerMean(), preProcessingMeta.getMaxTermScalerScale()));

            // 5. TF-IDF encoding for Amenities column
            // Create a TfIdfVectorizer using the serialized index and vocabulary
            TfIdfVectorizer vectorizer;
            try {
                vectorizer = new TfIdfVectorizer(preProcessingMeta.getSerializedIndex(), preProcessingMeta.getAmenitiesVectorizerVocabulary());
                processed.setAmenitiesTfIdf(vectorizer.transform(userSelf.getAmenities()));
            } catch (IOException e) {
                // Handle the exception (e.g., log the error, set vectorizer to null, etc.)
                e.printStackTrace();
            }

            // 7. Interaction features
            processed.setTermRange(processed.getMaxTerm() - processed.getMinTerm());
            processed.setSmokerInteraction(processed.getSmoker() * processed.getSmokersOk());
            processed.setPetInteraction(processed.getAnyPets() * processed.getPetsOk());

            return processed;
        }).toList();

        log.info("Finished preprocessing " + userSet.size() + " users...");

        return  processedUsers;
    }

    /***
     * Z-Score standardization
     *
     * @param value -- the value to be standardized
     * @param mean -- the mean of the data
     * @param stdDev -- the standard deviation of the data
     *
     * @return the standardized value
     */
    private double standardize(double value, double mean, double stdDev) {
        return (value - mean) / stdDev;
    }


    /***
     * Converts data into a matrix. Applies PCA to the data.
     * Use PCA_COMPONENTS to decide how many components to use for training.
     * Train the model on the reduced data and return it.
     *
     * @param preprocessedData -- the data to be trained on
     *
     * @return a trained CLARANS model from the SMILE library
     */
    private TrainedModel trainModel(List<ProcessedProfile> preprocessedData) {
        log.info("Beginning CLARANS training...");

        // Convert preprocessedData to a 2D array
        double[][] data = preprocessedData.stream()
                .map(ProcessedProfile::toDoubleArray)
                .toArray(double[][]::new);

        // TODO: Debug Purposes
        // Calculate covariance matrix
        double[][] covarianceMatrix = MathEx.cov(data);
        System.out.println("Covariance matrix:");
        for (double[] row : covarianceMatrix) {
            System.out.println(Arrays.toString(row));
        }

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

        log.info("Finished CLARANS training...");
        // Create and return the trained model
        PCAProperties pcaProperties = new PCAProperties();
        MatrixWrapper matrixWrapper = new MatrixWrapper(pca.loadings());
        pcaProperties.setLoadings(matrixWrapper);
        pcaProperties.setMean(pca.center());
        pcaProperties.setEigvalues(pca.variance());


        return new TrainedModel(pcaProperties, clarans);
    }


    /***
     * Saves the trained model to the database.
     *
     * @param trainedModel
     */
    private void saveModel(TrainedModel trainedModel) {
        trainedModelService.saveModel(trainedModel);
    }

    /***
     * Save the original user data, their feature vector, and their cluster
     * in the persistent store.
     *
     * @param preprocessedData -- the data to be saved
     * @param trainedModel -- the trained model to be used for clustering
     */
    private void saveClusteredProfiles(List<OnboardingProfileDTO> allUserData, List<ProcessedProfile> preprocessedData, TrainedModel trainedModel) {
        log.info("Begin saving  clustered profiles...");
        double[][] data = preprocessedData.stream()
                .map(ProcessedProfile::toDoubleArray)
                .toArray(double[][]::new);

        PCAProperties pcaProperties = trainedModel.getPca();
        // Create a Matrix object for projection, which is the identity matrix since we're not applying any transformations
        Matrix projectionMatrix = Matrix.eye(pcaProperties.getLoadings().getMatrix().nrow(), pcaProperties.getLoadings().getMatrix().ncol());
        PCA pca = new PCA(pcaProperties.getMean(), pcaProperties.getEigvalues(), pcaProperties.getLoadings().getMatrix(), projectionMatrix);


        double[][] reducedData = pca.getProjection(PCA_COMPONENTS).apply(data);

        // Convert reducedData from double[][] to Double[][]
        Double[][] convertedData = Arrays.stream(reducedData)
                .map(doubleArray -> Arrays.stream(doubleArray)
                        .boxed()
                        .toArray(Double[]::new))
                .toArray(Double[][]::new);

        for (int i = 0; i < convertedData.length; i++) {
            int cluster = trainedModel.getClarans().predict(convertedData[i]);
            ClusteredProfile clusteredProfile = new ClusteredProfile();
            clusteredProfile.setId(allUserData.get(i).getOnboardingSelf().getId());
            clusteredProfile.setOnboardingProfile(allUserData.get(i));
            clusteredProfile.setProcessedProfile(preprocessedData.get(i));
            clusteredProfile.setCluster(cluster);
            clusteredProfileService.createProfile(clusteredProfile);
        }
        log.info("Finish saving clustered profiles...");
    }

    /***
     * One-hot encodes a value.
     *
     * @param value -- the value to be encoded
     * @param uniqueValues -- the set of unique values
     *
     * @return the encoded value
     */
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
