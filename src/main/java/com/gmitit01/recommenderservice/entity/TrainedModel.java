package com.gmitit01.recommenderservice.entity;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import smile.clustering.CLARANS;
import smile.feature.extraction.PCA;

import java.util.Date;

@Data
@Document("TrainedModel")
public class TrainedModel extends UuidIdentifiedEntity {

    private PCA pca;
    private CLARANS<Double []> clarans;
    private final Date date = new Date();

    public TrainedModel(PCA pca, CLARANS<Double[]> clarans) {
        this.pca = pca;
        this.clarans = clarans;
    }
}
