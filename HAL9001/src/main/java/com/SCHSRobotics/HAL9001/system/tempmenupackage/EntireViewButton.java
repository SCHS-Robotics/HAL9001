package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

import org.firstinspires.ftc.robotcore.external.function.Function;
import org.firstinspires.ftc.robotcore.external.function.Supplier;

/**
 * A button on a view that covers the entire screen.
 *
 * @author Cole Savage, Level Up
 * @since 1.1.0
 * @version 1.1.0
 *
 * Creation Date: 4/30/20
 *
 * @see ViewElement
 * @see ViewListener
 * @see BaseViewButton
 */
public class EntireViewButton extends BaseViewButton {

    public EntireViewButton() {
        super(null);
    }

    @Override
    public EntireViewButton onClick(Button<Boolean> button, Program program) {
        return (EntireViewButton) super.onClick(button, program);
    }

    @Override
    public EntireViewButton onClick(Button<Boolean> button, Supplier<String> program) {
        return (EntireViewButton) super.onClick(button, (Program) program::get);
    }

    @Override
    public EntireViewButton onClick(Button<Boolean> button, Function<String, String> program) {
        return (EntireViewButton) super.onClick(button, (Program) () -> program.apply(getText()));
    }

    @Override
    public EntireViewButton whileClicked(Button<Boolean> button, Program program) {
        return (EntireViewButton) super.whileClicked(button, program);
    }

    @Override
    public EntireViewButton whileClicked(Button<Boolean> button, Supplier<String> program) {
        return (EntireViewButton) super.whileClicked(button, (Program) program::get);
    }

    @Override
    public EntireViewButton whileClicked(Button<Boolean> button, Function<String, String> program) {
        return (EntireViewButton) super.whileClicked(button, () -> program.apply(getText()));
    }

    @Override
    public EntireViewButton addBackgroundTask(Program program) {
        return (EntireViewButton) super.addBackgroundTask(program);
    }

    @Override
    public EntireViewButton addBackgroundTask(Supplier<String> program) {
        return (EntireViewButton) super.addBackgroundTask((Program) program::get);
    }

    @Override
    public EntireViewButton addBackgroundTask(Function<String, String> program) {
        return (EntireViewButton) super.addBackgroundTask(() -> program.apply(getText()));
    }
}
