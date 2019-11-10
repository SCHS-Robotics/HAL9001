/*
 * Filename: NotARealGamepadException.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/21/19
 */

package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception that is thrown when someone tries to use an invalid gamepad to control the robot.
 */
public class NotARealGamepadException extends RuntimeException {

    /**
     * Constructor for NotARealGamepadException.
     *
     * @param message - The error message to print to the screen.
     */
    public NotARealGamepadException(String message) {
        super(message);
    }
}
