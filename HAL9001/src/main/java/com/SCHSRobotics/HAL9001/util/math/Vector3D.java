package com.SCHSRobotics.HAL9001.util.math;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Vector3D extends Vector {
    private double x,y,z;
    public Vector3D(double a, double b, double c, CoordinateType coordinateType, AngleUnit angleUnit) {
        super(angleUnit, coordinateType, a, b, c);
        if(coordinateType == CoordinateType.CARTESIAN) {
            x = a;
            y = b;
            z = c;
        }
        else {
            x = magnitude*cos(angles[0])*cos(angles[1]);
            y = magnitude*cos(angles[0])*sin(angles[1]);
            z = magnitude*sin(angles[0]);
        }
    }

    public Vector3D(double x, double y, double z) {
        this(x, y, z, CoordinateType.CARTESIAN, AngleUnit.RADIANS);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
