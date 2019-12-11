/*
 * Filename: GuiNotPresentException.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 8/31/19
 */

package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown if a GUI is needed, but is not present.
 */
public class GuiNotPresentException extends RuntimeException {

    /**
     * Constructor for GuiNotPresentException
     *
     * @param message The error message to print to the screen.
     */
    public GuiNotPresentException(String message) {
        super(message);
    }
}
