package com.SCHSRobotics.HAL9001.util.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * An exception thrown if something really bad happens.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/29/19
 */
public class DumpsterFireException extends RuntimeException {

    /**
     * Constructor for DumpsterFireException.
     *
     * @param message The error message to print to the screen.
     */
    public DumpsterFireException(@Nullable String message) {
        super(message);
    }
}
