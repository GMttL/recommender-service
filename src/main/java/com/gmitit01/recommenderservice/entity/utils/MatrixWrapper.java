package com.gmitit01.recommenderservice.entity.utils;

import org.springframework.data.annotation.PersistenceConstructor;
import smile.math.matrix.Matrix;



public class MatrixWrapper {

    private Matrix matrix;

    public MatrixWrapper() {
        this.matrix = new Matrix(1, 1); // Initialize with a default value
    }

    public MatrixWrapper(Matrix matrix) {
        this.matrix = matrix;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }
}
