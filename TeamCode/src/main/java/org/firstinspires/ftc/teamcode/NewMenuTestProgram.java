package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.BaseTeleop;
import com.SCHSRobotics.HAL9001.system.tempmenupackage.ExampleMenu;
import com.SCHSRobotics.HAL9001.system.tempmenupackage.HALGUI;
import com.SCHSRobotics.HAL9001.system.tempmenupackage.Payload;
import com.SCHSRobotics.HAL9001.system.tempmenupackage.TextInput;
import com.SCHSRobotics.HAL9001.system.tempmenupackage.TextSelectionMenu;
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
        Log.wtf("robot null", ""+(robot == null));
        gui.setup(robot, new Button<>(1, Button.BooleanInputs.noButton));
        Payload payload = new Payload()
                .add(TextSelectionMenu.CHAR_SET_ID, TextInput.CharSet.ALPHANUMERIC_SPECIAL)
                .add(TextSelectionMenu.NEXT_MENU_ID, new ExampleMenu());
        gui.addRootMenu(new TextSelectionMenu(payload));
    }

    @Override
    protected void onUpdate() {
        gui.renderCurrentMenu();
    }

    @Override
    protected void onStop() {
        gui.stop();
    }
}
