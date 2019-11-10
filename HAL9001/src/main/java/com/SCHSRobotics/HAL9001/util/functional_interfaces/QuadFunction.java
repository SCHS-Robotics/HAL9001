/*
 * Filename: QuadFunction.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 7/18/19
 */

package com.SCHSRobotics.HAL9001.util.functional_interfaces;

/**
 * An arbitrary function with 4 inputs and 1 output.
 *
 * @param <T> - The datatype of the first input.
 * @param <R> - The datatype of the second input.
 * @param <S> - The datatype of the third input.
 * @param <Q> - The datatype of the fourth input.
 * @param <P> - The datatype of the output.
 */
public interface QuadFunction<T,R,S,Q,P> {

    P apply(T arg1, R arg2, S arg3, Q arg4);
}
