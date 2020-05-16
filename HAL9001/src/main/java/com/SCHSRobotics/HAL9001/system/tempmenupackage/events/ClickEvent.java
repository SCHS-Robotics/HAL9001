package com.SCHSRobotics.HAL9001.system.tempmenupackage.events;

import com.SCHSRobotics.HAL9001.util.misc.Button;

public abstract class ClickEvent extends Event {
    protected Button<?> button;
    public ClickEvent(int priority, Button<?> button) {
        super(priority);
        this.button = button;
    }

    public Button<?> getButton() {
        return button;
    }
}
