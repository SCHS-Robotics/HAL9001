package com.SCHSRobotics.HAL9001.system.gui.event;

import com.SCHSRobotics.HAL9001.util.control.Button;

public class WhileClickEvent extends ClickEvent<Button<Boolean>> {
    public WhileClickEvent(int priority, Button<Boolean> button) {
        super(priority, button);
    }
}
