/*
 * Filename: WrongDrivetypeException.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/31/19
 */

package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception that is thrown when the drivetype does not meet the criteria needed to perform a specific function.
 */
public class WrongDrivetypeException extends RuntimeException {

    /**
     * Constructor for WrongDriveTypeException.
     *
     * @param message The error message to print to the screen.
     */
    public WrongDrivetypeException(String message) {
        super(message);
    }
}
