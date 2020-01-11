package com.SCHSRobotics.HAL9001.util.math;

import org.jetbrains.annotations.NotNull;

/**
 * An enum representing different common units of distance.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/19/19
 */
public enum Units {
    MEGAMETERS(0.001,"MM"), MILLIMETERS(0.001,"mm"), CENTIMETERS(0.01,"cm"),  METERS(1.0,"m"), INCH(0.0254,"in"), FOOTS(0.3048,"ft"), FEET(0.3048,"ft"), YARDS(0.9144,"yd"), MILES(1609.34,"mi"), TILES(0.6096,"Ti");

    //The number that you multiply by to get meters.
    public double conversionFactor;
    //Their common abbreviation.
    public String abbreviation;

    /**
     * Constructor for Units.
     *
     * @param meterConversion The conversion factor used to convert that unit to meters.
     * @param abbreviation The abbreviation of the unit.
     */
    Units(double meterConversion, @NotNull String abbreviation)
    {
        conversionFactor = meterConversion;
        this.abbreviation = abbreviation;
    }

    /**
     * Converts from one unit of distance to another.
     *
     * @param input The value to convert.
     * @param fromUnit The unit of the value to convert.
     * @param toUnit The unit to convert to.
     * @return The converted value.
     */
    public static double convert(double input, @NotNull Units fromUnit, @NotNull Units toUnit) {
        double meters = input * fromUnit.conversionFactor;
        return  meters/toUnit.conversionFactor;
    }
}
