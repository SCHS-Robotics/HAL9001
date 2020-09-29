package com.SCHSRobotics.HAL9001.util.math.units;

public enum TimeUnit {
    NANOSECONDS(1), MILLISECONDS(1e6), SECONDS(MILLISECONDS.nanoConversionFactor * 1000);
    private double nanoConversionFactor;
    TimeUnit(double nanoConversionFactor) {
        this.nanoConversionFactor = nanoConversionFactor;
    }
    public static double convert(double timeIn, TimeUnit fromUnit, TimeUnit toUnit) {
        double timeInNanos = timeIn * fromUnit.nanoConversionFactor;
        return timeInNanos/toUnit.nanoConversionFactor;
    }

    public static double convert(long timeIn, TimeUnit fromUnit, TimeUnit toUnit) {
        return convert((double) timeIn, fromUnit, toUnit);
    }
}
