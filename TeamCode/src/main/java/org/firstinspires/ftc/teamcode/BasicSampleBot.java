package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.robot.Robot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class BasicSampleBot extends Robot {

    public SubSystem1 s1;
    /**
     * Constructor for robot.
     *
     * @param opMode - The opmode the robot is currently running.
     */
    public BasicSampleBot(OpMode opMode) {
        super(opMode);

        s1 = new SubSystem1(this);
    }
}