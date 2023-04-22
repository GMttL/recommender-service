package com.gmitit01.recommenderservice.converter;

import com.gmitit01.recommenderservice.utils.CosineDistance;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import smile.clustering.CLARANS;

import java.util.Arrays;

public class CLARANSWriteConverter implements Converter<CLARANS<Double []>, Document> {

    @Override
    public Document convert(CLARANS<Double []> source) {
        Document document = new Document();
        document.put("distortion", source.distortion);
        document.put("centroids", Arrays.asList(source.centroids));
        document.put("y", Arrays.stream(source.y).boxed().toList());
        document.put("distance", CosineDistance.class);

        return document;
    }
}
