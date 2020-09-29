package com.SCHSRobotics.HAL9001.util.math.quantities;

import com.SCHSRobotics.HAL9001.util.math.units.AngleUnits;

public class Vector3D extends BaseEuclideanVector<Vector3D> {
    private static final Vector3D ZERO_VECTOR = new Vector3D(0, 0, 0);

    public Vector3D(double a, double b, double c, CoordinateSystem3D coordinateSystem) {
        super(coordinateSystem.convertTo(CoordinateSystem3D.CARTESIAN).apply(new double[]{a, b, c}));
    }

    public Vector3D(double x, double y, double z) {
        this(x, y, z, CoordinateSystem3D.CARTESIAN);
    }

    public Vector3D(double r, double theta, AngleUnits angleUnit, double z) {
        this(r, angleUnit.convertTo(AngleUnits.RADIANS).apply(theta), z, CoordinateSystem3D.CYLINDRICAL);
    }

    public Vector3D(double rho, double phi, AngleUnits phiUnit, double theta, AngleUnits thetaUnit) {
        this(rho, phiUnit.convertTo(AngleUnits.RADIANS).apply(phi), thetaUnit.convertTo(AngleUnits.RADIANS).apply(theta));
    }

    private Vector3D(Vector3D v) {
        components = v.components;
    }

    public static Vector3D getZeroVector() {
        return ZERO_VECTOR.clone();
    }

    public double getXComponent() {
        return components[0];
    }

    public double getYComponent() {
        return components[1];
    }

    public double getZComponent() {
        return components[2];
    }

    public Vector3D cross(Vector3D vector) {
        return new Vector3D(
                this.getYComponent() * vector.getZComponent() - this.getZComponent() * vector.getYComponent(),
                this.getZComponent() * vector.getXComponent() - this.getXComponent() * vector.getZComponent(),
                this.getXComponent() * vector.getYComponent() - this.getYComponent() * vector.getXComponent()
        );
    }

    @Override
    public Vector3D clone() {
        return new Vector3D(this);
    }
}
