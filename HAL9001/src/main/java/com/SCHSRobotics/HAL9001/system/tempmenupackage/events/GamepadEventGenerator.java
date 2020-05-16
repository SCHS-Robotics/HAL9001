package com.SCHSRobotics.HAL9001.system.tempmenupackage.events;

import com.SCHSRobotics.HAL9001.system.tempmenupackage.HALGUI;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;

import java.util.HashMap;
import java.util.Map;

public class GamepadEventGenerator {
    private static final int DEFAULT_BUTTON_EVENT_PRIORITY = 0;

    private CustomizableGamepad gamepad;
    private Map<Button<Boolean>, Boolean> lastState;
    private Map<Button<Boolean>, Integer> priorities;
    //todo make singleton
    private static GamepadEventGenerator INSTANCE = new GamepadEventGenerator();
    public GamepadEventGenerator() {
        gamepad = new CustomizableGamepad(HALGUI.getInstance().getRobot());
        lastState = new HashMap<>();
        for(Button.BooleanInputs booleanButtonType : Button.BooleanInputs.values()) {
            lastState.put(new Button<>(1, booleanButtonType), false);
            lastState.put(new Button<>(2, booleanButtonType), false);
            priorities.put(new Button<>(1, booleanButtonType), DEFAULT_BUTTON_EVENT_PRIORITY);
            priorities.put(new Button<>(2, booleanButtonType), DEFAULT_BUTTON_EVENT_PRIORITY);
        }
    }

    public void setButtonEventPriority(Button<Boolean> button, int priority) {
        priorities.put(button, priority);
    }

    protected void generateEvents(Button[] validButtons) {
        for(Button<?> button : validButtons) {
            if(button.isBoolean()) {
                Button<Boolean> booleanButton = (Button<Boolean>) button;
                boolean currentValue = gamepad.getInput(booleanButton);
                Boolean lastValueBoxed = lastState.get(booleanButton);
                ExceptionChecker.assertNonNull(lastValueBoxed, new NullPointerException("returned value from gamepad was null, this shouldn't be possible."));
                boolean lastValue = lastValueBoxed;
                Integer boxedPriority = priorities.get(booleanButton);
                ExceptionChecker.assertNonNull(boxedPriority, new NullPointerException("Priority for button was null, this should be impossible."));
                int priority = boxedPriority;
                if (!lastValue && currentValue) {
                    Event.injectEvent(new OnClickEvent(priority, booleanButton));
                }
                if (lastValue && !currentValue) {
                    Event.injectEvent(new OnClickReleaseEvent(priority, booleanButton));
                }
                if (currentValue) {
                    Event.injectEvent(new WhileClickEvent(priority, booleanButton));
                }

                lastState.put(booleanButton, currentValue);
            }
        }
    }
}
