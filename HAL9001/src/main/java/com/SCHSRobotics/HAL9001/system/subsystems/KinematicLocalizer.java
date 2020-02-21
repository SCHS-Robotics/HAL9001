package com.SCHSRobotics.HAL9001.system.subsystems;

import com.SCHSRobotics.HAL9001.util.math.Vector3D;

public abstract class KinematicLocalizer {
    //TODO make this Pose2d instead of vector3d when you integrate w/ roadrunner
    public abstract Vector3D getPosition();
    public abstract double getVelocity();
    public abstract Vector3D wheelToRobotVelocity(double... wheelVelocities);
    public abstract double[] robotToWheelVelocity(Vector3D robotVelocity);
    public abstract Vector3D wheelToRobotPoseChange(double... wheelDeltas);
    public abstract double[] robotToWheelPoseChange(Vector3D robotPosChange);
}
