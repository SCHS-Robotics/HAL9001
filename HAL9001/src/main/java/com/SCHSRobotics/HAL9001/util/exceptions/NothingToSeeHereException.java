/*
 * Filename: NothingToSeeHereException.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 10/9/19
 */

package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown if an object is null that is not supposed to be null.
 */
public class NothingToSeeHereException extends RuntimeException {

    /**
     * Constructor for NothingToSeeHereException.
     *
     * @param message The message to print to the screen.
     */
    public NothingToSeeHereException(String message) {
        super(message);
    }
}
