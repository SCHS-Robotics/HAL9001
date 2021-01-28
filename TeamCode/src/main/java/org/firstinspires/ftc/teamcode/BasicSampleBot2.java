package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.robot.Robot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class BasicSampleBot2 extends Robot {

    public SubSystem1 s1;
    public SubSystem2 s2;
    public SubSystem3 s3;

    /**
     * Constructor for robot.
     *
     * @param opMode - The opmode the robot is currently running.
     */
    public BasicSampleBot2(OpMode opMode) {
        super(opMode);
        s1 = new SubSystem1(this);
        s2 = new SubSystem2(this);
        s3 = new SubSystem3(this);
    }
}