package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

/**
 * A button on a view that covers the entire screen.
 *
 * @author Cole Savage, Level Up
 * @since 1.1.0
 * @version 1.1.0
 *
 * Creation Date: 4/30/20
 *
 * @see ViewElement
 * @see ViewListener
 * @see ViewButton
 */
public class EntireViewButton extends ViewButton {
    @Override
    public String getText() {
        return null;
    }

    @Override
    public EntireViewButton onClick(Button<Boolean> button, Program program) {
        return (EntireViewButton) super.onClick(button, program);
    }
}
