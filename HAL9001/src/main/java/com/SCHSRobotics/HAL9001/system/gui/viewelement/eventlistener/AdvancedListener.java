package com.SCHSRobotics.HAL9001.system.gui.viewelement.eventlistener;

import com.SCHSRobotics.HAL9001.system.gui.event.criteria.CriteriaPacket;

public interface AdvancedListener extends EventListener {
    CriteriaPacket getCriteria();
}
