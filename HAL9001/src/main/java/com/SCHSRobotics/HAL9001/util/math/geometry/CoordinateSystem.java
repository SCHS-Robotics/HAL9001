package com.SCHSRobotics.HAL9001.util.math.geometry;

import org.firstinspires.ftc.robotcore.external.function.Function;

public interface CoordinateSystem<T extends CoordinateSystem<T>> {
    int dimensionality();

    Function<double[], double[]> convertTo(T coordinateSystem);
}
