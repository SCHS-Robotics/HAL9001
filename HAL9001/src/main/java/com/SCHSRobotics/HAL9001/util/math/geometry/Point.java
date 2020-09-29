package com.SCHSRobotics.HAL9001.util.math.geometry;

public interface Point<V extends Vector<V>, P extends Point<V, P>> {
    double[] getCoordinates();

    double distanceTo(P point);

    V vectorTo(P point);
}
