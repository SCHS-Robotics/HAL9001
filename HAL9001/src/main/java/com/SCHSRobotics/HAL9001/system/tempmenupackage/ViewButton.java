package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.misc.Toggle;

import java.util.LinkedList;

public abstract class ViewButton implements ViewListener {

    private CustomizableGamepad input;
    private LinkedList<Program> programs;
    private LinkedList<Button<Boolean>> buttons;
    private LinkedList<Toggle> toggles;

    public ViewButton() {
        input = new CustomizableGamepad(HALGUI.getInstance().getRobot());
        programs = new LinkedList<>();
        buttons = new LinkedList<>();
        toggles = new LinkedList<>();
    }

    public ViewButton onClick(Button<Boolean> button, Program program) {
        buttons.add(button);
        programs.add(program);
        toggles.add(new Toggle(Toggle.ToggleTypes.trueOnceToggle, false));
        return this;
    }

    @Override
    public boolean update() {
        boolean anythingUpdated = false;
        for (int i = 0; i < buttons.size(); i++) {
            Toggle currentToggle = toggles.get(i);
            currentToggle.updateToggle(input.getInput(buttons.get(i)));
            if(currentToggle.getCurrentState()) {
                programs.get(i).run();
                anythingUpdated = true;
            }
        }
        return anythingUpdated;
    }
}
