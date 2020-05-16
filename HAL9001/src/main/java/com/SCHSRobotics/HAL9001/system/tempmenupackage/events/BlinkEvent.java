package com.SCHSRobotics.HAL9001.system.tempmenupackage.events;


public class BlinkEvent extends Event {
    private HALMenuEventBased.BlinkState blinkState;
    public BlinkEvent(int priority, HALMenuEventBased.BlinkState blinkState) {
        super(priority);
        this.blinkState = blinkState;
    }

    public HALMenuEventBased.BlinkState getBlinkState() {
        return blinkState;
    }
}
