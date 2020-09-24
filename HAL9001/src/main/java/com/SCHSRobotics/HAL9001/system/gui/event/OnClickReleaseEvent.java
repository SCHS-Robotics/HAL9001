package com.SCHSRobotics.HAL9001.system.gui.event;

import com.SCHSRobotics.HAL9001.util.control.Button;

public class OnClickReleaseEvent extends ClickEvent<Button<Boolean>> {
    public OnClickReleaseEvent(int priority, Button<Boolean> button) {
        super(priority, button);
    }
}
