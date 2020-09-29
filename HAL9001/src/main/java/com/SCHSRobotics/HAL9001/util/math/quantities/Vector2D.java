package com.SCHSRobotics.HAL9001.util.math.quantities;

import com.SCHSRobotics.HAL9001.util.math.FakeNumpy;
import com.SCHSRobotics.HAL9001.util.math.units.AngleUnits;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Vector2D extends BaseEuclideanVector<Vector2D> {
    private static final Vector2D ZERO_VECTOR = new Vector2D(0, 0);

    public Vector2D(Point2D start, Point2D end) {
        super(FakeNumpy.subtract(end.coordinates, start.coordinates));
    }

    public Vector2D(Point2D end) {
        this(Point2D.getOrigin(), end);
    }

    public Vector2D(double x, double y) {
        this(new Point2D(x, y));
    }

    public Vector2D(double r, double theta, AngleUnits angleUnit) {
        super(CoordinateSystem2D.POLAR.convertTo(CoordinateSystem2D.CARTESIAN).apply(new double[]{r, angleUnit.convertTo(AngleUnits.RADIANS).apply(theta)}));
    }

    private Vector2D(Vector2D v) {
        components = v.components.clone();
    }

    public static Vector2D getZeroVector() {
        return ZERO_VECTOR.clone();
    }

    public double getX() {
        return components[0];
    }

    public double getY() {
        return components[1];
    }

    public double getAngle() {
        return atan2(getY(), getX());
    }

    public Vector2D rotate(double angle, AngleUnits angleUnit) {
        if (!isZeroVector()) {
            double theta = angleUnit.convertTo(AngleUnits.RADIANS).apply(angle);
            double rotX = getX() * cos(theta) - getY() * sin(theta);
            double rotY = getX() * sin(theta) + getY() * cos(theta);
            components[0] = (double) Math.round(1e9 * rotX) / 1e9;
            components[1] = (double) Math.round(1e9 * rotY) / 1e9;
        }
        return this;
    }

    public Vector2D rotate(double angle) {
        return rotate(angle, AngleUnits.RADIANS);
    }

    public Vector3D cross(Vector2D vector) {
        return new Vector3D(0, 0, this.getX() * vector.getY() + vector.getX() * this.getY());
    }

    @Override
    public Vector2D clone() {
        return new Vector2D(this);
    }
}
