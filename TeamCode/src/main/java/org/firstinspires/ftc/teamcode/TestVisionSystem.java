package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.VisionSubSystem;

import org.opencv.bioinspired.Bioinspired;
import org.opencv.bioinspired.Retina;
import org.opencv.core.Mat;
import org.opencv.core.Size;


public class TestVisionSystem extends VisionSubSystem {

    Retina retina;

    public TestVisionSystem(Robot robot) {
        super(robot);
    }

    @Override
    public void init() {
        retina = Retina.create(new Size(240,320),false, Bioinspired.RETINA_COLOR_BAYER,true);
    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {
        startVision();
    }

    @Override
    public void handle() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Mat onCameraFrame(Mat input) {
        Mat test = new Mat(input.size(),input.type());
        retina.applyFastToneMapping(input,test);
        input.release();
        return test;
    }

}
