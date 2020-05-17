package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

public class OnClickEvent extends ClickEvent<Button<Boolean>> {
    public OnClickEvent(int priority, Button<Boolean> button) {
        super(priority, button);
    }
}
