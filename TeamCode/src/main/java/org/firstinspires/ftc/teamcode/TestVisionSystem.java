package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.VisionSubsystem;

import org.opencv.core.Mat;

public class TestVisionSystem extends VisionSubsystem {

    public TestVisionSystem(Robot robot) {
        super(robot);
    }

    @Override
    public void init() throws InterruptedException {

    }

    @Override
    public void init_loop() throws InterruptedException {

    }

    @Override
    public void start() throws InterruptedException {

    }

    @Override
    public void handle() throws InterruptedException {

    }

    @Override
    public void stop() throws InterruptedException {

    }

    @Override
    public Mat onCameraFrame(Mat input) {
        return input;
    }

}
