package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.BaseTeleop;
import com.SCHSRobotics.HAL9001.system.tempmenupackage.ExampleMenu;
import com.SCHSRobotics.HAL9001.system.tempmenupackage.HALGUI;
import com.SCHSRobotics.HAL9001.util.annotations.MainRobot;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "New Menu Test Program")
public class NewMenuTestProgram extends BaseTeleop {
    public @MainRobot FakeDummyRobot robot;
    public HALGUI gui;

    @Override
    protected void onStart() {
        gui = HALGUI.getInstance();
        gui.setup(robot, new Button<>(1, Button.BooleanInputs.noButton));
        gui.addRootMenu(new ExampleMenu());
    }

    @Override
    protected void onUpdate() {
        gui.renderCurrentMenu();
    }
}
