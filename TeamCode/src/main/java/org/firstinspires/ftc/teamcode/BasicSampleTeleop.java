package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.config.ConfigData;
import com.SCHSRobotics.HAL9001.system.config.ProgramOptions;
import com.SCHSRobotics.HAL9001.system.robot.BaseTeleop;
import com.SCHSRobotics.HAL9001.system.robot.LinkTo;
import com.SCHSRobotics.HAL9001.system.robot.MainRobot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

//@StandAlone is not required but stops it from auto using configs from autonomous
//@StandAlone
//@Disabled should be removed when using
//@Disabled
@LinkTo(destination = "Basic Sample Bot Auto")
@ProgramOptions(options = {BasicSampleTeleop.Test.class, BasicSampleTeleop.Test2.class})
@TeleOp(name = "Basic Sample Bot Teleop")
//extends BaseTeleop means it is a TeleOp program

public class BasicSampleTeleop extends BaseTeleop {

    enum Test {
        A, B, C, D, E
    }
    enum Test2 {
        F, G, H, I, J
    }

    public @MainRobot BasicSampleBot robot;
    private ConfigData setting;
    private Test a;
    private Test2 b;

    //return the robot that will be use.

    //Not necessary to have this (you can delete it), basically if you want to do something special on init you would put it here
    @Override
    protected void onInit() {}

    //Not necessary to have this (you can delete it), basically if you want to do something special on init in a loop you would put it here
    @Override
    protected void onInitLoop() {}

    //Not necessary to have this (you can delete it), basically if you want to do something special on start you would put it here
    @Override
    protected void onStart() {
        setting = robot.pullOpModeSettings();
        a = setting.getData("Test", Test.class);
        b = setting.getData("Test2",Test2.class);
    }

    //Not necessary to have this (you can delete it), basically if you want to do something special in a loop after pressing start you would put it here
    @Override
    protected void onUpdate() {
        telemetry.clearAll();
        telemetry.addLine(a.name());
        telemetry.addLine(b.name());
        telemetry.update();
    }

    //Not necessary to have this (you can delete it), basically if you want to do something special on stop you would put it here
    @Override
    protected void onStop() {}
}
