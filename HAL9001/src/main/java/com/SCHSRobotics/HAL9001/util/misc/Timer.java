package com.SCHSRobotics.HAL9001.util.misc;

import com.SCHSRobotics.HAL9001.util.math.units.HALTimeUnit;

public class Timer {
    private long startTime;
    private double duration;

    public Timer() {
        startTime = 0;
        duration = 0;
    }

    public void start() {
        duration = 0;
        startTime = System.nanoTime();
    }

    public double getTimePassed(HALTimeUnit timeUnit) {
        long timeNanos = System.nanoTime() - startTime;
        return HALTimeUnit.convert(timeNanos, HALTimeUnit.NANOSECONDS, timeUnit);
    }

    public void start(double duration, HALTimeUnit timeUnit) {
        this.duration = HALTimeUnit.convert(duration, timeUnit, HALTimeUnit.NANOSECONDS);
        startTime = System.nanoTime();
    }

    public void start(long duration, HALTimeUnit timeUnit) {
        start((double) duration, timeUnit);
    }

    public void reset() {
        startTime = System.nanoTime();
    }

    public boolean requiredTimeElapsed() {
        return getTimePassed(HALTimeUnit.NANOSECONDS) > duration;
    }
}
