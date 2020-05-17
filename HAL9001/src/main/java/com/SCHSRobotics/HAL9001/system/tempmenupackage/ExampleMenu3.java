package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

public class ExampleMenu3 extends HALMenu {
    @Override
    protected void init(Payload payload) {
        addItem(new TextElement("Hey, you're not supposed to be here!"));
        addItem(new EntireViewButton()
            .onClick(new Button<>(1, Button.BooleanInputs.b), (DataPacket packet) -> {
                gui.back();
             }));
    }
}
