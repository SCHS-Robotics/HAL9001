package com.SCHSRobotics.HAL9001.system.gui.event;

import com.SCHSRobotics.HAL9001.util.control.Button;

/**
 * An event that is injected whenever a boolean button is released.
 *
 * @author Cole Savage, Level Up
 * @version 1.0.0
 * @see ClickEvent
 * @see Event
 * @see GamepadEventGenerator
 * @see Button
 * <p>
 * Creation Date: 5/17/20
 * @since 1.1.0
 */
public class OnClickReleaseEvent extends ClickEvent<Button<Boolean>> {

    /**
     * The constructor for OnClickReleaseEvent.
     *
     * @param priority The event's priority.
     * @param button   The button being released.
     */
    public OnClickReleaseEvent(int priority, Button<Boolean> button) {
        super(priority, button);
    }
}
