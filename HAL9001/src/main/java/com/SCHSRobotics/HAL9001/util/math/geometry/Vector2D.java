package com.SCHSRobotics.HAL9001.util.math.geometry;

import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.exceptions.HALMathException;
import com.SCHSRobotics.HAL9001.util.math.FakeNumpy;
import com.SCHSRobotics.HAL9001.util.math.HALMathUtil;
import com.SCHSRobotics.HAL9001.util.math.units.HALAngleUnit;

import org.ejml.simple.SimpleMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * A 2D Vector class.
 * <p>
 * Creation Date: 7/11/17
 *
 * @author Cole Savage, Level Up
 * @version 3.0.0
 * @see Vector
 * @see BaseEuclideanVector
 * @see Vector3D
 * @see Point2D
 * @since 1.0.0
 */
public class Vector2D extends BaseEuclideanVector<Vector2D> {
    //The zero vector for 2 dimensions.
    private static final Vector2D ZERO_VECTOR = new Vector2D(0, 0);

    /**
     * A constructor for Vector2D.
     *
     * @param start The vector's start point.
     * @param end   The vector's end point.
     * @see Point2D
     * @see FakeNumpy
     */
    public Vector2D(@NotNull Point2D start, @NotNull Point2D end) {
        super(FakeNumpy.subtract(end.coordinates, start.coordinates));
    }

    /**
     * A constructor for Vector2D. Assumes start point of the vector is the origin.
     *
     * @param end The vector's end point.
     * @see Point2D
     */
    public Vector2D(Point2D end) {
        this(Point2D.getOrigin(), end);
    }

    /**
     * A constructor for Vector2D.
     *
     * @param x The vector's x component.
     * @param y The vector's y component.
     * @see Point2D
     */
    public Vector2D(double x, double y) {
        this(new Point2D(x, y));
    }

    /**
     * A constructor for Vector2D.
     *
     * @param r         The vector's magnitude.
     * @param theta     The vector's angle relative to the positive x axis.
     * @param angleUnit The units of the entered angle.
     */
    public Vector2D(double r, double theta, @NotNull HALAngleUnit angleUnit) {
        this(new Point2D(r, theta, angleUnit));
    }

    /**
     * A private constructor for Vector2D, used for cloning purposes.
     *
     * @param v The vector to clone.
     */
    private Vector2D(@NotNull Vector2D v) {
        components = v.components.clone();
    }

    /**
     * Converts a 2D vector in (cartesian) matrix form into a Vector2D object.
     *
     * @param inputMatrix The (cartesian) matrix form of a 2D vector. Can be a column matrix or a row matrix.
     * @return The Vector2D representation of the given vector.
     * @throws HALMathException Throws this exception if the input is not a vector matrix or if the input is not 2 dimensional.
     */
    @NotNull
    @Contract("_ -> new")
    public static Vector2D fromMatrix(@NotNull SimpleMatrix inputMatrix) {
        ExceptionChecker.assertTrue(inputMatrix.isVector(), new HALMathException("Input matrix is not a vector matrix."));

        SimpleMatrix vectorMatrix;
        if (inputMatrix.numRows() == 1) vectorMatrix = inputMatrix.transpose();
        else vectorMatrix = inputMatrix.copy();

        ExceptionChecker.assertTrue(vectorMatrix.numRows() == CoordinateSystem2D.CARTESIAN.dimensionality(), new HALMathException("Input must be a 2D vector in matrix form."));

        return new Vector2D(vectorMatrix.get(0, 0), vectorMatrix.get(1, 0));
    }

    /**
     * Gets the zero vector for two dimensions.
     *
     * @return The zero vector for two dimensions.
     */
    public static Vector2D getZeroVector() {
        return ZERO_VECTOR.clone();
    }

    /**
     * Gets the vector's x component.
     *
     * @return The vector's x component.
     */
    public double getX() {
        return components[0];
    }

    /**
     * Gets the vector's y component.
     *
     * @return The vector's y component.
     */
    public double getY() {
        return components[1];
    }

    /**
     * Gets the vector's angle relative to the positive x axis.
     *
     * @return The vector's angle relative to the positive x axis.
     */
    public double getAngle() {
        return atan2(getY(), getX());
    }

    /**
     * Rotates this vector counterclockwise by the given angle. (counterclockwise is positive, clockwise is negative)
     *
     * @param angle     The angle to rotate this vector by.
     * @param angleUnit The units of the given angle (units are radians by default).
     * @return This vector.
     */
    public Vector2D rotate(double angle, AngleUnit angleUnit) {
        if (!isZeroVector()) {
            double theta = angleUnit.toRadians(angle);
            double rotX = getX() * cos(theta) - getY() * sin(theta);
            double rotY = getX() * sin(theta) + getY() * cos(theta);
            components[0] = HALMathUtil.floatingPointFix(rotX);
            components[1] = HALMathUtil.floatingPointFix(rotY);
        }
        return this;
    }

    /**
     * Rotates this vector counterclockwise by the given angle. (counterclockwise is positive, clockwise is negative)
     *
     * @param angle The angle to rotate this vector by (units are radians by default).
     * @return This vector.
     */
    public Vector2D rotate(double angle) {
        return rotate(angle, AngleUnit.RADIANS);
    }

    /**
     * Calculates the 2D cross product of this vector with another given vector.
     *
     * @param vector The vector to multiply by (cross product).
     * @return The cross product of this vector and the given vector.
     */
    public Vector3D cross(@NotNull Vector2D vector) {
        return new Vector3D(0, 0, this.getX() * vector.getY() + vector.getX() * this.getY());
    }

    @Override
    public Vector2D clone() {
        return new Vector2D(this);
    }
}