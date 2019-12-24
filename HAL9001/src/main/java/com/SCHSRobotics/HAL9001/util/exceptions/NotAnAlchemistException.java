package com.SCHSRobotics.HAL9001.util.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * An exception that is thrown when things cannot be converted into other things or made out of nothing.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/19/19
 */
public class NotAnAlchemistException extends RuntimeException {

    /**
     * Constructor for NotAnAlchemistException.
     *
     * @param message The error message to print to the screen.
     */
    public NotAnAlchemistException(@Nullable String message) {
        super(message);
    }
}