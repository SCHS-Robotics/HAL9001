package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown if an invalid link is used in the LinkTo annotation.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 12/19/19
 */
public class InvalidLinkException extends RuntimeException {

    /**
     * Constructor for InvalidLinkException.
     *
     * @param message The error message to print to the screen.
     */
    public InvalidLinkException(String message) {
        super(message);
    }
}
