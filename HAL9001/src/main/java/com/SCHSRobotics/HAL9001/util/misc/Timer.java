package com.SCHSRobotics.HAL9001.util.misc;

import com.SCHSRobotics.HAL9001.util.math.units.TimeUnit;

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

    public double getTimePassed(TimeUnit timeUnit) {
        long timeNanos = System.nanoTime() - startTime;
        return TimeUnit.convert(timeNanos, TimeUnit.NANOSECONDS, timeUnit);
    }

    public void start(double duration, TimeUnit timeUnit) {
        this.duration = TimeUnit.convert(duration, timeUnit, TimeUnit.NANOSECONDS);
        startTime = System.nanoTime();
    }

    public void start(long duration, TimeUnit timeUnit) {
        start((double) duration, timeUnit);
    }

    public void reset() {
        startTime = System.nanoTime();
    }

    public boolean requiredTimeElapsed() {
        return getTimePassed(TimeUnit.NANOSECONDS) > duration;
    }
}
