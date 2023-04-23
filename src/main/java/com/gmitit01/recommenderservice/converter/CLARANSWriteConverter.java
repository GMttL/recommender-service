package com.gmitit01.recommenderservice.converter;

import com.gmitit01.recommenderservice.utils.CosineDistance;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import smile.clustering.CLARANS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WritingConverter
public class CLARANSWriteConverter implements Converter<CLARANS<Double []>, Document> {

    @Override
    public Document convert(CLARANS<Double []> source) {
        Document document = new Document();
        document.put("distortion", source.distortion);
        document.put("centroids", convertCentroidsToNestedList(source.centroids));
        document.put("y", Arrays.stream(source.y).boxed().toList());
        document.put("distance", CosineDistance.class);

        return document;
    }

    private List<List<Double>> convertCentroidsToNestedList(Double[][] centroids) {
        List<List<Double>> nestedList = new ArrayList<>();
        for (Double[] centroid : centroids) {
            nestedList.add(Arrays.asList(centroid));
        }
        return nestedList;
    }
}
