/*
 * Filename: WrongSkyscraperBlueprintException.java
 * Author: Dylan Zueck and Cole Savage
 * Team Name: Crow Force, Level Up
 * Date: 7/20/19
 */

package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown when a selection zone is filled with too many or too few lines.
 */
public class WrongSkyscraperBlueprintException extends RuntimeException{

    /**
     * Ctor for WrongSkyscraperBlueprintException.
     *
     * @param message The message to print to the screen.
     */
    public WrongSkyscraperBlueprintException(String message) {super(message);}
}
