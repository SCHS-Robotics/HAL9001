package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

public class ExampleMenu2 extends HALMenu {

    @Override
    protected void init(Payload payload) {
        selectionZone = new SelectionZone(2,2);
        addItem(new ViewButton("## | Sorry Mario, the princess is in another castle.")
                    .onClick(new Button<>(1, Button.BooleanInputs.y), (DataPacket packet) -> {
                        gui.back();
                    })
                    .onClick(new Button<>(1, Button.BooleanInputs.a), (DataPacket packet) -> {
                        gui.inflate(new ExampleMenu3());
                    }));

        addItem(new TextElement("#"));
    }
}
