/*
 * Filename: InvalidSelectionZoneException.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/20/19
 */

package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown if the specified command produces or requires an illegal selection zone.
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
