/*
 * Filename: VisionSubSystem.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 11/6/19
 */

package com.SCHSRobotics.HAL9001.system.source.BaseRobot;

import org.opencv.core.Mat;

public abstract class VisionSubsystem extends SubSystem {

    private static int globalId = 0;
    private int priority;
    private boolean enabled;

    public VisionSubsystem(Robot robot) {
        super(robot);
        priority = globalId;
        globalId++;

        enabled = false;
    }

    public final int getPriority() {
        return priority;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void startVision() {
        enabled = true;
    }

    public final void stopVision() {
        enabled = false;
    }

    public abstract Mat onCameraFrame(Mat input);

    public void onViewportTapped() {}
}
