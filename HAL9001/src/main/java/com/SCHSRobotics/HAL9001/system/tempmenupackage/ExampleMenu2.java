package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

public class ExampleMenu2 extends HALMenu {

    private TextInput textEntry = new TextInput("# | ", TextInput.CharSet.ALPHANUMERIC, 3,
            new Button<>(1, Button.BooleanInputs.a),
            new Button<>(1, Button.BooleanInputs.b));
    @Override
    protected void init(Payload payload) {
        setSelectionZone(2,2);
        addItem(new ListViewButton("## | Sorry Mario, the princess is in another castle.")
                    .onClick(new Button<>(1, Button.BooleanInputs.y), () -> {
                        gui.back();
                    })
                    .onClick(new Button<>(1, Button.BooleanInputs.a), () -> {
                        gui.inflate(new ExampleMenu3());
                    }));

        addItem(new TextElement("#"));
    }
}
