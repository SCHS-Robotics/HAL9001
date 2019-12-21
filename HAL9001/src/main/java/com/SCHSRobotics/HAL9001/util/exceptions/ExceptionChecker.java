package com.SCHSRobotics.HAL9001.util.exceptions;

import org.jetbrains.annotations.Contract;

/**
 * An class used to easily throw exceptions when a condition is teue.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.6
 * @version 1.0.0
 *
 * Creation Date: 12/20/19
 */
public class ExceptionChecker {
    private ExceptionChecker() {}

    /**
     * Throws an exception if a given condition is not true.
     *
     * @param condition The condition that must be false to throw the exception.
     * @param exception The exception to throw if the condition is false.
     */
    @Contract("false, _ -> fail")
    public static void assertTrue(boolean condition, RuntimeException exception) {
        if(!condition) {
            throw exception;
        }
    }

    @Contract("true, _ -> fail")
    public static void assertFalse(boolean condition, RuntimeException exception) {
        assertTrue(!condition, exception);
    }

    @Contract("null, _ -> fail")
    public static void assertNonNull(Object object, RuntimeException exception) {
        assertFalse(object == null, exception);
    }

    @Contract("!null, _ -> fail")
    public static void assertNull(Object object, RuntimeException exception) {
        assertTrue(object == null, exception);
    }
}
