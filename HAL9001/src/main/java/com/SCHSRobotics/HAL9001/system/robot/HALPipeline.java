package com.SCHSRobotics.HAL9001.system.robot;

import org.opencv.core.Mat;

public abstract class HALPipeline {
    private Mat mat = new Mat();
    private boolean requestStop = false;

    public abstract boolean useViewport();

    public abstract Mat processFrame(Mat input);

    public void stopPipeline() {
        requestStop = true;
    }

    protected final Mat processFrameInternal(Mat input) {
        input.copyTo(mat);
        return processFrame(mat);
    }
}
