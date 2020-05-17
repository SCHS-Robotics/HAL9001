package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

public class OnClickReleaseEvent extends ClickEvent<Button<Boolean>> {
    public OnClickReleaseEvent(int priority, Button<Boolean> button) {
        super(priority, button);
    }
}
