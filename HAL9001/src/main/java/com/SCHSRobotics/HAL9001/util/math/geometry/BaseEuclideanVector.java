package com.SCHSRobotics.HAL9001.util.math.geometry;

import com.SCHSRobotics.HAL9001.util.math.units.HALAngleUnit;

import org.ejml.simple.SimpleMatrix;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static java.lang.Math.acos;
import static java.lang.Math.sqrt;

/**
 * The base class for all Euclidean vectors.
 * <p>
 * Creation Date: 7/11/17
 *
 * @param <V> This class's datatype.
 * @author Cole Savage, Level Up
 * @version 3.0.0
 * @see Vector
 * @since 1.1.0
 */
public abstract class BaseEuclideanVector<V extends BaseEuclideanVector<V>> implements Vector<V> {
    //The vector's components (cartesian).
    protected double[] components;

    /**
     * The base constructor for Euclidean vectors.
     *
     * @param components The (cartesian) components of a vector
     */
    public BaseEuclideanVector(double... components) {
        this.components = components;
    }

    @Override
    public boolean isZeroVector() {
        boolean isZero = true;
        for (double component : components) isZero &= component == 0;
        return isZero;
    }

    @Override
    public boolean isNormalTo(V vector) {
        return this.dot(vector) == 0;
    }

    @Override
    public boolean isUnitVector() {
        return this.magnitude() == 1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public double magnitude() {
        return sqrt(this.dot((V) this));
    }

    @Override
    public double angleTo(V vector, HALAngleUnit angleUnit) {
        return HALAngleUnit.RADIANS.convertTo(angleUnit).apply(acos(this.dot(vector) / (this.magnitude() * vector.magnitude())));
    }

    /**
     * Gets the angle between two vectors. Defaults to radians.
     *
     * @param vector The vector to get the angle to.
     * @return The angle between two vectors, defaults to radians.
     */
    public double angleTo(V vector) {
        return angleTo(vector, HALAngleUnit.RADIANS);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V normalize() {
        double norm = this.magnitude();
        for (int i = 0; i < components.length; i++) components[i] /= norm;
        return (V) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V add(@NotNull V vector) {
        double[] otherComponents = vector.components;
        for (int i = 0; i < components.length; i++) components[i] += otherComponents[i];
        return (V) this;
    }

    @Override
    public V subtract(@NotNull V vector) {
        return add(vector.multiply(-1));
    }

    @SuppressWarnings("unchecked")
    @Override
    public V multiply(double scalar) {
        for (int i = 0; i < components.length; i++) components[i] *= scalar;
        return (V) this;
    }

    @Override
    public V divide(double scalar) {
        return multiply(1 / scalar);
    }

    public V negate() {
        return this.multiply(-1);
    }

    public V scaleTo(double scale) {
        return this.normalize().multiply(scale);
    }

    @Override
    public double dot(@NotNull V vector) {
        double[] otherComponents = vector.components;
        double total = 0;
        for (int i = 0; i < components.length; i++) total += components[i] * otherComponents[i];
        return total;
    }

    @Override
    public V project(@NotNull V ontoVector) {
        return ontoVector.multiply(this.dot(ontoVector) / ontoVector.dot(ontoVector));
    }

    @NotNull
    @Override
    public String toString() {
        String componentArrayString = Arrays.toString(components);
        return "<" + componentArrayString.substring(1, componentArrayString.length() - 1) + '>';
    }

    @Override
    public SimpleMatrix toMatrix() {
        double[][] vectorMatrix = new double[components.length][1];
        for (int i = 0; i < components.length; i++) vectorMatrix[i] = new double[]{components[i]};
        return new SimpleMatrix(vectorMatrix);
    }

    /**
     * Clones the vector.
     *
     * @return A copy of this vector.
     */
    public abstract V clone();
}