package com.SCHSRobotics.HAL9001.util.math.quantities;

import com.SCHSRobotics.HAL9001.util.math.units.AngleUnits;

public interface Vector<V extends Vector<V>> {
    double[] getComponents();

    boolean isZeroVector();

    boolean isUnitVector();

    boolean isNormalTo(V vector);

    double norm();

    double angleTo(V vector, AngleUnits unit);

    V normalize();

    V add(V vector);

    V subtract(V vector);

    V multiply(double scalar);

    V divide(double scalar);

    V negate();

    V scaleTo(double scale);

    double dot(V vector);

    V project(V ontoVector);
}
