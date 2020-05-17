package com.SCHSRobotics.HAL9001.system.tempmenupackage;

public class BlinkEvent extends Event {
    private HALMenu.BlinkState blinkState;
    public BlinkEvent(int priority, HALMenu.BlinkState blinkState) {
        super(priority);
        this.blinkState = blinkState;
    }

    public HALMenu.BlinkState getBlinkState() {
        return blinkState;
    }
}
