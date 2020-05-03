package com.SCHSRobotics.HAL9001.system.tempmenupackage;

public class ExampleMenu2 extends HALMenu {
    @Override
    protected void init(Payload payload) {
        addItem(new TextElement("Sorry Mario, the princess is in another castle."));
    }
}
