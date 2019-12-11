/*
 * Filename: InvalidMoveCommandException.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 9/5/19
 */

package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown if a drive train is given an invalid movement command.
 */
public class InvalidMoveCommandException extends RuntimeException {

    /**
     * Constructor for InvalidMoveCommandException.
     *
     * @param message The error message to print to the screen.
     */
    public InvalidMoveCommandException(String message) {
        super(message);
    }
}
