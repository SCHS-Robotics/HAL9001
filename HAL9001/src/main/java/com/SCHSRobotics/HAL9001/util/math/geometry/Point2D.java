package com.SCHSRobotics.HAL9001.util.math.geometry;

import com.SCHSRobotics.HAL9001.util.math.units.HALAngleUnit;

public class Point2D extends BaseEuclideanPoint<Vector2D, Point2D> {
    private static final Point2D ORIGIN = new Point2D(0, 0);
    double x, y;
    double r, theta;

    public Point2D(double a, double b, CoordinateSystem2D coordinateSystem) {
        super(coordinateSystem.convertTo(CoordinateSystem2D.CARTESIAN).apply(new double[]{a, b}));
        x = coordinates[0];
        y = coordinates[1];
        updatePolarValues(x, y);
    }

    public Point2D(double x, double y) {
        this(x, y, CoordinateSystem2D.CARTESIAN);
    }

    private Point2D(Point2D point) {
        this.coordinates = point.coordinates;
        this.x = point.x;
        this.y = point.y;
        this.r = point.r;
        this.theta = point.theta;
    }

    public Point2D(double r, double theta, HALAngleUnit angleUnit) {
        this(r, angleUnit.convertTo(HALAngleUnit.RADIANS).apply(theta), CoordinateSystem2D.POLAR);
    }

    public static Point2D getOrigin() {
        return ORIGIN.clone();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        coordinates[0] = x;
        updatePolarValues(x, y);
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        coordinates[1] = y;
        updatePolarValues(x, y);
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
        updateCartesianValues(r, theta);
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
        updateCartesianValues(r, theta);
    }

    private void updateCartesianValues(double r, double theta) {
        double[] cartesianPoint = CoordinateSystem2D.POLAR.convertTo(CoordinateSystem2D.CARTESIAN).apply(new double[]{r, theta});
        x = cartesianPoint[0];
        y = cartesianPoint[1];
        coordinates[0] = x;
        coordinates[1] = y;
    }

    private void updatePolarValues(double x, double y) {
        double[] polarPoint = CoordinateSystem2D.CARTESIAN.convertTo(CoordinateSystem2D.POLAR).apply(new double[]{x, y});
        r = polarPoint[0];
        theta = polarPoint[1];
    }

    @Override
    public Vector2D vectorTo(Point2D point) {
        return new Vector2D(this, point);
    }

    @Override
    public Point2D clone() {
        return new Point2D(this);
    }
}
