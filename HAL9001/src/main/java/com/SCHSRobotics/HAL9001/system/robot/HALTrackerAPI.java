package com.SCHSRobotics.HAL9001.system.robot;

import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class HALTrackerAPI extends OpenCvPipeline {
    private final List<HALPipeline> nonDisplayablePipelines = new ArrayList<>();
    private final Queue<HALPipeline> displayablePipelines = new LinkedBlockingQueue<>();

    public synchronized void addPipeline(HALPipeline pipeline) {
        nonDisplayablePipelines.add(pipeline);
        if (pipeline.useViewport()) {
            displayablePipelines.add(pipeline);
        }
    }

    public synchronized void removePipeline(HALPipeline tracker) {
        nonDisplayablePipelines.remove(tracker);
        displayablePipelines.remove(tracker);
    }

    @Override
    public synchronized Mat processFrame(Mat input) {
        if (nonDisplayablePipelines.size() == 0 && displayablePipelines.size() == 0) {
            return input;
        }

        for (HALPipeline pipeline : new ArrayList<>(nonDisplayablePipelines)) {
            if (pipeline.useViewport()) {
                displayablePipelines.add(pipeline);
                nonDisplayablePipelines.remove(pipeline);
            }
        }

        for (HALPipeline pipeline : new LinkedBlockingQueue<>(displayablePipelines)) {
            if (!pipeline.useViewport()) {
                displayablePipelines.remove(pipeline);
                nonDisplayablePipelines.add(pipeline);
            }
        }

        if (displayablePipelines.size() == 0) {
            for (HALPipeline pipeline : nonDisplayablePipelines) {
                pipeline.processFrameInternal(input);
            }
            return input;
        }

        HALPipeline currentPipeline = displayablePipelines.peek();
        Mat returnMat = currentPipeline.processFrameInternal(input);

        for (HALPipeline pipeline : displayablePipelines) {
            if (!pipeline.equals(currentPipeline)) {
                pipeline.processFrameInternal(input);
            }
        }

        for (HALPipeline pipeline : nonDisplayablePipelines) {
            pipeline.processFrameInternal(input);
        }

        return returnMat;
    }

    @Override
    public synchronized void onViewportTapped() {
        if (displayablePipelines.size() > 0) {
            displayablePipelines.add(displayablePipelines.poll());
        }
    }
}
