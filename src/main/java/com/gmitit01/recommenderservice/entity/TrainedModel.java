package com.gmitit01.recommenderservice.entity;


import lombok.Data;
import smile.clustering.CLARANS;
import smile.feature.extraction.PCA;

import java.util.Date;

@Data
public class TrainedModel {

    private final PCA pca;
    private final CLARANS<Double []> clarans;
    private final Date date = new Date();
}
