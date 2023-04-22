package com.gmitit01.recommenderservice.entity;


import com.gmitit01.recommenderservice.entity.utils.UuidIdentifiedEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import smile.clustering.CLARANS;

import java.util.Date;

@Data
@Document("TrainedModel")
public class TrainedModel extends UuidIdentifiedEntity {

    @Field(value = "pca", write = Field.Write.ALWAYS)
    private PCAProperties pca;

    @Field(value = "clarans", write = Field.Write.ALWAYS)
    private CLARANS<Double []> clarans;

    @Field(value = "date", write = Field.Write.ALWAYS)
    private Date date = new Date();

    public TrainedModel(PCAProperties pca, CLARANS<Double[]> clarans) {
        this.pca = pca;
        this.clarans = clarans;
    }
}
