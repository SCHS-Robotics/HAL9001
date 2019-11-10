/*
 * Filename: NotAnAlchemistException.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/19/19
 */

package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception that is thrown when things cannot be converted into other things or made out of nothing.
 */
public class NotAnAlchemistException extends RuntimeException {

    /**
     * Constructor for NotAnAlchemistException.
     *
     * @param message - The error message to print to the screen.
     */
    public NotAnAlchemistException(String message) {
        super(message);
    }
}
