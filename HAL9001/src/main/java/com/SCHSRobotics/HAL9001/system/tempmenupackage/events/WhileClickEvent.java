package com.SCHSRobotics.HAL9001.system.tempmenupackage.events;

import com.SCHSRobotics.HAL9001.util.misc.Button;

public class WhileClickEvent extends OnClickEvent {
    public WhileClickEvent(int priority, Button<Boolean> button) {
        super(priority, button);
    }
}
