package com.SCHSRobotics.HAL9001.system.subsystems;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;

import org.jetbrains.annotations.NotNull;

public abstract class NonHolomnicDriveTrain extends DriveTrain {
    public NonHolomnicDriveTrain(@NotNull Robot robot, @NotNull Params params) {
        super(robot, params);
    }

}
