package com.SCHSRobotics.HAL9001.util.math;

import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.atan2;

public class Vector {

    protected double[] components;
    protected double[] eulerAngles;
    protected double[] angles;
    protected double magnitude;
    protected AngleUnit angleUnit;

    /**
     * Specifies the input coordinate format for the constructor.
     */
    public enum CoordinateType {
        CARTESIAN, POLAR
    }

    public Vector(@NotNull AngleUnit angleUnit, @NotNull CoordinateType coordinateType, double... components) {
        this.angleUnit = angleUnit;
        this.components = components;

        if(coordinateType == CoordinateType.CARTESIAN) {
            magnitude = 0;
            for (double component : components) {
                magnitude += component * component;
            }
            magnitude = Math.sqrt(magnitude);

            eulerAngles = new double[components.length];
            angles = new double[components.length-1];
            double modulus = angleUnit == AngleUnit.DEGREES ? 360 : 2*PI;

            for(int i = 0; i < components.length; i++) {
                eulerAngles[i] = FakeNumpy.mod(acos(components[i]/magnitude), modulus);
            }
            for(int i = 0; i < components.length - 1; i++){
                angles[i] = FakeNumpy.mod(atan2(components[i],components[components.length-1]),modulus);
            }
        }
        else {
            magnitude = components[0];
            eulerAngles = new double[components.length];
            angles = new double[components.length - 1];

            angles = Arrays.copyOfRange(components,1,components.length);

            //TODO calculate euler angles

        }
    }

    public Vector(@NotNull AngleUnit angleUnit, double... components) {
        this(angleUnit, CoordinateType.CARTESIAN, components);
    }

    public Vector(double... components) {
        this(AngleUnit.RADIANS, CoordinateType.CARTESIAN, components);
    }

    /**
     * Returns if the current vector is the zero vector.
     *
     * @return Whether the x and y components of the vector both equal to 0.
     */
    public boolean isZeroVector() {
        boolean isZero = true;
        for(double component : components) {
            isZero &= Math.abs(component) < 1e-7; //To account for floating point errors.
        }
        return isZero;
    }

    /**
     * Normalizes the vector to a specified length.
     *
     * @param length The length to normalize the vector to.
     *
     * @return A scaled version of this vector.
     */
    public Vector scaleTo(double length) {
        if(isZeroVector()) {
            return new Vector(angleUnit, CoordinateType.CARTESIAN,components);
        }

        double[] scaledComponents = components.clone();

        FakeNumpy.divide(scaledComponents,magnitude);
        FakeNumpy.multiply(scaledComponents,length);
        return new Vector(angleUnit,CoordinateType.CARTESIAN,scaledComponents);
    }

    /**
     * Normalizes the vector to a length of 1 unit (a unit vector).
     *
     * @return A unit vector in the direction of this vector
     */
    public Vector normalize() {
        return scaleTo(1);
    }

    /**
     * Multiply the vector by a scalar.
     *
     * @param scalar A constant number.
     *
     * @return A vector whose magnitude has been multiplied by a scalar
     */
    public Vector scalarMultiply(double scalar) {
        return scaleTo(scalar*magnitude);
    }

    public double dotProduct(@NotNull Vector vector) {
        ExceptionChecker.assertEqual(components.length,vector.components.length, new DumpsterFireException("Vectors must be of the same dimension to be defined in the dot product."));
        double product = 0;
        for(int i = 0; i < components.length; i++) {
            product += components[i]*vector.components[i];
        }
        return product;
    }

    public Vector crossProduct(@NotNull Vector vector) {
        int stopIdx;

        ExceptionChecker.assertTrue(components.length <= 3, new DumpsterFireException("This vector must have at most 3 dimensions in order perform the cross product (7-dimensional operations not currently supported)"));

        double[] this3DComponents = new double[3];
        stopIdx = 0;
        for(int i = 0; i < components.length; i++) {
            this3DComponents[i] = components[i];
            stopIdx = i;
        }
        for(int i = stopIdx + 1; i < this3DComponents.length; i++) {
            this3DComponents[i] = 0;
        }

        ExceptionChecker.assertTrue(vector.components.length <= 3, new DumpsterFireException("Multiplied vector must have at most 3 dimensions in order perform the cross product (7-dimensional operations not currently supported)"));

        double[] vector3DComponents = new double[3];
        stopIdx = 0;
        for(int i = 0; i < vector.components.length; i++) {
            vector3DComponents[i] = vector.components[i];
            stopIdx = i;
        }
        for(int i = stopIdx + 1; i < vector3DComponents.length; i++) {
            this3DComponents[i] = 0;
        }

        double[] crossProductComponents = new double[] {
                this3DComponents[1]*vector3DComponents[2] - this3DComponents[2]*vector3DComponents[1],
                -this3DComponents[0]*vector3DComponents[2] + this3DComponents[2]*vector3DComponents[0],
                this3DComponents[0]*vector3DComponents[1] - this3DComponents[1]*vector3DComponents[0]
        };

        return new Vector(angleUnit,CoordinateType.CARTESIAN,crossProductComponents);
    }

    public Vector project(@NotNull Vector vector) {
        return vector.normalize().scalarMultiply(this.dotProduct(vector)/vector.magnitude);
    }

    public Matrix toMatrix() {
        return new Matrix(this);
    }

    public int dimensionality() {
        return components.length;
    }

    public static double calcAngle(@NotNull Vector v1, @NotNull Vector v2, @NotNull AngleUnit angleUnit) {
        double thetaRadians = v1.dotProduct(v2)/(v1.magnitude*v2.magnitude);
        return angleUnit == AngleUnit.DEGREES ? Math.toDegrees(thetaRadians) : thetaRadians;
    }

    public double[] getComponents() {
        return components;
    }
}
