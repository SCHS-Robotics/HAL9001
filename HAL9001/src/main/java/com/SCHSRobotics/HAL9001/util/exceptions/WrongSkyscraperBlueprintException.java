package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown when a selection zone is filled with too many or too few lines.
 *
 * @author Dylan Zueck, Crow Force
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/20/19
 */
public class WrongSkyscraperBlueprintException extends RuntimeException{

    /**
     * Ctor for WrongSkyscraperBlueprintException.
     *
     * @param message The message to print to the screen.
     */
    public WrongSkyscraperBlueprintException(String message) {super(message);}
}
