package com.SCHSRobotics.HAL9001.util.math;

import static java.lang.Math.round;

public class HALMathUtil {
    private static final double FLOATING_POINT_FIXER_CONSTANT = 1e9;

    private HALMathUtil() {
    }

    public static int mod(int x, int m) {
        return (int) mod((double) x, (double) m);
    }

    public static double mod(double x, int m) {
        return mod(x, (double) m);
    }

    public static double mod(int x, double m) {
        return mod((double) x, m);
    }

    public static double mod(double x, double m) {
        return (x % m + m) % m;
    }

    public static double floatingPointFix(double value) {
        return round(value * FLOATING_POINT_FIXER_CONSTANT) / FLOATING_POINT_FIXER_CONSTANT;
    }
}
