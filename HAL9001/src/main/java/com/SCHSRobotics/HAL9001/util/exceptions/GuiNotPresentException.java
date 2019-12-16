package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown if a GUI is needed, but is not present.
 *
 * @author Dylan Zueck, Crow Force
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 8/31/19
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
