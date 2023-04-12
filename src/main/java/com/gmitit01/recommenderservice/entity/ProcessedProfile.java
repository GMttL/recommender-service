package com.gmitit01.recommenderservice.entity;

import lombok.Data;

@Data
public class ProcessedProfile {

    private double age;
    private int[] genderEncoding;
    private int[] occupationEncoding;
    private int smoker;
    private int anyPets;
    private double budget;
    private double minTerm;
    private double maxTerm;
    private int smokersOk;
    private int petsOk;
    private double[] amenitiesTfIdf;
    private double termRange;
    private int smokerInteraction;
    private int petInteraction;


    public double[] toDoubleArray() {
        double[] array = new double[genderEncoding.length + occupationEncoding.length + amenitiesTfIdf.length + 11];
        int index = 0;

        array[index++] = age;
        for (int genderValue : genderEncoding) {
            array[index++] = genderValue;
        }
        for (int occupationValue : occupationEncoding) {
            array[index++] = occupationValue;
        }
        array[index++] = smoker;
        array[index++] = anyPets;
        array[index++] = budget;
        array[index++] = minTerm;
        array[index++] = maxTerm;
        array[index++] = smokersOk;
        array[index++] = petsOk;
        for (double amenityValue : amenitiesTfIdf) {
            array[index++] = amenityValue;
        }
        array[index++] = termRange;
        array[index++] = smokerInteraction;
        array[index] = petInteraction;

        return array;
    }

}
