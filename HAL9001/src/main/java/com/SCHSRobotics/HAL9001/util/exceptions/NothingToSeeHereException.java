package com.SCHSRobotics.HAL9001.util.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * An exception thrown if an object is null that is not supposed to be null.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 10/9/19
 */
public class NothingToSeeHereException extends RuntimeException {

    /**
     * Constructor for NothingToSeeHereException.
     *
     * @param message The message to print to the screen.
     */
    public NothingToSeeHereException(@Nullable String message) {
        super(message);
    }
}