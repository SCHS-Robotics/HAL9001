package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown if a drive train is given an invalid movement command.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 9/5/19
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
