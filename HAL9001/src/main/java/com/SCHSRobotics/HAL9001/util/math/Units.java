package com.SCHSRobotics.HAL9001.util.math;

/**
 * An enum representing different common units of distance.
 */
public enum Units {
    MEGAMETERS(0.001,"MM"), MILLIMETERS(0.001,"mm"), CENTIMETERS(0.01,"cm"),  METERS(1.0,"m"), INCH(0.0254,"in"), FOOTS(0.3048,"ft"), FEET(0.3048,"ft"), YARDS(0.9144,"yd"), MILES(1609.34,"mi"), TILES(0.6096,"Ti");

    //The number that you multiply by to get meters.
    public double conversionFactor;
    //Their common abreviation.
    public String abreviation;

    /**
     * Constructor for Units.
     *
     * @param meterConversion - The conversion factor used to convert that unit to meters.
     * @param abreviation - The abbreviation of the unit.
     */
    Units(double meterConversion, String abreviation)
    {
        conversionFactor = meterConversion;
        this.abreviation = abreviation;
    }

    /**
     * Converts from one unit of distance to another.
     *
     * @param input - The value to convert.
     * @param fromUnit - The unit of the value to convert.
     * @param toUnit - The unit to convert to.
     * @return
     */
    public static double convert(double input, Units fromUnit, Units toUnit) {
        double meters = input * fromUnit.conversionFactor;
        return  meters/toUnit.conversionFactor;
    }
}
