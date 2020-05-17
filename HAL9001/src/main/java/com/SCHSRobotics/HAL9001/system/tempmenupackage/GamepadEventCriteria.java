package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GamepadEventCriteria<T extends ClickEvent, S extends Button<?>> extends EventCriteria<T> {
    private S[] validButtons;
    public GamepadEventCriteria(Set<S> validButtons) {
        super((T event) -> validButtons.contains(event.getButton()));
        this.validButtons = (S[]) validButtons.toArray();
    }

    @SafeVarargs
    public GamepadEventCriteria(S... validButtons) {
        this(new HashSet<>(Arrays.asList(validButtons)));
    }

    public Button<?>[] getValidButtons() {
        return validButtons;
    }
}
