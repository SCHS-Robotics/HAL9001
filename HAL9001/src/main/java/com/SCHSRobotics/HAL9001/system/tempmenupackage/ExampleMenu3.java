package com.SCHSRobotics.HAL9001.system.tempmenupackage;

public class ExampleMenu3 extends HALMenu {
    @Override
    protected void init(Payload payload) {
        addItem(new TextElement("Hey, you're not supposed to be here!"));
    }
}
