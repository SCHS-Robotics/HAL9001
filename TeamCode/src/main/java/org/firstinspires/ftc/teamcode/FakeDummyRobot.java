package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.robot.Robot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;


public class FakeDummyRobot extends Robot {
    public SubSystem1 subSystem1;
    //public TestVisionSystem vision;
    //public TestVisionSystem2 vision2;
    //public @InternalCamera(resWidth = 320, resHeight = 240, usesViewport = true) OpenCvInternalCamera camera;

    public FakeDummyRobot(OpMode opMode) {
        super(opMode);
        //vision = new TestVisionSystem(this);
        //vision2 = new TestVisionSystem2(this);
        subSystem1 = new SubSystem1(this);
    }
}
