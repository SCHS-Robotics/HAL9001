package com.SCHSRobotics.HAL9001.system.gui.event.criteria;

import com.SCHSRobotics.HAL9001.system.gui.event.ClickEvent;
import com.SCHSRobotics.HAL9001.util.control.Button;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GamepadEventCriteria<T extends ClickEvent, S extends Button<?>> extends EventCriteria<T> {
    private Set<S> validButtons;
    public GamepadEventCriteria(Set<S> validButtons) {
        super((T event) -> validButtons.contains(event.getButton()));
        this.validButtons = validButtons;
    }

    @SafeVarargs
    public GamepadEventCriteria(S... validButtons) {
        this(new HashSet<>(Arrays.asList(validButtons)));
    }

    public Set<S> getValidButtons() {
        return validButtons;
    }
}
