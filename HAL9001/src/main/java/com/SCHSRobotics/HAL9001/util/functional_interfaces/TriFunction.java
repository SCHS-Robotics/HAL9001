package com.SCHSRobotics.HAL9001.util.functional_interfaces;

/**
 * An arbitrary function with 3 inputs and 1 output.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/18/19
 *
 * @param <T> The datatype of the first input.
 * @param <R> The datatype of the second input.
 * @param <S> The datatype of the third input.
 * @param <Q> The datatype of the output.
 */
public interface TriFunction<T,R,S,Q> {

    Q apply(T arg1, R arg2, S arg3);
}
