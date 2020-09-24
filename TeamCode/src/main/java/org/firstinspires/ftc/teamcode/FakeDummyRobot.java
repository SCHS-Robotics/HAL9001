package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.robot.Robot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;


public class FakeDummyRobot extends Robot {
    public SubSystem1 subSystem1;
    public FakeDummyRobot(OpMode opMode) {
        super(opMode);
        subSystem1 = new SubSystem1(this);
    }
}
