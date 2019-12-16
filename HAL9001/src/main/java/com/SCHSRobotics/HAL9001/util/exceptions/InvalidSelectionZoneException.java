package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown if the specified command produces or requires an illegal selection zone.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/20/19
 */
public class InvalidSelectionZoneException extends RuntimeException {

    /**
     * Constructor for InvalidSelectionZoneException.
     *
     * @param message The message to print to the screen.
     */
    public InvalidSelectionZoneException(String message) {
        super(message);
    }
}
