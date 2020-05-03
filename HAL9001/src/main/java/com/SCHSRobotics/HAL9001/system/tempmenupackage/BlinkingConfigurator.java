package com.SCHSRobotics.HAL9001.system.tempmenupackage;

/**
 * An interface for view elements that can modify the behavior of the cursor.
 *
 * @author Cole Savage, Level Up
 * @since 1.1.0
 * @version 1.1.0
 *
 * Creation Date: 4/29/20
 */
public interface BlinkingConfigurator {

    /**
     * Returns whether the view element can request that the cursor not blink when an update is triggered.
     *
     * @return Whether the view element can request that the cursor not blink when an update is triggered.
     */
    boolean requestNoBlinkOnTriggeredUpdate();
}
