package com.SCHSRobotics.HAL9001.util.math.units;

public enum HALTimeUnit {
    NANOSECONDS(1), MILLISECONDS(1e6), SECONDS(MILLISECONDS.nanoConversionFactor * 1000);
    private double nanoConversionFactor;

    HALTimeUnit(double nanoConversionFactor) {
        this.nanoConversionFactor = nanoConversionFactor;
    }

    public static double convert(double timeIn, HALTimeUnit fromUnit, HALTimeUnit toUnit) {
        double timeInNanos = timeIn * fromUnit.nanoConversionFactor;
        return timeInNanos / toUnit.nanoConversionFactor;
    }

    public static double convert(long timeIn, HALTimeUnit fromUnit, HALTimeUnit toUnit) {
        return convert((double) timeIn, fromUnit, toUnit);
    }
}
