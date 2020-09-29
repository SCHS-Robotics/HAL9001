package com.SCHSRobotics.HAL9001.system.gui.viewelement.eventlistener;

import com.SCHSRobotics.HAL9001.system.gui.event.criteria.CriteriaPacket;

/**
 * An interface that gives EventListeners the ability to be more selective about which events they handle via CriteriaPackets.
 *
 * @author Cole Savage, Level Up
 * @version 1.0.0
 * @see EventListener
 * @see CriteriaPacket
 * @see com.SCHSRobotics.HAL9001.system.gui.event.criteria.EventCriteria
 * <p>
 * Creation Date: 5/17/20
 * @since 1.1.0
 */
public interface AdvancedListener extends EventListener {

    /**
     * Gets a packet of criteria that events have to satisfy in order to be handled.
     *
     * @return A packet of criteria that events have to satisfy in order to be handled.
     * @see CriteriaPacket
     */
    CriteriaPacket getCriteria();
}
