package com.SCHSRobotics.HAL9001.system.tempmenupackage;

public interface EventListener extends ViewElement {
    //returns true if it should force-trigger a cursor update, false otherwise.
    boolean onEvent(Event event);
}
