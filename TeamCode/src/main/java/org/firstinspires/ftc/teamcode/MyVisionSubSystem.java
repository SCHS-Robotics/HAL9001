package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.robot.Camera;
import com.SCHSRobotics.HAL9001.system.robot.HALPipeline;
import com.SCHSRobotics.HAL9001.system.robot.Robot;
import com.SCHSRobotics.HAL9001.system.robot.VisionSubSystem;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class MyVisionSubSystem extends VisionSubSystem {
    private final SquarePipeline myPipeline = new SquarePipeline();

    public MyVisionSubSystem(Robot robot) {
        super(robot);
    }

    @Override
    protected HALPipeline[] getPipelines() {
        return new HALPipeline[]{myPipeline};
    }

    @Override
    public void init() {

    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {

    }

    @Override
    public void handle() {

    }

    @Override
    public void stop() {

    }

    @Camera(id = "my camera")
    public static class SquarePipeline extends HALPipeline {
        @Override
        public Mat processFrame(Mat input) {
            Imgproc.rectangle(
                    input,
                    new Point(
                            input.cols() / 4,
                            input.rows() / 4),
                    new Point(
                            input.cols() * (3f / 4f),
                            input.rows() * (3f / 4f)),
                    new Scalar(0, 255, 0), 4);
            return input;
        }

        @Override
        public boolean useViewport() {
            return true;
        }
    }
}
