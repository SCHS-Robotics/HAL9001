package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception that is thrown when the drivetype does not meet the criteria needed to perform a specific function.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 8/31/19
 */
public class WrongDrivetypeException extends RuntimeException {

    /**
     * Constructor for WrongDriveTypeException.
     *
     * @param message The error message to print to the screen.
     */
    public WrongDrivetypeException(String message) {
        super(message);
    }
}
