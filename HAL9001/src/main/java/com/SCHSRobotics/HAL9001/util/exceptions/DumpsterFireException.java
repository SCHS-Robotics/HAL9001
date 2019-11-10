/*
 * Filename: DumpsterFireException.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/29/19
 */

package com.SCHSRobotics.HAL9001.util.exceptions;

/**
 * An exception thrown if something really bad happens.
 */
public class DumpsterFireException extends RuntimeException {

    /**
     * Constructor for DumpsterFireException.
     *
     * @param message - The error message to print to the screen.
     */
    public DumpsterFireException(String message) {
        super(message);
    }
}
