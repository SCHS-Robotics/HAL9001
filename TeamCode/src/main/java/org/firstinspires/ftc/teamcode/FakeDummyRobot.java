package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.robot.ExternalCamera;
import com.SCHSRobotics.HAL9001.system.robot.InternalCamera;
import com.SCHSRobotics.HAL9001.system.robot.Robot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvInternalCamera;


public class FakeDummyRobot extends Robot {
    @InternalCamera(resWidth = 320, resHeight = 240, usesViewport = true)
    public OpenCvInternalCamera internalCamera = (OpenCvInternalCamera) getCamera(Robot.INTERNAL_CAMERA_ID);
    @ExternalCamera(resWidth = 320, resHeight = 240, configName = "my camera", usesViewport = false)
    public OpenCvCamera camera = getCamera("my camera");

    public FakeDummyRobot(OpMode opMode) {
        super(opMode);
    }
}
