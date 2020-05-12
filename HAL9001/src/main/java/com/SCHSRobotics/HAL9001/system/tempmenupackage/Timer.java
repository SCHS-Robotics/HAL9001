package com.SCHSRobotics.HAL9001.system.tempmenupackage;

public class Timer {
    private long startTime;
    private double duration;
    public Timer() {
        startTime = 0;
        duration = -1;
    }

    public void start() {
        duration = -1;
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

    public boolean requiredTimeElapsed() {
        return duration != -1 && getTimePassed(TimeUnit.NANOSECONDS) > duration;
    }
}
