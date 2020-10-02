package com.SCHSRobotics.HAL9001.system.robot.subsystems;

import com.SCHSRobotics.HAL9001.system.robot.Robot;

import org.jetbrains.annotations.NotNull;

public abstract class NonHolomnicDriveTrain extends DriveTrain {
    public NonHolomnicDriveTrain(@NotNull Robot robot, @NotNull Params params) {
        super(robot, params);
    }

}
