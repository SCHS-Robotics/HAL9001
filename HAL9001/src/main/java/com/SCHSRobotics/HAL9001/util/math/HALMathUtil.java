package com.SCHSRobotics.HAL9001.util.math;

public class HALMathUtil {
    private HALMathUtil() {}

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
}
