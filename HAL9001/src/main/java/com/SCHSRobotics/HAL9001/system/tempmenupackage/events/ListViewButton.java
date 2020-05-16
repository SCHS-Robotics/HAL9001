package com.SCHSRobotics.HAL9001.system.tempmenupackage.events;

import com.SCHSRobotics.HAL9001.system.tempmenupackage.Program;
import com.SCHSRobotics.HAL9001.util.misc.Button;

import org.firstinspires.ftc.robotcore.external.function.Function;
import org.firstinspires.ftc.robotcore.external.function.Supplier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@HandlesEvents(events = OnClickEvent.class)
public class ListViewButton implements AdvancedListener {
    private Set<Button<?>> validButtons = new HashSet<>();
    private MultiElementMap<Button<Boolean>, Program> programs;
    private MultiElementMap<Button<Boolean>, Supplier<String>> textSupplierPrograms;
    private MultiElementMap<Button<Boolean>, Function<String, String>> textModifyingPrograms;
    private String text;
    public ListViewButton(String text) {
        this.text = text;
        programs = new MultiElementMap<>();
        textSupplierPrograms = new MultiElementMap<>();
        textModifyingPrograms = new MultiElementMap<>();
    }

    public ListViewButton onClick(Button<Boolean> button, Program program) {
        programs.putElement(button, program);
        validButtons.add(button);
        return this;
    }

    public ListViewButton onClick(Button<Boolean> button, Supplier<String> program) {
        textSupplierPrograms.putElement(button, program);
        validButtons.add(button);
        return this;
    }

    public ListViewButton onClick(Button<Boolean> button, Function<String, String> program) {
        textModifyingPrograms.putElement(button, program);
        validButtons.add(button);
        return this;
    }

    @Override
    public CriteriaPacket getCriteria() {
        GamepadEventCriteria<OnClickEvent> buttonCriteria = new GamepadEventCriteria<>(validButtons);
        CriteriaPacket criteriaPacket = new CriteriaPacket();
        criteriaPacket.add(buttonCriteria);

        return criteriaPacket;
    }

    @Override
    public boolean onEvent(Event eventIn) {
        if(eventIn instanceof OnClickEvent) {
            OnClickEvent event = (OnClickEvent) eventIn;
            Button<?> eventButton = event.getButton();

            //Normal Programs
            List<Program> programsToRun = programs.get(eventButton);
            programsToRun = programsToRun == null ? new ArrayList<>() : programsToRun;

            //Text supplier programs
            List<Supplier<String>> textSupplyingProgramsToRun = textSupplierPrograms.get(eventButton);
            textSupplyingProgramsToRun = textSupplyingProgramsToRun == null ? new ArrayList<>() : textSupplyingProgramsToRun;

            //Text modifier programs.
            List<Function<String, String>> textModifyingProgramsToRun = textModifyingPrograms.get(eventButton);
            textModifyingProgramsToRun = textModifyingProgramsToRun == null ? new ArrayList<>() : textModifyingProgramsToRun;

            //Run normal programs for that button first.
            for(Program program : programsToRun) {
                program.run();
            }

            //Then run text supplier programs.
            for(Supplier<String> program : textSupplyingProgramsToRun) {
                text = program.get();
            }

            //Then run text modifier programs
            for(Function<String, String> program : textModifyingProgramsToRun) {
                text = program.apply(text);
            }
            return true;
        }
        return false;
    }

    @Override
    public String getText() {
        return text;
    }
}
