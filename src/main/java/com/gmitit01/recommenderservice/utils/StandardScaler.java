package com.gmitit01.recommenderservice.utils;

import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class StandardScaler {
    private double mean;
    private double stdDev;

    public void fit(double[] data) {
        mean = Arrays.stream(data).average().orElse(0);
        stdDev = Math.sqrt(Arrays.stream(data).map(x -> Math.pow(x - mean, 2)).average().orElse(0));
    }

    public double transform(double value, double[] data) {
        double zScore = (value - mean) / stdDev;

        // Outlier detection based on Z-score
        // Note: Customize the threshold according to your requirements
        double threshold = 3.0;
        if (Math.abs(zScore) > threshold) {
            // Handle outlier
            // You can choose different strategies, such as capping or imputing
            // Here, we cap the value at the threshold
            value = mean + (threshold * stdDev * (zScore > 0 ? 1 : -1));
        }

        return (value - mean) / stdDev;
    }
}
