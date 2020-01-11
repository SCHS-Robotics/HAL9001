package com.SCHSRobotics.HAL9001.util.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * An exception thrown when a button meant to return boolean data is mapped to a button that does not return boolean data.
 *
 * @author Dylan Zueck, Crow Force
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/20/19
 */
public class NotBooleanInputException extends RuntimeException {

    /**
     * Constructor for NotBooleanInputException.
     *
     * @param message The message to print to the screen.
     */
    public NotBooleanInputException(@Nullable String message) {
        super(message);
    }
}