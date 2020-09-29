package com.SCHSRobotics.HAL9001.util.math.geometry;

public class Line<V extends Vector<V>, P extends BaseEuclideanPoint<V, P>> {
    protected P startPoint, endPoint;
    protected V vector;

    public Line(P startPoint, P endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        vector = startPoint.vectorTo(endPoint);
    }

    public double distanceTo(P point) {
        return vector.subtract(startPoint.vectorTo(point).project(vector)).norm();
    }

    public double length() {
        return startPoint.distanceTo(endPoint);
    }
}
