package com.SCHSRobotics.HAL9001.system.tempmenupackage;

@DynamicSelectionZone(pattern = {true})
public abstract class ListViewMenu extends HALMenu {
    public ListViewMenu(Payload payload) {
        super(payload);
        selectionZone = new SelectionZone(1,1);
    }

    public ListViewMenu() {
        this(new Payload());
    }
}
