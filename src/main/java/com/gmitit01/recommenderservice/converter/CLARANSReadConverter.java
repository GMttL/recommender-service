package com.gmitit01.recommenderservice.converter;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import smile.clustering.CLARANS;
import smile.math.distance.Distance;

import java.util.List;

@ReadingConverter
public class CLARANSReadConverter implements Converter<Document, CLARANS> {

    @Override
    public CLARANS convert(Document source) {
        double distortion = source.getDouble("distortion");
        Object[] medoids = source.get("centroids", List.class).toArray();
        int[] y = source.get("y", List.class).stream().mapToInt(i -> (int) i).toArray();
        String distanceClassName = source.get("distance", Document.class).get("_class", String.class);

        Distance<Double[]> distance = null;
        try {
            Class<?> distanceClass = Class.forName(distanceClassName);
            distance = (Distance<Double[]>) distanceClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CLARANS(distortion, medoids, y, distance);
    }
}