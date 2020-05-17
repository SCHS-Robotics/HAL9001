package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

public class WhileClickEvent extends ClickEvent<Button<Boolean>> {
    public WhileClickEvent(int priority, Button<Boolean> button) {
        super(priority, button);
    }
}
