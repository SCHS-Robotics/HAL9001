package com.SCHSRobotics.HAL9001.system.gui.event;

/**
 * A functional interface used to allow EventListeners to run pieces of code when they are triggered.
 *
 * @author Cole Savage, Level Up
 * @version 1.0.0
 * @see TaskPacket
 * @see DataPacket
 * @see com.SCHSRobotics.HAL9001.system.gui.viewelement.eventlistener.EventListener
 * @see com.SCHSRobotics.HAL9001.system.gui.HALMenu
 * <p>
 * Creation Date: 5/17/20
 * @since 1.1.0
 */
@FunctionalInterface
public interface Task {

    /**
     * A method that will be overriden with code that the event listener should run.
     *
     * @param dataPacket A datapacket giving the code information about the event and event listener running the task.
     * @see DataPacket
     * @see Event
     * @see com.SCHSRobotics.HAL9001.system.gui.viewelement.eventlistener.EventListener
     */
    void run(DataPacket dataPacket);
}
