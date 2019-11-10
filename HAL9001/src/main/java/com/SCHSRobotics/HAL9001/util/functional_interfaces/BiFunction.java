/*
 * Filename: BiFunction.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/18/19
 */

package com.SCHSRobotics.HAL9001.util.functional_interfaces;

/**
 * An arbitrary function with 2 inputs and 1 output.
 *
 * @param <T> - The datatype of the first input.
 * @param <R> - The datatype of the second input.
 * @param <S> - The datatype of the output.
 */
public interface BiFunction<T,R,S> {

    S apply(T arg1, R arg2);
}
