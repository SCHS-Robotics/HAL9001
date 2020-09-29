package com.SCHSRobotics.HAL9001.util.math.units;


import org.firstinspires.ftc.robotcore.external.function.Function;

public enum AngleUnits {
    DEGREES, RADIANS;

    public Function<Double, Double> convertTo(AngleUnits angleUnit) {
        if (this.equals(angleUnit)) {
            return Double::doubleValue;
        } else if (angleUnit.equals(RADIANS)) {
            return Math::toRadians;
        } else {
            return Math::toDegrees;
        }
    }
}
