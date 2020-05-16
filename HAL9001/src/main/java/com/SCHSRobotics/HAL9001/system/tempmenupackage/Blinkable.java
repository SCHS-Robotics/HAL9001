package com.SCHSRobotics.HAL9001.system.tempmenupackage;

public interface Blinkable {
    void onBlinkEvent(HALMenu.BlinkState blinkState);
    void setBlinkEnabled(boolean blinkEnabled);
}
