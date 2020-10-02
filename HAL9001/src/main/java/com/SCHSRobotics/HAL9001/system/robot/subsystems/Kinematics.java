package com.SCHSRobotics.HAL9001.system.robot.subsystems;

public abstract class Kinematics {
    public abstract double[] wheelToRobotVelocity(double... wheelVelocities);
    public abstract double[] robotToWheelVelocity(double... robotVelocity);
    public abstract double[] wheelToRobotPoseChange(double... wheelDeltas);
    public abstract double[] robotToWheelPoseChange(double... robotPosChange);
}
