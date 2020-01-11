package com.SCHSRobotics.HAL9001.util.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * An exception that is thrown when someone tries to use an invalid gamepad to control the robot.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/21/19
 */
public class NotARealGamepadException extends RuntimeException {

    /**
     * Constructor for NotARealGamepadException.
     *
     * @param message The error message to print to the screen.
     */
    public NotARealGamepadException(@Nullable String message) {
        super(message);
    }
}