package com.gmitit01.recommenderservice.entity;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Data
@Document("PreprocessingMeta")
public class PreProcessingMeta extends UuidIdentifiedEntity {

    private Set<String> uniqueGenders;
    private Set<String> uniqueOccupations;

    // Parameters for StandardScaler
    private double ageScalerMean;
    private double ageScalerScale;
    private double budgetScalerMean;
    private double budgetScalerScale;
    private double minTermScalerMean;
    private double minTermScalerScale;
    private double maxTermScalerMean;
    private double maxTermScalerScale;

    // Parameters for TfIdfVectorizer
    private List<String> amenitiesVectorizerVocabulary;
}
