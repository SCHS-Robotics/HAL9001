package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.misc.Toggle;

import org.firstinspires.ftc.robotcore.external.function.Function;
import org.firstinspires.ftc.robotcore.external.function.Supplier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class BaseViewButton implements ButtonListener {
    private static final int MAX_WHILE_PRESS_WAIT_MS = 100;

    private CustomizableGamepad input;
    private List<Button<Boolean>> buttons;
    private Map<Integer, Toggle> toggleLookup;

    private Map<Integer, Program> programs;
    private Map<Integer, Supplier<String>> textSupplierPrograms;
    private Map<Integer, Function<String, String>> textModifyingPrograms;

    private Map<Integer, Program> backgroundPrograms;
    private Map<Integer, Supplier<String>> backgroundTextSupplierPrograms;
    private Map<Integer, Function<String, String>> backgroundTextModifyingPrograms;

    private Map<Integer, Timer> whilePressedTimers;

    private int globalBackgroundOrder;

    private Timer disabledTimer;
    private String text;

    public BaseViewButton(@Nullable String text) {
        this.text = text;
        input = new CustomizableGamepad(HALGUI.getInstance().getRobot());

        buttons = new ArrayList<>();
        toggleLookup = new HashMap<>();

        programs = new HashMap<>();
        textSupplierPrograms = new HashMap<>();
        textModifyingPrograms = new HashMap<>();

        backgroundPrograms = new HashMap<>();
        backgroundTextSupplierPrograms = new HashMap<>();
        backgroundTextModifyingPrograms = new HashMap<>();

        whilePressedTimers = new HashMap<>();

        globalBackgroundOrder = 0;
        disabledTimer = new Timer();
    }

    public BaseViewButton onClick(Button<Boolean> button, Program program) {
        toggleLookup.put(buttons.size(), new Toggle(Toggle.ToggleTypes.trueOnceToggle, false));
        programs.put(buttons.size(), program);
        buttons.add(button);
        return this;
    }

    public BaseViewButton onClick(Button<Boolean> button, Supplier<String> program) {
        toggleLookup.put(buttons.size(), new Toggle(Toggle.ToggleTypes.trueOnceToggle, false));
        textSupplierPrograms.put(buttons.size(), program);
        buttons.add(button);
        return this;
    }

    public BaseViewButton onClick(Button<Boolean> button, Function<String, String> program) {
        toggleLookup.put(buttons.size(), new Toggle(Toggle.ToggleTypes.trueOnceToggle, false));
        textModifyingPrograms.put(buttons.size(), program);
        buttons.add(button);
        return this;
    }

    public BaseViewButton whileClicked(Button<Boolean> button, Program program) {
        programs.put(buttons.size(), program);
        whilePressedTimers.put(buttons.size(), new Timer());
        buttons.add(button);
        return this;
    }

    public BaseViewButton whileClicked(Button<Boolean> button, Supplier<String> program) {
        textSupplierPrograms.put(buttons.size(), program);
        whilePressedTimers.put(buttons.size(), new Timer());
        buttons.add(button);
        return this;
    }

    public BaseViewButton whileClicked(Button<Boolean> button, Function<String, String> program) {
        textModifyingPrograms.put(buttons.size(), program);
        whilePressedTimers.put(buttons.size(), new Timer());
        buttons.add(button);
        return this;
    }

    public BaseViewButton addBackgroundTask(Program program) {
        backgroundPrograms.put(globalBackgroundOrder, program);
        globalBackgroundOrder++;
        return this;
    }

    public BaseViewButton addBackgroundTask(Supplier<String> program) {
        backgroundTextSupplierPrograms.put(globalBackgroundOrder, program);
        globalBackgroundOrder++;
        return this;
    }

    public BaseViewButton addBackgroundTask(Function<String, String> program) {
        backgroundTextModifyingPrograms.put(globalBackgroundOrder, program);
        globalBackgroundOrder++;
        return this;
    }

    @Override
    public final boolean update() {
        if(!disabledTimer.requiredTimeElapsed()) {
            Iterator<Integer> toggleIndicesIterator = toggleLookup.keySet().iterator();
            List<Toggle> toggles = new ArrayList<>(toggleLookup.values());
            for (int i = 0; i < toggles.size(); i++) {
                Toggle currentToggle = toggles.get(i);
                Button<Boolean> button = buttons.get(toggleIndicesIterator.next());
                currentToggle.updateToggle(input.getInput(button));
                currentToggle.getCurrentState();
            }
            return false;
        }
        boolean anythingUpdated = false;
        for (int i = 0; i < buttons.size(); i++) {
            boolean runProgram = false;
            if(toggleLookup.containsKey(i)) {
                Toggle currentToggle = toggleLookup.get(i);
                ExceptionChecker.assertNonNull(currentToggle, new NullPointerException("Toggle was null. This should be impossible."));
                currentToggle.updateToggle(input.getInput(buttons.get(i)));
                runProgram = currentToggle.getCurrentState();
            }
            else if(whilePressedTimers.containsKey(i)) {
                Timer whilePressedTimer = whilePressedTimers.get(i);
                ExceptionChecker.assertNonNull(whilePressedTimer, new NullPointerException("Timer was null, this should be impossible."));
                if(whilePressedTimer.requiredTimeElapsed()) {
                    runProgram = input.getInput(buttons.get(i));
                    whilePressedTimer.start(MAX_WHILE_PRESS_WAIT_MS, TimeUnit.MILLISECONDS);
                }
            }
            if(runProgram && programs.containsKey(i)) {
                Program program = programs.get(i);
                ExceptionChecker.assertNonNull(program, new NullPointerException("Program to run is null."));
                program.run();
            }
            else if(runProgram && textSupplierPrograms.containsKey(i)) {
                Supplier<String> program = textSupplierPrograms.get(i);
                ExceptionChecker.assertNonNull(program, new NullPointerException("Program to run is null."));
                text = program.get();
            }
            else if(runProgram && textModifyingPrograms.containsKey(i)) {
                Function<String, String> program = textModifyingPrograms.get(i);
                ExceptionChecker.assertNonNull(program, new NullPointerException("Program to run is null."));
                text = program.apply(text);
            }

            anythingUpdated |= runProgram;
        }

        if(!anythingUpdated) {
            for (int i = 0; i < globalBackgroundOrder; i++) {
                if(backgroundPrograms.containsKey(i)) {
                    Program program = backgroundPrograms.get(i);
                    ExceptionChecker.assertNonNull(program, new NullPointerException("Program to run is null."));
                    program.run();
                }
                else if(backgroundTextSupplierPrograms.containsKey(i)) {
                    Supplier<String> program = backgroundTextSupplierPrograms.get(i);
                    ExceptionChecker.assertNonNull(program, new NullPointerException("Program to run is null."));
                    text = program.get();
                }
                else if(backgroundTextModifyingPrograms.containsKey(i)) {
                    Function<String, String> program = backgroundTextModifyingPrograms.get(i);
                    ExceptionChecker.assertNonNull(program, new NullPointerException("Program to run is null."));
                    text = program.apply(text);
                }
            }
        }

        return anythingUpdated;
    }

    @Override
    public void disable(long timeDisabledMs) {
        disabledTimer.start(timeDisabledMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public String getText() {
        return text;
    }
}
