package com.SCHSRobotics.HAL9001.system.tempmenupackage;

public interface ViewListener extends ViewElement {
    boolean update();
    void disable(long timeDisabledMs);
}
