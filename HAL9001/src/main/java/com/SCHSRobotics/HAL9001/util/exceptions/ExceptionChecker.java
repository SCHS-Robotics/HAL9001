package com.SCHSRobotics.HAL9001.util.exceptions;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An class used to easily throw exceptions when a condition is teue.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.6
 * @version 1.0.0
 *
 * Creation Date: 12/20/19
 */
@SuppressWarnings("unused")
public class ExceptionChecker {

    @Contract(pure = true)
    private ExceptionChecker() {}

    /**
     * Throws an exception if a given condition is not true.
     *
     * @param condition The condition that must be false to throw the exception.
     * @param exception The exception to throw if the condition is false.
     */
    @Contract("false, _ -> fail")
    public static void assertTrue(boolean condition, @NotNull RuntimeException exception) {
        if(!condition) {
            throw exception;
        }
    }

    @Contract("true, _ -> fail")
    public static void assertFalse(boolean condition, @NotNull RuntimeException exception) {
        assertTrue(!condition, exception);
    }

    @Contract("null, _ -> fail")
    public static void assertNonNull(@Nullable Object object, @NotNull RuntimeException exception) {
        assertFalse(object == null, exception);
    }

    @Contract("!null, _ -> fail")
    public static void assertNull(@Nullable Object object, @NotNull RuntimeException exception) {
        assertTrue(object == null, exception);
    }

    public static void assertEqual(@NotNull Object obj1, @NotNull Object obj2, @NotNull RuntimeException exception) {
        assertTrue(obj1.equals(obj2), exception);
    }

    public static void assertNotEqual(@NotNull Object obj1, @NotNull Object obj2, @NotNull RuntimeException exception) {
        assertFalse(obj1.equals(obj2), exception);
    }
}