/*
 * Filename: ChannelDoesNotExistException.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/20/19
 */

package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown if a channel in a color image does not exist.
 */
public class ChannelDoesNotExistException extends RuntimeException {

    /**
     * Constructor for ChannelDoesNotExistException.
     *
     * @param message The message to print to the screen.
     * @param cause The cause of the error.
     */
    public ChannelDoesNotExistException(String message, Throwable cause) {
        super(message,cause);
    }
}
