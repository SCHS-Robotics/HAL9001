package com.SCHSRobotics.HAL9001.util.math;

public class Axis {
    private Vector unitVector;
    public Axis(Vector axisVector) {
        unitVector = axisVector.normalize();
    }
}
