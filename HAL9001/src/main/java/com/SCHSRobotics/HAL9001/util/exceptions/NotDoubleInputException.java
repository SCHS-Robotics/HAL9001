package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown when a button meant to return double data is mapped to a button that does not return double data.
 *
 * @author Dylan Zueck, Crow Force
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/20/19
 */
public class NotDoubleInputException extends RuntimeException {

    /**
     * Constructor for NotDoubleInputException.
     *
     * @param message The message to print to the screen.
     */
    public NotDoubleInputException(String message) {
        super(message);
    }
}
