package com.SCHSRobotics.HAL9001.util.math.geometry;

import static java.lang.Math.sqrt;

public abstract class BaseEuclideanPoint<V extends Vector<V>, P extends BaseEuclideanPoint<V, P>> implements Point<V, P> {

    protected double[] coordinates;

    public BaseEuclideanPoint(double... coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public double[] getCoordinates() {
        return coordinates;
    }

    @Override
    public double distanceTo(P point) {
        double dst = 0;
        for (int i = 0; i < coordinates.length; i++) {
            dst += (coordinates[i] - point.coordinates[i]) * (coordinates[i] - point.coordinates[i]);
        }
        return sqrt(dst);
    }

    public double distanceTo(Line<V, P> line) {
        return line.distanceTo((P) this);
    }

    public abstract P clone();

    /*
    private static double[] unboxArray(Double[] array) {
        double[] output = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            output[i] = array[i];
        }
        return output;
    }

    private static Double[] boxArray(double[] array) {
        Double[] output = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            output[i] = array[i];
        }
        return output;
    }
     */
}
