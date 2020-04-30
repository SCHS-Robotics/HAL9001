package com.SCHSRobotics.HAL9001.util.math;

import org.jetbrains.annotations.NotNull;

/**
 * A class for doing mathematical operations on 2 dimensional vectors.
 *
 * @author Cole Savage, Level Up
 * @since 0.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/11/17
 */
@SuppressWarnings({"WeakerAccess","unused"})
public class Vector {

    /**
     * Specifies the input coordinate format for the constructor.
     */
    public enum CoordinateType {
        CARTESIAN, POLAR
    }

    //Properties of the vector.
    public double x,y,r,theta;

    /**
     * Constructor for vector.
     *
     * @param inx The first input component.
     * @param iny The second input component.
     * @param inCoord The enum specifying the input format.
     */
    public Vector(double inx, double iny, @NotNull CoordinateType inCoord) {
        if (inCoord == CoordinateType.CARTESIAN) {
            this.x = inx;
            this.y = iny;
            this.r = Math.sqrt(Math.pow(inx,2)+Math.pow(iny,2));

            this.theta = inx > 0 ? Math.atan(iny/inx) : inx < 0 ? Math.atan(iny/inx) + Math.PI : inx == 0 && iny > 0 ? Math.PI/2 : inx == 0 && iny < 0 ? -Math.PI/2 : 0;
            this.theta = this.theta > 0 ? this.theta : this.theta + 2 * Math.PI; //To make everything positive, because I don't like negative angles as much
            this.r = Math.sqrt(Math.pow(inx,2)+Math.pow(iny,2));
        }
        else if(inCoord == CoordinateType.POLAR) {
            this.r = inx;
            this.theta = iny;
            this.x = inx*Math.cos(iny);
            this.y = inx*Math.sin(iny);
            this.theta = this.theta > 0 ? this.theta : this.theta + 2 * Math.PI; //To make everything positive, because I don't like negative angles as much
        }
    }

    /**
     * Constructor for vector.
     *
     * @param inx The input x component.
     * @param iny The input y component.
     */
    public Vector(double inx, double iny) {
        this.x = inx;
        this.y = iny;

        this.theta = inx > 0 ? Math.atan(iny/inx) : inx < 0 ? Math.atan(iny/inx) + Math.PI : inx == 0 && iny > 0 ? Math.PI/2 : inx == 0 && iny < 0 ? -Math.PI/2 : 0;
        this.theta = this.theta > 0 ? this.theta : this.theta + 2*Math.PI; //To make everything positive, because I don't like negative angles as much
        this.r = Math.sqrt(Math.pow(inx,2)+Math.pow(iny,2));
    }

    /**
     * Rotates the vector components about the origin by an angle theta. The rotation angle must be in radians.
     *
     * @param theta The angle to rotate the vector in radians. Counterclockwise is positive, clockwise is negative.
     */
    public void rotate(double theta) {
        if(!isZeroVector()) {
            double rotx = this.x * Math.cos(theta) - this.y * Math.sin(theta);
            double roty = this.x * Math.sin(theta) + this.y * Math.cos(theta);
            this.x = rotx;
            this.y = roty;

            this.theta += theta;
            this.theta = this.theta > 0 ? this.theta : this.theta + 2 * Math.PI; //To make everything positive, because I don't like negative angles as much
        }
    }

    /**
     * Returns if the current vector is the zero vector.
     *
     * @return Whether the x and y components of the vector both equal to 0.
     */
    public boolean isZeroVector() {
        return (this.x == 0.0) && (this.y == 0.0);
    }

    /**
     * Normalizes the vector to a specified length.
     *
     * @param length The length to normalize the vector to.
     */
    public void normalize(double length) {
        if(!isZeroVector()) {
            x = length * (x / r);
            y = length * (y / r);
            this.r = length;
        }
    }

    /**
     * Normalizes the vector to a length of 1 unit (a unit vector).
     */
    public void normalize() {
        normalize(1.0);
    }

    /**
     * Multiply the vector by a scalar.
     *
     * @param scalar A constant number.
     */
    public void scalarMultiply(double scalar) {
        this.normalize(scalar*r);
    }

    /**
     * Performs a dot product with another vector.
     *
     * @param v The second vector.
     * @return The dot product of this vector and v.
     */
    public double dotProduct(Vector v) {
        return v.x*x + v.y*y;
    }

    @Override
    public Vector clone() {
        return new Vector(x,y);
    }
}
