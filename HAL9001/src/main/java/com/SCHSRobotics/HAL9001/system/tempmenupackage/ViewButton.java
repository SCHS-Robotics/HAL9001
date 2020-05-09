package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.misc.Toggle;

import java.util.ArrayList;
import java.util.List;

public abstract class ViewButton implements ViewListener {

    private CustomizableGamepad input;
    private List<Program> programs;
    private List<Button<Boolean>> buttons;
    private List<Toggle> toggles;
    private long disableStartTimeMs;
    private long disableDurationMs;

    public ViewButton() {
        input = new CustomizableGamepad(HALGUI.getInstance().getRobot());
        programs = new ArrayList<>();
        buttons = new ArrayList<>();
        toggles = new ArrayList<>();
        disableStartTimeMs = 0;
        disableDurationMs = 0;
    }

    public ViewButton onClick(Button<Boolean> button, Program program) {
        buttons.add(button);
        programs.add(program);
        toggles.add(new Toggle(Toggle.ToggleTypes.trueOnceToggle, false));
        return this;
    }

    @Override
    public boolean update() {
        if(System.currentTimeMillis() - disableStartTimeMs < disableDurationMs) {
            for (int i = 0; i < buttons.size(); i++) {
                Toggle currentToggle = toggles.get(i);
                currentToggle.updateToggle(input.getInput(buttons.get(i)));
            }
            return false;
        }
        boolean anythingUpdated = false;
        for (int i = 0; i < buttons.size(); i++) {
            Toggle currentToggle = toggles.get(i);
            currentToggle.updateToggle(input.getInput(buttons.get(i)));
            if (currentToggle.getCurrentState()) {
                programs.get(i).run();
                anythingUpdated = true;
            }
        }
        return anythingUpdated;
    }

    @Override
    public void disable(long timeDisabledMs) {
        disableStartTimeMs = System.currentTimeMillis();
        disableDurationMs = timeDisabledMs;
    }
}
