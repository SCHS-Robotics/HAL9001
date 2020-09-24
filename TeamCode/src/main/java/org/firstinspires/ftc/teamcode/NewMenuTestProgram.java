package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.gui.HALGUI;
import com.SCHSRobotics.HAL9001.system.gui.menus.TelemetryMenu;
import com.SCHSRobotics.HAL9001.system.robot.BaseTeleop;
import com.SCHSRobotics.HAL9001.system.robot.MainRobot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "New Menu Test Program")
public class NewMenuTestProgram extends BaseTeleop {
    public @MainRobot FakeDummyRobot robot;
    public HALGUI gui;
    TelemetryMenu menu1 = new TelemetryMenu();
    TelemetryMenu menu2 = new TelemetryMenu();
    int i = 0;

    @Override
    protected void onStart() {
        //gui = HALGUI.getInstance();
        //gui.setup(robot, new Button<>(1, Button.BooleanInputs.x));
        /*
        Payload payload = new Payload()
                .add(TextSelectionMenu.CHAR_SET_ID, Charset.ALPHANUMERIC_SPECIAL)
                .add(TextSelectionMenu.NEXT_MENU_ID, new ExampleMenu());
        gui.addRootMenu(new TextSelectionMenu(payload));
        */
/*
        Payload payload = new Payload()
                .add(ConfigConstants.SELECTION_MODE_ID, ConfigSelectionMode.AUTONOMOUS)
                .add(ConfigConstants.ROBOT_FILEPATH_ID, Environment.getExternalStorageDirectory().getPath()+"/System64"+"/robot_FakeDummyRobot");
*/

        //gui.addRootMenu(menu1);
        //gui.addRootMenu(menu2);

        //menu1.addData("test",1);
        //menu2.addLine("test2");
        //menu1.update();
        //menu2.update();
    }

    @Override
    protected void onUpdate() {
        /*gui.renderCurrentMenu();
        waitTime(1000, () -> gui.renderCurrentMenu());
        menu2.addData("test",i);
        menu2.update();
        i++;*/
    }

    @Override
    protected void onStop() {
        //gui.stop();
    }
}
