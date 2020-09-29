package com.SCHSRobotics.HAL9001.util.math.geometry;

public class Axis3D {
    private final Vector3D axisUnitVector;

    public Axis3D(Vector3D axisVector) {
        axisUnitVector = axisVector.clone().normalize();
    }

    public Vector3D getAxisVector() {
        return axisUnitVector.clone();
    }

    public double getX() {
        return axisUnitVector.getXComponent();
    }

    public double getY() {
        return axisUnitVector.getYComponent();
    }

    public double getZ() {
        return axisUnitVector.getZComponent();
    }
}