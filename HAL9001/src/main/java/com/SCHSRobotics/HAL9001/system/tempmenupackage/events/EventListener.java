package com.SCHSRobotics.HAL9001.system.tempmenupackage.events;

import com.SCHSRobotics.HAL9001.system.tempmenupackage.ViewElement;

public interface EventListener extends ViewElement {
    //returns true if it should force-trigger a cursor update, false otherwise.
    boolean onEvent(Event event);
}
