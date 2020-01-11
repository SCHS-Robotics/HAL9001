package com.SCHSRobotics.HAL9001.util.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An exception thrown if a channel in a color image does not exist.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/20/19
 */
public class ChannelDoesNotExistException extends RuntimeException {

    /**
     * Constructor for ChannelDoesNotExistException.
     *
     * @param message The message to print to the screen.
     * @param cause The cause of the error.
     */
    public ChannelDoesNotExistException(@Nullable String message, @NotNull Throwable cause) {
        super(message,cause);
    }
}
