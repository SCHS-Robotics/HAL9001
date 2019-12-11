/*
 * Filename: ViewportDisabledException.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 11/9/19
 */

package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown if a channel in a color image does not exist.
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
