package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception that is thrown when a button meant to return vector data is mapped to a button that does not return vector data.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 8/31/19
 */
public class NotVectorInputException extends RuntimeException {

    /**
     * Constructor for NotVectorInputException.
     *
     * @param message The error message to print to the screen.
     */
    public NotVectorInputException(String message) {
        super(message);
    }
}
