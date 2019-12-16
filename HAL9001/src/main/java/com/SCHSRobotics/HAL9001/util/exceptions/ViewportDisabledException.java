package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown if a channel in a color image does not exist.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 11/9/19
 */
public class ViewportDisabledException extends RuntimeException {

    /**
     * Constructor for ViewportDisabledException.
     *
     * @param message The message to print to the screen.
     */
    public ViewportDisabledException(String message) {
        super(message);
    }
}
