package com.SCHSRobotics.HAL9001.util.math;

import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A class for doing mathematical operations on arrays. Basically numpy, but less good.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 10/19/20.
 */
@SuppressWarnings("unused")
public class FakeNumpy {

    /**
     * Private default constructor to make class basically static.
     */
    private FakeNumpy() {}

    /**
     * Finds the maximum value of an array.
     *
     * @param array The input array.
     * @param <T> The element type of the array.
     * @return The maximum of the array.
     */
    @Nullable
    public static <T extends Comparable<? super T>> T max(@NotNull T[] array) {
        if(array.length == 0) {
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
    private static <T extends Comparable<? super T>> T max(@NotNull T[] array, int n) {
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
    public static double max(@NotNull double[] array) {
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
    private static double max(@NotNull double[] array, int n) {
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
    public static int max(@NotNull int[] array) {
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
    private static int max(@NotNull int[] array, int n) {
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
    public static float max(@NotNull float[] array) {
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
    private static float max(@NotNull float[] array, int n) {
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
    public static long max(@NotNull long[] array) {
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
    private static long max(@NotNull long[] array, int n) {
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
    public static short max(@NotNull short[] array) {
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
    private static short max(@NotNull short[] array, int n) {
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
    @Nullable
    public static <T extends Comparable<? super T>> T min(@NotNull T[] array) {
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
    private static <T extends Comparable<? super T>> T min(@NotNull T[] array, int n) {
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
    public static double min(@NotNull double[] array) {
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
    private static double min(@NotNull double[] array, int n) {
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
    public static int min(@NotNull int[] array) {
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
    private static int min(@NotNull int[] array, int n) {
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
    public static float min(@NotNull float[] array) {
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
    private static float min(@NotNull float[] array, int n) {
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
    public static long min(@NotNull long[] array) {
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
    private static long min(@NotNull long[] array, int n) {
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
    public static short min(@NotNull short[] array) {
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
    private static short min(@NotNull short[] array, int n) {
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
    public static <T> T[] slice(@NotNull T[] array, int startIdx, int endIdx) {
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
    public static double[] slice(@NotNull double[] array, int startIdx, int endIdx) {
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
    public static int[] slice(@NotNull int[] array, int startIdx, int endIdx) {
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
    public static float[] slice(@NotNull float[] array, int startIdx, int endIdx) {
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
    public static long[] slice(@NotNull long[] array, int startIdx, int endIdx) {
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
    public static short[] slice(@NotNull short[] array, int startIdx, int endIdx) {
        return Arrays.copyOfRange(array, startIdx, endIdx + 1);
    }

    /**
     * Multiplies every element in a double array by a constant double.
     *
     * @param array The input array.
     * @param multiplier The constant to multiply by.
     */
    public static void multiply(@NotNull double[] array, double multiplier) {
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
    public static void multiply(@NotNull int[] array, int multiplier) {
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
    public static void multiply(@NotNull float[] array, float multiplier) {
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
    public static void multiply(@NotNull long[] array, long multiplier) {
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
    public static void multiply(@NotNull int[] array, double multiplier) {
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
    public static void multiply(@NotNull float[] array, double multiplier) {
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
    public static void multiply(@NotNull long[] array, double multiplier) {
        for(int i = 0; i < array.length; i++) {
            array[i] = Math.round(array[i]*multiplier);
        }
    }

    /**
     * Divides every element in a double array by a constant double.
     *
     * @param array The input array.
     * @param multiplier The constant to divide by.
     */
    public static void divide(@NotNull double[] array, double multiplier) {
        ExceptionChecker.assertNotEqual(multiplier,0.0,new ArithmeticException("You can't divide by zero."));
        multiply(array, 1.0/multiplier);
    }

    /**
     * Divides every element in an integer array by a constant integer.
     *
     * @param array The input array.
     * @param multiplier The constant to divide by.
     */
    public static void divide(@NotNull int[] array, int multiplier) {
        ExceptionChecker.assertNotEqual(multiplier,0,new ArithmeticException("You can't divide by zero."));
        multiply(array, 1.0/multiplier);
    }

    /**
     * Divides every element in a float array by a constant float.
     *
     * @param array The input array.
     * @param multiplier The constant to divide by.
     */
    public static void divide(@NotNull float[] array, float multiplier) {
        ExceptionChecker.assertNotEqual(multiplier,0.0f,new ArithmeticException("You can't divide by zero."));
        multiply(array, 1.0/multiplier);
    }

    /**
     * Divides every element in a long array by a constant long.
     *
     * @param array The input array.
     * @param multiplier The constant to divide by.
     */
    public static void divide(@NotNull long[] array, long multiplier) {
        ExceptionChecker.assertNotEqual(multiplier,0L,new ArithmeticException("You can't divide by zero."));
        multiply(array, 1.0/multiplier);
    }

    /**
     * Divides every element in a integer array by a constant double and rounds it to an integer.
     *
     * @param array The input array.
     * @param multiplier The constant to divide by.
     */
    public static void divide(@NotNull int[] array, double multiplier) {
        ExceptionChecker.assertNotEqual(multiplier,0.0,new ArithmeticException("You can't divide by zero."));
        multiply(array, 1.0/multiplier);
    }

    /**
     * Divides every element in a float array by a constant double.
     *
     * @param array The input array.
     * @param multiplier The constant to divide by.
     */
    public static void divide(@NotNull float[] array, double multiplier) {
        ExceptionChecker.assertNotEqual(multiplier,0.0,new ArithmeticException("You can't divide by zero."));
        multiply(array, 1.0/multiplier);
    }

    /**
     * Divides every element in a long array by a constant double.
     *
     * @param array The input array.
     * @param multiplier The constant to divide by.
     */
    public static void divide(@NotNull long[] array, double multiplier) {
        ExceptionChecker.assertNotEqual(multiplier,0.0,new ArithmeticException("You can't divide by zero."));
        multiply(array, 1.0/multiplier);
    }

    /**
     * Takes the absolute value of every element in a double array.
     *
     * @param array The input array.
     * @return The absolute value of the input array.
     */
    public static double[] abs(@NotNull double[] array) {
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
    public static int[] abs(@NotNull int[] array) {
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
    public static float[] abs(@NotNull float[] array) {
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
    public static long[] abs(@NotNull long[] array) {
        long[] output = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            output[i] = Math.abs(array[i]);
        }
        return output;
    }

    public static <T> boolean checkForDuplicates(@NotNull T[] array) {
        Set<T> set = new HashSet<>();
        for(T element : array) {
            if(set.contains(element)) {
                return true;
            }
            set.add(element);
        }
        return false;
    }

    public static <T> T[] removeDuplicates(@NotNull T[] array)
    {
        Set<T> set = new LinkedHashSet<>(Arrays.asList(array));

        List<T> lst = new ArrayList<>(set);

        T[] arrOut = slice(array.clone(),0,lst.size()-1);
        for(int i = 0; i < lst.size(); i++) {
            arrOut[i] = lst.get(i);
        }
        return arrOut;
    }

    public static double mod(double x, double modulus) {
        return (x % modulus + modulus) % modulus;
    }

    public static double[] add(double[] list1, double[] list2) {
        if(list1.length != list2.length) {
            throw new ArithmeticException("Arrays are different sizes, can't be subtracted");
        }

        double[] output = new double[list1.length];
        for(int i = 0; i < list1.length; i++) {
            output[i] = list1[i] + list2[i];
        }
        return output;
    }

    public static int[] add(int[] list1, int[] list2) {
        if(list1.length != list2.length) {
            throw new ArithmeticException("Arrays are different sizes, can't be subtracted");
        }

        int[] output = new int[list1.length];
        for(int i = 0; i < list1.length; i++) {
            output[i] = list1[i] + list2[i];
        }
        return output;
    }

    //list1 - list2
    public static double[] subtract(double[] list1, double[] list2) {
        double[] list2cpy = list2.clone();
        multiply(list2cpy, -1);
        return add(list2cpy, list1);
    }

    public static int[] subtract(int[] list1, int[] list2) {
        int[] list2cpy = list2.clone();
        multiply(list2cpy, -1);
        return add(list2cpy, list1);
    }

    public static double[] absdiff(double[] list1, double[] list2) {
        return abs(subtract(list1,list2));
    }

    public static int[] absdiff(int[] list1, int[] list2) {
        return abs(subtract(list1,list2));
    }

    public static double average(double[] list) {
        double sum = 0;
        for(double d : list) {
            sum += d;
        }
        return sum/list.length;
    }
}