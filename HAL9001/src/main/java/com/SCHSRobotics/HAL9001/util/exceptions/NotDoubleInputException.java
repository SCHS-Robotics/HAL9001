/*
 * Filename: NotDoubleInputException.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/20/19
 */

package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown when a button meant to return double data is mapped to a button that does not return double data.
 */
public class NotDoubleInputException extends RuntimeException {

    /**
     * Ctor for NotDoubleInputException.
     *
     * @param message - The message to print to the screen.
     */
    public NotDoubleInputException(String message) {
        super(message);
    }
}
