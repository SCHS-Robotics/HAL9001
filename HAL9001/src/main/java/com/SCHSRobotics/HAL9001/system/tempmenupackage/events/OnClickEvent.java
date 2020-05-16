package com.SCHSRobotics.HAL9001.system.tempmenupackage.events;


import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.misc.Toggle;

public class OnClickEvent extends ClickEvent {

    private Toggle toggle;
    private CustomizableGamepad gamepad;
    private ClickMode clickMode;
    public enum ClickMode {
        SINGLE_CLICK, WHILE_CLICKED
    }
    public OnClickEvent(int priority, Button<Boolean> button) {
        super(priority, button);
    }
}
