/*
 * Filename: TriFunction.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/18/19
 */

package com.SCHSRobotics.HAL9001.util.functional_interfaces;

/**
 * An arbitrary function with 3 inputs and 1 output.
 *
 * @param <T> The datatype of the first input.
 * @param <R> The datatype of the second input.
 * @param <S> The datatype of the third input.
 * @param <Q> The datatype of the output.
 */
public interface TriFunction<T,R,S,Q> {

    Q apply(T arg1, R arg2, S arg3);
}
