package com.SCHSRobotics.HAL9001.util.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * An exception thrown when a selection zone is filled with too many or too few lines.
 *
 * @author Dylan Zueck, Crow Force
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 8/20/19
 */
public class SkyscraperTooTallException extends RuntimeException{

    /**
     * Constructor for SkyscraperTooTallException.
     *
     * @param message The message to print to the screen.
     */
    public SkyscraperTooTallException(@Nullable String message) {super(message);}
}