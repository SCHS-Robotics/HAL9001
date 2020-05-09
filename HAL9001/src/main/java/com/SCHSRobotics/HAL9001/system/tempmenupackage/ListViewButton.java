package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.functional_interfaces.Function;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.misc.Toggle;

import java.util.ArrayList;
import java.util.List;

public class ListViewButton extends ViewButton {

    private String text;
    private List<SupplierProgram<String>> textSupplyingPrograms;
    private List<Function<String, String>> textModifyingPrograms;
    private List<Button<Boolean>> textModifyingButtons, textSupplyingButtons;
    private List<Toggle> textModifyingToggles, textSupplyingToggles;
    private CustomizableGamepad input;

    public ListViewButton(String text) {
        this.text = text;

        textSupplyingPrograms = new ArrayList<>();
        textSupplyingButtons = new ArrayList<>();
        textSupplyingToggles = new ArrayList<>();

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
        textSupplyingButtons.add(button);
        textSupplyingPrograms.add(program);
        textSupplyingToggles.add(new Toggle(Toggle.ToggleTypes.trueOnceToggle, false));
        return this;
    }

    public ListViewButton onClick(Button<Boolean> button, Function<String, String> program) {
        textModifyingButtons.add(button);
        textModifyingPrograms.add(program);
        textSupplyingToggles.add(new Toggle(Toggle.ToggleTypes.trueOnceToggle, false));
        return this;
    }

    @Override
    public boolean update() {
        boolean anythingUpdated = super.update();
        for (int i = 0; i < textModifyingButtons.size(); i++) {
            Toggle currentToggle = textModifyingToggles.get(i);
            currentToggle.updateToggle(input.getInput(textModifyingButtons.get(i)));
            if(currentToggle.getCurrentState()) {
                text = textModifyingPrograms.get(i).apply(text);
            }
        }
        for (int i = 0; i < textSupplyingButtons.size(); i++) {
            Toggle currentToggle = textSupplyingToggles.get(i);
            currentToggle.updateToggle(input.getInput(textSupplyingButtons.get(i)));
            if(currentToggle.getCurrentState()) {
                text = textSupplyingPrograms.get(i).get();
                anythingUpdated = true;
            }
        }
        return anythingUpdated;
    }
}
