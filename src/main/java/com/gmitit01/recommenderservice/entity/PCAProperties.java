package com.gmitit01.recommenderservice.entity;


import com.gmitit01.recommenderservice.entity.utils.MatrixWrapper;
import lombok.Data;


@Data
public class PCAProperties {

    private MatrixWrapper loadings;
    private double[] mean;
    private double[] eigvalues;
}