package com.SCHSRobotics.HAL9001.system.tempmenupackage.events;

import com.SCHSRobotics.HAL9001.util.misc.Button;

import java.util.Set;

public class GamepadEventCriteria<T extends ClickEvent> extends EventCriteria<T> {
    private Button<?>[] validButtons;
    public GamepadEventCriteria(Set<Button<?>> validButtons) {
        super((T event) -> validButtons.contains(event.getButton()));
        this.validButtons = (Button<?>[]) validButtons.toArray();
    }

    public Button<?>[] getValidButtons() {
        return validButtons;
    }
}
