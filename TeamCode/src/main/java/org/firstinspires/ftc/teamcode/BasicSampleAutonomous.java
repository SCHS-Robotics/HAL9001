package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.BaseAutonomous;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.util.annotations.StandAlone;
import com.SCHSRobotics.HAL9001.util.misc.AutoTransitioner;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

//@StandAlone is not required but stops it from auto using configs from autonomous
@StandAlone
//@Disabled should be removed when using
//@Disabled
@Autonomous(name = "Basic Sample Bot Auto")
//extends BaseTeleop means it is a TeleOp program
public class BasicSampleAutonomous extends BaseAutonomous {

    private BasicSampleBot robot;

    //return the robot that will be used
    @Override
    protected Robot buildRobot() {
        robot = new BasicSampleBot(this);
        return robot;
    }

    @Override
    public void main() {
        AutoTransitioner.transitionOnStop(this, "Basic Sample Bot Teleop");
        while(opModeIsActive()) {
            telemetry.clearAll();
            telemetry.addLine("BAZAMBA");
            telemetry.update();
            Log.wtf("SHABAM","BAZAMBA");
        }
    }
}
