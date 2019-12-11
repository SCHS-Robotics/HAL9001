/*
 * Filename: SkyscraperTooTallException.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 8/20/19
 */

package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown when a selection zone is filled with too many or too few lines.
 */
public class SkyscraperTooTallException extends RuntimeException{

    /**
     * Ctor for SkyscraperTooTallException.
     *
     * @param message The message to print to the screen.
     */
    public SkyscraperTooTallException(String message) {super(message);}
}
