package com.SCHSRobotics.HAL9001.system.gui.viewelement.eventlistener;

import com.SCHSRobotics.HAL9001.system.gui.event.Event;
import com.SCHSRobotics.HAL9001.system.gui.viewelement.ViewElement;

public interface EventListener extends ViewElement {
    //returns true if it should force-trigger a cursor update, false otherwise.
    boolean onEvent(Event event);
}
