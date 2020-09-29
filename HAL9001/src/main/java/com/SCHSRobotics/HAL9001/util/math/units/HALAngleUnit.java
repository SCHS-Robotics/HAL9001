package com.SCHSRobotics.HAL9001.util.math.units;


import org.firstinspires.ftc.robotcore.external.function.Function;

public enum HALAngleUnit {
    DEGREES, RADIANS;

    public Function<Double, Double> convertTo(HALAngleUnit angleUnit) {
        if (this.equals(angleUnit)) {
            return Double::doubleValue;
        } else if (angleUnit.equals(RADIANS)) {
            return Math::toRadians;
        } else {
            return Math::toDegrees;
        }
    }
}
