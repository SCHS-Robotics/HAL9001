package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.BaseAutonomous;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.util.annotations.LinkTo;
import com.SCHSRobotics.HAL9001.util.annotations.ProgramOptions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

//@StandAlone is not required but stops it from auto using configs from autonomous
@Autonomous(name = "Basic Sample Bot Auto")
@LinkTo(destination = "Basic Sample Bot Teleop")
@ProgramOptions(options = {"a","b","c"})
//extends BaseTeleop means it is a TeleOp program
public class BasicSampleAutonomous extends BaseAutonomous {

    public BasicSampleBot robot;

    private String setting;

    //returns the robot that will be used
    @Override
    protected Robot buildRobot() {
        robot = new BasicSampleBot(this);
        Log.wtf("ran","ran");
        return robot;
    }

    @Override
    public void main() {
        setting = robot.pullProgramSetting();
        while(opModeIsActive()) {
            telemetry.clearAll();
            telemetry.addLine(setting);
            telemetry.update();
        }
    }
}
