package com.gmitit01.recommenderservice.utils;

import org.springframework.stereotype.Component;
import smile.math.distance.Distance;

@Component
public class CosineDistance implements Distance<Double[]> {

    @Override
    public double d(Double[] doubles, Double[] t1) {
        if (doubles.length != t1.length) {
            throw new IllegalArgumentException("Input arrays have different lengths: " + doubles.length + " and " + t1.length);
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < doubles.length; i++) {
            dotProduct += doubles[i] * t1[i];
            normA += doubles[i] * doubles[i];
            normB += t1[i] * t1[i];
        }
        return 1 - (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));
    }

    public double cosineSimilarity(Double[] vec1, Double[] vec2) {
        double cosineDist = d(vec1, vec2);
        return 1 - cosineDist;
    }

}
