/*
 * Filename: ArrayMath.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 10/19/20
 */

//TODO Javadocs
package com.SCHSRobotics.HAL9001.util.math;

import android.util.Log;

import java.util.Arrays;

public class ArrayMath {

    private ArrayMath() {}

    public static <T extends Comparable<? super T>> T max(T[] array) {
        if(array.length == 0) {
            Log.w("Array Math Warning","Warning! Finding the maximum number of an empty array will return null!");
            return null;
        }
        return max(array,array.length);
    }

    private static <T extends Comparable<? super T>> T max(T[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        T currentMax = max(array, n-1);
        return array[n-1].compareTo(currentMax) > 0 ? array[n-1] : currentMax;
    }

    public static double max(double[] array) {
        if(array.length == 0) {
            return 0;
        }
        return max(array,array.length);
    }

    private static double max(double[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.max(array[n-1], max(array, n-1));
    }

    public static int max(int[] array) {
        if(array.length == 0) {
            return 0;
        }
        return max(array,array.length);
    }

    private static int max(int[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.max(array[n-1], max(array, n-1));
    }

    public static float max(float[] array) {
        if(array.length == 0) {
            return 0;
        }
        return max(array,array.length);
    }

    private static float max(float[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.max(array[n-1], max(array, n-1));
    }

    public static long max(long[] array) {
        if(array.length == 0) {
            return 0;
        }
        return max(array,array.length);
    }

    private static long max(long[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.max(array[n-1], max(array, n-1));
    }

    public static short max(short[] array) {
        if(array.length == 0) {
            return 0;
        }
        return max(array,array.length);
    }

    private static short max(short[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return (short) Math.max(array[n-1], max(array, n-1));
    }

    public static <T extends Comparable<? super T>> T min(T[] array) {
        if(array.length == 0) {
            return null;
        }
        return min(array,array.length);
    }

    private static <T extends Comparable<? super T>> T min(T[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        T currentMin = min(array, n-1);
        return array[n-1].compareTo(currentMin) < 0 ? array[n-1] : currentMin;
    }

    public static double min(double[] array) {
        if(array.length == 0) {
            return 0;
        }
        return min(array,array.length);
    }

    private static double min(double[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.min(array[n-1], min(array, n-1));
    }

    public static int min(int[] array) {
        if(array.length == 0) {
            return 0;
        }
        return min(array,array.length);
    }

    private static int min(int[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.min(array[n-1], min(array, n-1));
    }

    public static float min(float[] array) {
        if(array.length == 0) {
            return 0;
        }
        return min(array,array.length);
    }

    private static float min(float[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.min(array[n-1], min(array, n-1));
    }

    public static long min(long[] array) {
        if(array.length == 0) {
            return 0;
        }
        return min(array,array.length);
    }

    private static long min(long[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return Math.min(array[n-1], min(array, n-1));
    }

    public static short min(short[] array) {
        if(array.length == 0) {
            return 0;
        }
        return min(array,array.length);
    }

    private static short min(short[] array, int n) {
        if(n == 1) {
            return array[0];
        }
        return (short) Math.min(array[n-1], min(array, n-1));
    }

    public static <T> T[] slice(T[] array, int startIdx, int endIdx) {
        return Arrays.copyOfRange(array, startIdx, endIdx + 1);
    }

    public static double[] slice(double[] array, int startIdx, int endIdx) {
        return Arrays.copyOfRange(array, startIdx, endIdx + 1);
    }

    public static int[] slice(int[] array, int startIdx, int endIdx) {
        return Arrays.copyOfRange(array, startIdx, endIdx + 1);
    }

    public static float[] slice(float[] array, int startIdx, int endIdx) {
        return Arrays.copyOfRange(array, startIdx, endIdx + 1);
    }

    public static long[] slice(long[] array, int startIdx, int endIdx) {
        return Arrays.copyOfRange(array, startIdx, endIdx + 1);
    }

    public static short[] slice(short[] array, int startIdx, int endIdx) {
        return Arrays.copyOfRange(array, startIdx, endIdx + 1);
    }

    public static void multiply(double[] array, double multiplier) {
        for(int i = 0; i < array.length; i++) {
            array[i] = array[i]*multiplier;
        }
    }

    public static void multiply(int[] array, int multiplier) {
        for(int i = 0; i < array.length; i++) {
            array[i] = array[i]*multiplier;
        }
    }

    public static void multiply(float[] array, float multiplier) {
        for(int i = 0; i < array.length; i++) {
            array[i] = array[i]*multiplier;
        }
    }

    public static void multiply(long[] array, long multiplier) {
        for(int i = 0; i < array.length; i++) {
            array[i] = array[i]*multiplier;
        }
    }

    public static void multiply(int[] array, double multiplier) {
        for(int i = 0; i < array.length; i++) {
            array[i] = (int) Math.round(array[i]*multiplier);
        }
    }

    public static void multiply(float[] array, double multiplier) {
        for(int i = 0; i < array.length; i++) {
            array[i] = array[i]*(float) multiplier;
        }
    }

    public static void multiply(long[] array, double multiplier) {
        for(int i = 0; i < array.length; i++) {
            array[i] = array[i]*(long) multiplier;
        }
    }

    public static void divide(double[] array, double multiplier) {
        multiply(array, 1.0/multiplier);
    }

    public static void divide(int[] array, int multiplier) {
        multiply(array, 1.0/multiplier);
    }

    public static void divide(float[] array, float multiplier) {
        multiply(array, 1.0/multiplier);
    }

    public static void divide(long[] array, long multiplier) {
        multiply(array, 1.0/multiplier);
    }

    public static void divide(int[] array, double multiplier) {
        multiply(array, 1.0/multiplier);
    }

    public static void divide(float[] array, double multiplier) {
        multiply(array, 1.0/multiplier);
    }

    public static void divide(long[] array, double multiplier) {
        multiply(array, 1.0/multiplier);
    }

    public static double[] abs(double[] array) {
        double[] output = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            output[i] = Math.abs(array[i]);
        }
        return output;
    }

    public static int[] abs(int[] array) {
        int[] output = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            output[i] = Math.abs(array[i]);
        }
        return output;
    }

    public static float[] abs(float[] array) {
        float[] output = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            output[i] = Math.abs(array[i]);
        }
        return output;
    }

    public static long[] abs(long[] array) {
        long[] output = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            output[i] = Math.abs(array[i]);
        }
        return output;
    }
}