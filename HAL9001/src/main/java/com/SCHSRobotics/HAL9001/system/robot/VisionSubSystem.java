package com.SCHSRobotics.HAL9001.system.robot;

import org.opencv.core.Mat;

/**
 * An abstract class for writing vision-based subsystems.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.5
 * @version 1.0.0
 *
 * Creation Date: 11/6/19
 */
public abstract class VisionSubSystem extends SubSystem {
    //The global Id for all vision subsystems (determines Id of next created vision subsystem).
    private static int globalId = 0;
    //The subsystem's priority for being shown on the viewport.
    private int priority;
    //Whether or not the vision is currently enabled.
    private boolean enabled;

    /**
     * A constructor for VisionSubSystem.
     *
     * @param robot The robot running the program.
     */
    public VisionSubSystem(Robot robot) {
        super(robot);
        priority = globalId;
        globalId++;

        enabled = false;
    }

    /**
     * Gets the subsystem's priority.
     *
     * @return The subsystem's priority.
     */
    public final int getPriority() {
        return priority;
    }

    /**
     * Gets whether the subsystem's vision system is enabled.
     *
     * @return Whether the subsystem's vision system is enabled.
     */
    public final boolean isEnabled() {
        return enabled;
    }

    /**
     * Enabled the subsystem's vision system.
     */
    public final void startVision() {
        enabled = true;
    }

    /**
     * Disables the subsystem's vision system.
     */
    public final void stopVision() {
        enabled = false;
    }

    /**
     * An abstract function that runs every camera frame while vision is enabled.
     *
     * @param input The input camera frame.
     * @return The output camera frame (must be same size as input frame).
     */
    public abstract Mat onCameraFrame(Mat input);

    /**
     * An abstract function that runs whenever the viewport is tapped and this vision code is being displayed.
     */
    public void onViewportTapped() {}
}
