package com.SCHSRobotics.HAL9001.util.math.geometry;

public class Axis2D {
    private final Vector2D axisUnitVector;

    public Axis2D(Vector2D axisVector) {
        axisUnitVector = axisVector.clone().normalize();
    }

    public Vector2D getAxisVector() {
        return axisUnitVector.clone();
    }

    public double getX() {
        return axisUnitVector.getX();
    }

    public double getY() {
        return axisUnitVector.getY();
    }
}
