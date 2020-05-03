package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

public class ExampleMenu extends HALMenu {
    @Override
    protected void init(Payload payload) {
        addItem(new ListViewButton("# | Fun Times")
                .onClick(new Button<>(1, Button.BooleanInputs.a), () -> {
                    gui.inflate(new ExampleMenu2());
                })
        );
        addItem(new TextElement("# | LOOK! its new text!"));
        addItem(new EntireViewButton()
                .onClick(new Button<>(1, Button.BooleanInputs.b), () -> {
                    gui.inflate(new ExampleMenu3());
                }));
    }
}
