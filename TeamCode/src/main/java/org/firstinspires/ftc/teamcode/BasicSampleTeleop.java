package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.BaseTeleop;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.util.annotations.StandAlone;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

//@StandAlone is not required but stops it from auto using configs from autonomous
@StandAlone
//@Disabled should be removed when using
//@Disabled
@TeleOp(name = "Tank Sample")
//extends BaseTeleop means it is a TeleOp program
public class BasicSampleTeleop extends BaseTeleop {

    private BasicSampleBot robot;

    //return the robot that will be used
    @Override
    protected Robot buildRobot() {
        robot = new BasicSampleBot(this);
        return robot;
    }

    //Not necessary to have this (you can delete it), basically if you want to do something special on init you would put it here
    @Override
    protected void onInit() {}

    //Not necessary to have this (you can delete it), basically if you want to do something special on init in a loop you would put it here
    @Override
    protected void onInitLoop() {}

    //Not necessary to have this (you can delete it), basically if you want to do something special on start you would put it here
    @Override
    protected void onStart() {
        robot.vision.startVision();
    }

    //Not necessary to have this (you can delete it), basically if you want to do something special in a loop after pressing start you would put it here
    @Override
    protected void onUpdate() {}

    //Not necessary to have this (you can delete it), basically if you want to do something special on stop you would put it here
    @Override
    protected void onStop() {}
}
