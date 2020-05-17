package com.SCHSRobotics.HAL9001.system.tempmenupackage;

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
    private static GamepadEventGenerator INSTANCE = new GamepadEventGenerator();
    private GamepadEventGenerator() {
        reset();
    }

    public static GamepadEventGenerator getInstance() {
        return INSTANCE;
    }

    public void setButtonEventPriority(Button<Boolean> button, int priority) {
        priorities.put(button, priority);
    }

    protected void generateEvents(Button<?>[] validButtons) {
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

    protected void reset() {
        gamepad = new CustomizableGamepad(HALGUI.getInstance().getRobot());
        lastState = new HashMap<>();
        priorities = new HashMap<>();
        for(Button.BooleanInputs booleanButtonType : Button.BooleanInputs.values()) {
            Button<Boolean> button1 = new Button<>(1, booleanButtonType);
            Button<Boolean> button2 = new Button<>(2, booleanButtonType);

            lastState.put(button1, false);
            lastState.put(button2, false);
            priorities.put(button1, DEFAULT_BUTTON_EVENT_PRIORITY);
            priorities.put(button2, DEFAULT_BUTTON_EVENT_PRIORITY);
        }
    }
}
