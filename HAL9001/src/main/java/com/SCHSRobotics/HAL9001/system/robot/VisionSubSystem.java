package com.SCHSRobotics.HAL9001.system.robot;

import org.jetbrains.annotations.NotNull;

/**
 * An interface for writing vision-based subsystems.
 *
 * @author Cole Savage, Level Up
 * @version 1.0.0
 * <p>
 * Creation Date: 11/6/19
 * @since 1.0.5
 */
public abstract class VisionSubSystem extends SubSystem {
    public VisionSubSystem(@NotNull Robot robot) {
        super(robot);
    }

    protected abstract HALPipeline[] getPipelines();
}
