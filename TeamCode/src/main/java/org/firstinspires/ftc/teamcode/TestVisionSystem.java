package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.robot.Camera;
import com.SCHSRobotics.HAL9001.system.robot.HALPipeline;
import com.SCHSRobotics.HAL9001.system.robot.Robot;
import com.SCHSRobotics.HAL9001.system.robot.VisionSubSystem;

import org.opencv.core.Mat;


public class TestVisionSystem extends VisionSubSystem {

    private boolean runVision = true;

    public TestVisionSystem(Robot robot) {
        super(robot);
    }

    @Override
    protected HALPipeline[] getPipelines() {
        return new HALPipeline[]{new TestPipeline()};
    }

    @Override
    public void init() {
    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {
        runVision = false;
        robot.reverseInternalCameraDirection();
    }

    @Override
    public void handle() {

    }

    @Override
    public void stop() {

    }

    @Camera(id = Robot.INTERNAL_CAMERA_ID)
    public class TestPipeline extends HALPipeline {
        @Override
        public Mat processFrame(Mat input) {
            return input;
        }

        @Override
        public boolean useViewport() {
            return runVision;
        }
    }
}
