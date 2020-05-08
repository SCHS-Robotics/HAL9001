package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.misc.Toggle;

import java.util.ArrayList;
import java.util.List;

public class ListViewButton extends ViewButton {

    private String text;
    private List<SupplierProgram<String>> textModifyingPrograms;
    private List<Button<Boolean>> textModifyingButtons;
    private List<Toggle> textModifyingToggles;
    private CustomizableGamepad input;

    public ListViewButton(String text) {
        this.text = text;
        textModifyingPrograms = new ArrayList<>();
        textModifyingButtons = new ArrayList<>();
        textModifyingToggles = new ArrayList<>();
        input = new CustomizableGamepad(HALGUI.getInstance().getRobot());
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public ListViewButton onClick(Button<Boolean> button, Program program) {
        return (ListViewButton) super.onClick(button, program);
    }

    public ListViewButton onClick(Button<Boolean> button, SupplierProgram<String> program) {
        textModifyingButtons.add(button);
        textModifyingPrograms.add(program);
        textModifyingToggles.add(new Toggle(Toggle.ToggleTypes.trueOnceToggle, false));
        return this;
    }

    @Override
    public boolean update() {
        boolean anythingUpdated = super.update();
        for (int i = 0; i < textModifyingButtons.size(); i++) {
            Toggle currentToggle = textModifyingToggles.get(i);
            currentToggle.updateToggle(input.getInput(textModifyingButtons.get(i)));
            if(currentToggle.getCurrentState()) {
                text = textModifyingPrograms.get(i).get();
                anythingUpdated = true;
            }
        }
        return anythingUpdated;
    }
}
