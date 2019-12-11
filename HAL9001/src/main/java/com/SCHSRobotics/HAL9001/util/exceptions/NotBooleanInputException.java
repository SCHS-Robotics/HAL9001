/*
 * Filename: NotBooleanInputException.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/20/19
 */

package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown when a button meant to return boolean data is mapped to a button that does not return boolean data.
 */
public class NotBooleanInputException extends RuntimeException {

    /**
     * Ctor for NotBooleanInputException.
     *
     * @param message The message to print to the screen.
     */
    public NotBooleanInputException(String message) {
        super(message);
    }
}
