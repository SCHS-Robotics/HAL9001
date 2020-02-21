package com.SCHSRobotics.HAL9001.util.math;


import org.jetbrains.annotations.NotNull;
import org.opencv.core.Mat;

public class Matrix {

    private Mat vals;
    public Matrix(double[][] vals) {
        this.vals = new Mat();
        put(vals);
    }

    public Matrix(@NotNull Vector vector) {
        this.vals = new Mat();
        double[][] vals = new double[vector.dimensionality()][1];
        double[] components = vector.getComponents();
        for (int i = 0; i < vector.dimensionality(); i++) {
            vals[i] = new double[] {components[i]};
        }
        put(vals);
    }

    public Matrix(Mat vals) {
        this.vals = vals;
    }

    private void put(@NotNull double[][] vals) {
        for(int row = 0; row < vals.length; row++){
            for(int col = 0; col < vals[0].length; col++) {
                this.vals.put(row, col, vals[row][col]);
            }
        }
    }

    public void transpose() {

    }

    public void trace() {

    }

    public void invert() {

    }

    public void multiply() {

    }

    public void determinant() {

    }

    public void scalarMultiply() {

    }

    public void scalarDivide() {

    }

    public void mask() {

    }

    public void rref() {

    }

    public void ref() {

    }

    public void rank() {

    }

    public void nullity() {

    }

    public void dimensionality() {

    }

    public void isZeroMatrix() {

    }

    public void isIdentityMatrix() {

    }

    public static void identityMatrix() {

    }

    public static void zeroMatrix() {

    }

    public static void onesMatrix() {

    }
}
