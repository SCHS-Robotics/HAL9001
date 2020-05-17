package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

public abstract class ClickEvent <T extends Button<?>> extends Event {
    protected T button;
    public ClickEvent(int priority, T button) {
        super(priority);
        this.button = button;
    }

    public T getButton() {
        return button;
    }
}
