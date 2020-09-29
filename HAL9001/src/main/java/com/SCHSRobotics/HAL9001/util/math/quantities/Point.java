package com.SCHSRobotics.HAL9001.util.math.quantities;

public interface Point<V extends Vector<V>, P extends Point<V, P>> {
    double[] getCoordinates();

    double distanceTo(P point);

    V vectorTo(P point);
}
