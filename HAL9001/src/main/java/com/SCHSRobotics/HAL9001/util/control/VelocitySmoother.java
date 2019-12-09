/*
 * Filename: VelocitySmoother.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/27/19
 */

package com.SCHSRobotics.HAL9001.util.control;

import com.SCHSRobotics.HAL9001.util.exceptions.NotAnAlchemistException;
import com.SCHSRobotics.HAL9001.util.functional_interfaces.BiFunction;

import org.firstinspires.ftc.robotcore.external.Function;

@Deprecated
//TODO class currently broken
public class VelocitySmoother {

    private double aMax;
    private boolean functionAlreadyInUse;
    private long lastGenerationTime;
    private Function<Double,Double> velocityProfile;
    private Function<Double,Double> accelerationProfile;
    private BiFunction<Double,Double,Double> jerkProfile;

    private double dP,t0,xShift,yShift;

    //private SmoothstepFunction smoother;


    public VelocitySmoother(double accelMax) {
        this.aMax = accelMax;
        this.functionAlreadyInUse = false;
        //.velocityProfile = (Double time)-> (dP*(3*Math.pow((time-xShift)/t0,2)-2*Math.pow((time-xShift)/t0,3))+yShift);
        //this.accelerationProfile = (Double time) -> (((6*dP)/Math.pow(t0,2))*(time-xShift)-((6*dP)/Math.pow(t0,3))*Math.pow(time-xShift,2));
        //this.jerkProfile = (Double shift, Double time) -> ((6*dP)/Math.pow(t0,2) - ((12*dP)/Math.pow(t0,3))*(time-shift));
        this.dP = 0;
        this.t0 = 1;
        this.xShift = 0;
        this.yShift = 0;
    }

    public double getVelocity() {
        return velocityProfile.apply((System.currentTimeMillis()-lastGenerationTime)/1000.0);
    }

    public void update(double current_velocity, double target_velocity) {

        double tk = (System.currentTimeMillis()-lastGenerationTime)/1000.0;

        double currentAcceleration = accelerationProfile.apply(tk);

        double dP = target_velocity-current_velocity;
        double t0 = (3.0*dP)/(2.0*aMax);

        double k1 = 2*tk-t0;
        double k2 = t0*tk - Math.pow(tk,2)-currentAcceleration*(Math.pow(t0,3)/(6*dP));

        double shift1 = (-k1 + Math.sqrt(Math.pow(k1,2)+4*k2))/-2.0;
        double shift2 = (-k1 - Math.sqrt(Math.pow(k1,2)+4*k2))/-2.0;

        double jerk1 = jerkProfile.apply(shift1,tk);
        double jerk2 = jerkProfile.apply(shift2,tk);

        if ((jerk1 < 0  && jerk2 < 0) || jerk1 > 0 && jerk2 > 0) {
            throw new NotAnAlchemistException("Something has gone very wrong :)");
        }
        if(dP < 0) {
            xShift = jerk1 < 0 ? shift1 : shift2;
        }
        else if(dP > 0) {
            xShift = jerk1 > 0 ? shift1 : shift2;
        }
        else {
            xShift = 0;
        }
        this.t0 = t0;
        this.dP = dP;

    }
}
