package com.SCHSRobotics.HAL9001.system.gui.event;

import com.SCHSRobotics.HAL9001.util.control.Button;

public class OnClickEvent extends ClickEvent<Button<Boolean>> {
    public OnClickEvent(int priority, Button<Boolean> button) {
        super(priority, button);
    }
}
