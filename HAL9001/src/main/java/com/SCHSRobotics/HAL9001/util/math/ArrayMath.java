package com.SCHSRobotics.HAL9001.util.math;

import android.util.Log;

import java.util.Arrays;

/**
 * A class for doing mathematical operations on arrays. Basically numpy, but less good.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 10/19/20.
 */
public class ArrayMath {

    /**
     * Private default constructor to make class basically static.
     */
    private ArrayMath() {}

    /**
     * Finds the maximum value of an array.
     *
     * @param array The input array.
     * @param <T> The element type of the array.
     * @return The maximum of the array.
     */
    public static <T extends Comparable<? super T>> T max(T[] array) {
        if(array.length == 0) {
            Log.w("Array Math Warning","Warning! Finding the maximum number of an empty array will return null!");
            return null;
        }
        return max(array,array.length);
    }

    /**
     * Finds the maximum value of an array.
     *
     * @param array The input array.
     * @param n The maximum index of the elements in the array that will be compared.
     * @param <T> The element type of the array.
     * @return The maximum of the array.
     */
    private static <T extends Comparable<? super T>> T max(T[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        T currentMax = max(array, n-1);
        return array[n-1].compareTo(currentMax) > 0 ? array[n-1] : currentMax;
    }

    /**
     * Finds the maximum value of an array of doubles.
     *
     * @param array The input array.
     * @return The maximum of the array.
     */
    public static double max(double[] array) {
        if(array.length == 0) {
            return 0;
        }
        return max(array,array.length);
    }

    /**
     * Finds the maximum value of an array of doubles.
     *
     * @param array The input array.
     * @param n The maximum index of the elements in the array that will be compared.
     * @return The maximum of the array.
     */
    private static double max(double[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.max(array[n-1], max(array, n-1));
    }

    /**
     * Finds the maximum value of an array of integers.
     *
     * @param array The input array.
     * @return The maximum of the array.
     */
    public static int max(int[] array) {
        if(array.length == 0) {
            return 0;
        }
        return max(array,array.length);
    }

    /**
     * Finds the maximum value of an array of integers.
     *
     * @param array The input array.
     * @param n The maximum index of the elements in the array that will be compared.
     * @return The maximum of the array.
     */
    private static int max(int[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.max(array[n-1], max(array, n-1));
    }

    /**
     * Finds the maximum value of an array of floats.
     *
     * @param array The input array.
     * @return The maximum of the array.
     */
    public static float max(float[] array) {
        if(array.length == 0) {
            return 0;
        }
        return max(array,array.length);
    }

    /**
     * Finds the maximum value of an array of floats.
     *
     * @param array The input array.
     * @param n The maximum index of the elements in the array that will be compared.
     * @return The maximum of the array.
     */
    private static float max(float[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.max(array[n-1], max(array, n-1));
    }

    /**
     * Finds the maximum value of an array of longs.
     *
     * @param array The input array.
     * @return The maximum of the array.
     */
    public static long max(long[] array) {
        if(array.length == 0) {
            return 0;
        }
        return max(array,array.length);
    }

    /**
     * Finds the maximum value of an array of longs.
     *
     * @param array The input array.
     * @param n The maximum index of the elements in the array that will be compared.
     * @return The maximum of the array.
     */
    private static long max(long[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.max(array[n-1], max(array, n-1));
    }

    /**
     * Finds the maximum value of an array of shorts.
     *
     * @param array The input array.
     * @return The maximum of the array.
     */
    public static short max(short[] array) {
        if(array.length == 0) {
            return 0;
        }
        return max(array,array.length);
    }

    /**
     * Finds the maximum value of an array of shorts.
     *
     * @param array The input array.
     * @param n The maximum index of the elements in the array that will be compared.
     * @return The maximum of the array.
     */
    private static short max(short[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return (short) Math.max(array[n-1], max(array, n-1));
    }

    /**
     * Finds the minimum value of an array.
     *
     * @param array The input array.
     * @param <T> The element type of the array.
     * @return The minimum of the array.
     */
    public static <T extends Comparable<? super T>> T min(T[] array) {
        if(array.length == 0) {
            return null;
        }
        return min(array,array.length);
    }

    /**
     * Finds the minimum value of an array.
     *
     * @param array The input array.
     * @param n The maximum index of the elements in the array that will be compared.
     * @param <T> The element type of the array.
     * @return The minimum of the array.
     */
    private static <T extends Comparable<? super T>> T min(T[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        T currentMin = min(array, n-1);
        return array[n-1].compareTo(currentMin) < 0 ? array[n-1] : currentMin;
    }

    /**
     * Finds the minimum value of an array of doubles.
     *
     * @param array The input array.
     * @return The minimum of the array.
     */
    public static double min(double[] array) {
        if(array.length == 0) {
            return 0;
        }
        return min(array,array.length);
    }

    /**
     * Finds the minimum value of an array of doubles.
     *
     * @param array The input array.
     * @param n The maximum index of the elements in the array that will be compared.
     * @return The minimum of the array.
     */
    private static double min(double[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.min(array[n-1], min(array, n-1));
    }

    /**
     * Finds the minimum value of an array of integers.
     *
     * @param array The input array.
     * @return The minimum of the array.
     */
    public static int min(int[] array) {
        if(array.length == 0) {
            return 0;
        }
        return min(array,array.length);
    }

    /**
     * Finds the minimum value of an array of integers.
     *
     * @param array The input array.
     * @param n The maximum index of the elements in the array that will be compared.
     * @return The minimum of the array.
     */
    private static int min(int[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.min(array[n-1], min(array, n-1));
    }

    /**
     * Finds the minimum value of an array of floats.
     *
     * @param array The input array.
     * @return The minimum of the array.
     */
    public static float min(float[] array) {
        if(array.length == 0) {
            return 0;
        }
        return min(array,array.length);
    }

    /**
     * Finds the minimum value of an array of floats.
     *
     * @param array The input array.
     * @param n The maximum index of the elements in the array that will be compared.
     * @return The minimum of the array.
     */
    private static float min(float[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.min(array[n-1], min(array, n-1));
    }

    /**
     * Finds the minimum value of an array of longs.
     *
     * @param array The input array.
     * @return The minimum of the array.
     */
    public static long min(long[] array) {
        if(array.length == 0) {
            return 0;
        }
        return min(array,array.length);
    }

    /**
     * Finds the minimum value of an array of longs.
     *
     * @param array The input array.
     * @param n The maximum index of the elements in the array that will be compared.
     * @return The minimum of the array.
     */
    private static long min(long[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.min(array[n-1], min(array, n-1));
    }

    /**
     * Finds the minimum value of an array of shorts.
     *
     * @param array The input array.
     * @return The minimum of the array.
     */
    public static short min(short[] array) {
        if(array.length == 0) {
            return 0;
        }
        return min(array,array.length);
    }

    /**
     * Finds the minimum value of an array of shorts.
     *
     * @param array The input array.
     * @param n The maximum index of the elements in the array that will be compared.
     * @return The minimum of the array.
     */
    private static short min(short[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return (short) Math.min(array[n-1], min(array, n-1));
    }

    /**
     * Slices an array from startIdx to endIdx (inclusive).
     *
     * @param array The input array.
     * @param startIdx The starting index for the slice.
     * @param endIdx The ending index for the slice.
     * @param <T> The element type of the array.
     * @return The slice of the array that goes from startIdx to endIdx (inclusive).
     */
    public static <T> T[] slice(T[] array, int startIdx, int endIdx) {
        return Arrays.copyOfRange(array, startIdx, endIdx + 1);
    }

    /**
     * Slices a double array from startIdx to endIdx (inclusive).
     *
     * @param array The input array.
     * @param startIdx The starting index for the slice.
     * @param endIdx The ending index for the slice.
     * @return The slice of the array that goes from startIdx to endIdx (inclusive).
     */
    public static double[] slice(double[] array, int startIdx, int endIdx) {
        return Arrays.copyOfRange(array, startIdx, endIdx + 1);
    }

    /**
     * Slices an integer array from startIdx to endIdx (inclusive).
     *
     * @param array The input array.
     * @param startIdx The starting index for the slice.
     * @param endIdx The ending index for the slice.
     * @return The slice of the array that goes from startIdx to endIdx (inclusive).
     */
    public static int[] slice(int[] array, int startIdx, int endIdx) {
        return Arrays.copyOfRange(array, startIdx, endIdx + 1);
    }

    /**
     * Slices a float array from startIdx to endIdx (inclusive).
     *
     * @param array The input array.
     * @param startIdx The starting index for the slice.
     * @param endIdx The ending index for the slice.
     * @return The slice of the array that goes from startIdx to endIdx (inclusive).
     */
    public static float[] slice(float[] array, int startIdx, int endIdx) {
        return Arrays.copyOfRange(array, startIdx, endIdx + 1);
    }

    /**
     * Slices a long array from startIdx to endIdx (inclusive).
     *
     * @param array The input array.
     * @param startIdx The starting index for the slice.
     * @param endIdx The ending index for the slice.
     * @return The slice of the array that goes from startIdx to endIdx (inclusive).
     */
    public static long[] slice(long[] array, int startIdx, int endIdx) {
        return Arrays.copyOfRange(array, startIdx, endIdx + 1);
    }

    /**
     * Slices a short array from startIdx to endIdx (inclusive).
     *
     * @param array The input array.
     * @param startIdx The starting index for the slice.
     * @param endIdx The ending index for the slice.
     * @return The slice of the array that goes from startIdx to endIdx (inclusive).
     */
    public static short[] slice(short[] array, int startIdx, int endIdx) {
        return Arrays.copyOfRange(array, startIdx, endIdx + 1);
    }

    /**
     * Multiplies every element in a double array by a constant double.
     *
     * @param array The input array.
     * @param multiplier The constant to multiply by.
     */
    public static void multiply(double[] array, double multiplier) {
        for(int i = 0; i < array.length; i++) {
            array[i] = array[i]*multiplier;
        }
    }

    /**
     * Multiplies every element in an integer array by a constant integer.
     *
     * @param array The input array.
     * @param multiplier The constant to multiply by.
     */
    public static void multiply(int[] array, int multiplier) {
        for(int i = 0; i < array.length; i++) {
            array[i] = array[i]*multiplier;
        }
    }

    /**
     * Multiplies every element in a float array by a constant float.
     *
     * @param array The input array.
     * @param multiplier The constant to multiply by.
     */
    public static void multiply(float[] array, float multiplier) {
        for(int i = 0; i < array.length; i++) {
            array[i] = array[i]*multiplier;
        }
    }

    /**
     * Multiplies every element in a long array by a constant long.
     *
     * @param array The input array.
     * @param multiplier The constant to multiply by.
     */
    public static void multiply(long[] array, long multiplier) {
        for(int i = 0; i < array.length; i++) {
            array[i] = array[i]*multiplier;
        }
    }

    /**
     * Multiplies every element in an integer array by a constant double and rounds it to an integer.
     *
     * @param array The input array.
     * @param multiplier The constant to multiply by.
     */
    public static void multiply(int[] array, double multiplier) {
        for(int i = 0; i < array.length; i++) {
            array[i] = (int) Math.round(array[i]*multiplier);
        }
    }

    /**
     * Multiplies every element in a float array by a constant double.
     *
     * @param array The input array.
     * @param multiplier The constant to multiply by.
     */
    public static void multiply(float[] array, double multiplier) {
        for(int i = 0; i < array.length; i++) {
            array[i] = array[i]*(float) multiplier;
        }
    }

    /**
     * Multiplies every element in a long array by a constant double.
     *
     * @param array The input array.
     * @param multiplier The constant to multiply by.
     */
    public static void multiply(long[] array, double multiplier) {
        for(int i = 0; i < array.length; i++) {
            array[i] = array[i]*(long) multiplier;
        }
    }

    /**
     * Divides every element in a double array by a constant double.
     *
     * @param array The input array.
     * @param multiplier The constant to divide by.
     */
    public static void divide(double[] array, double multiplier) {
        multiply(array, 1.0/multiplier);
    }

    /**
     * Divides every element in an integer array by a constant integer.
     *
     * @param array The input array.
     * @param multiplier The constant to divide by.
     */
    public static void divide(int[] array, int multiplier) {
        multiply(array, 1.0/multiplier);
    }

    /**
     * Divides every element in a float array by a constant float.
     *
     * @param array The input array.
     * @param multiplier The constant to divide by.
     */
    public static void divide(float[] array, float multiplier) {
        multiply(array, 1.0/multiplier);
    }

    /**
     * Divides every element in a long array by a constant long.
     *
     * @param array The input array.
     * @param multiplier The constant to divide by.
     */
    public static void divide(long[] array, long multiplier) {
        multiply(array, 1.0/multiplier);
    }

    /**
     * Divides every element in a integer array by a constant double and rounds it to an integer.
     *
     * @param array The input array.
     * @param multiplier The constant to divide by.
     */
    public static void divide(int[] array, double multiplier) {
        multiply(array, 1.0/multiplier);
    }

    /**
     * Divides every element in a float array by a constant double.
     *
     * @param array The input array.
     * @param multiplier The constant to divide by.
     */
    public static void divide(float[] array, double multiplier) {
        multiply(array, 1.0/multiplier);
    }

    /**
     * Divides every element in a long array by a constant double.
     *
     * @param array The input array.
     * @param multiplier The constant to divide by.
     */
    public static void divide(long[] array, double multiplier) {
        multiply(array, 1.0/multiplier);
    }

    /**
     * Takes the absolute value of every element in a double array.
     *
     * @param array The input array.
     * @return The absolute value of the input array.
     */
    public static double[] abs(double[] array) {
        double[] output = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            output[i] = Math.abs(array[i]);
        }
        return output;
    }

    /**
     * Takes the absolute value of every element in an integer array.
     *
     * @param array The input array.
     * @return The absolute value of the input array.
     */
    public static int[] abs(int[] array) {
        int[] output = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            output[i] = Math.abs(array[i]);
        }
        return output;
    }

    /**
     * Takes the absolute value of every element in a float array.
     *
     * @param array The input array.
     * @return The absolute value of the input array.
     */
    public static float[] abs(float[] array) {
        float[] output = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            output[i] = Math.abs(array[i]);
        }
        return output;
    }

    /**
     * Takes the absolute value of every element in a long array.
     *
     * @param array The input array.
     * @return The absolute value of the input array.
     */
    public static long[] abs(long[] array) {
        long[] output = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            output[i] = Math.abs(array[i]);
        }
        return output;
    }
}