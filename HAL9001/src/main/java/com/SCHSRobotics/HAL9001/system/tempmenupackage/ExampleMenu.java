package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

public class ExampleMenu extends ListViewMenu {
    @Override
    protected void init(Payload payload) {
        //selectionZone = new SelectionZone(1,2);
        addItem(new ListViewButton("# | Fun Times")
                .onClick(new Button<>(1, Button.BooleanInputs.x), () -> {
                    gui.inflate(new ExampleMenu2());
                })
        );
        addItem(new TextElement("# | LOOK! its new text!"));
        addItem(new ListViewButton("# | More fun stuff")
                .onClick(new Button<>(1, Button.BooleanInputs.a), () -> {
                    gui.inflate(new ExampleMenu2());
                }));
        addItem(new EntireViewButton()
                .onClick(new Button<>(1, Button.BooleanInputs.b), () -> {
                    gui.inflate(new ExampleMenu3());
                }));
        addItem(new TextElement("# | LOOK! its new text!"));
        addItem(new TextElement("# | LOOK! its new text!"));
        addItem(new TextElement("# | LOOK! its new text!"));
        addItem(new EntireViewButton()
                 .onClick(new Button<>(1, Button.BooleanInputs.y), () -> {
                    gui.forward();
                 }));
        addItem(new TextElement("# | LOOK! its new text!"));
        addItem(new TextElement("# | LOOK! its new text!"));
        addItem(new TextElement("# | LOOK! its new text!"));
        addItem(new TextElement("# | LOOK! its new text!"));
        addItem(new TextElement("# | You found the end of the page!"));

    }
}
