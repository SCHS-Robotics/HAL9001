package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

import org.firstinspires.ftc.robotcore.external.function.Function;
import org.firstinspires.ftc.robotcore.external.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class ViewButton extends BaseViewButton {
    public ViewButton(@Nullable String text) {
        super(text);
    }

    @Override
    public ViewButton onClick(Button<Boolean> button, Program program) {
        return (ViewButton) super.onClick(button, program);
    }

    @Override
    public ViewButton onClick(Button<Boolean> button, Supplier<String> program) {
        return (ViewButton) super.onClick(button, program);
    }

    @Override
    public ViewButton onClick(Button<Boolean> button, Function<String, String> program) {
        return (ViewButton) super.onClick(button, program);
    }

    @Override
    public ViewButton whileClicked(Button<Boolean> button, Program program) {
        return (ViewButton) super.whileClicked(button, program);
    }

    @Override
    public ViewButton whileClicked(Button<Boolean> button, Supplier<String> program) {
        return (ViewButton) super.whileClicked(button, program);
    }

    @Override
    public ViewButton whileClicked(Button<Boolean> button, Function<String, String> program) {
        return (ViewButton) super.whileClicked(button, program);
    }

    @Override
    public ViewButton addBackgroundTask(Program program) {
        return (ViewButton) super.addBackgroundTask(program);
    }

    @Override
    public ViewButton addBackgroundTask(Supplier<String> program) {
        return (ViewButton) super.addBackgroundTask(program);
    }

    @Override
    public ViewButton addBackgroundTask(Function<String, String> program) {
        return (ViewButton) super.addBackgroundTask(program);
    }
}
