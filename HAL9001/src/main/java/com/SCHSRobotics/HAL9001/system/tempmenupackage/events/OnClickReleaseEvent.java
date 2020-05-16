package com.SCHSRobotics.HAL9001.system.tempmenupackage.events;

import com.SCHSRobotics.HAL9001.util.misc.Button;

public class OnClickReleaseEvent extends OnClickEvent {
    public OnClickReleaseEvent(int priority, Button<Boolean> button) {
        super(priority, button);
    }
}
