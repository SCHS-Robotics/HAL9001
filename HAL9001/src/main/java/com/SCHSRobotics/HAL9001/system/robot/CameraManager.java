package com.SCHSRobotics.HAL9001.system.robot;

import android.util.Log;

import org.opencv.core.Size;
import org.openftc.easyopencv.OpenCvCamera;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CameraManager {
    private static final Map<String, OpenCvCamera> externalCameras = new HashMap<>();
    private static final Map<String, Size> resolutionMap = new HashMap<>();
    private static final Map<String, Boolean> isStartedMap = new HashMap<>();
    private static final Map<String, HALTrackerAPI> trackerAPIMap = new HashMap<>();
    private static OpenCvCamera internalCamera;
    private static String internalCameraId;

    private CameraManager() {
    }

    protected static void addCamera(String id, OpenCvCamera camera, CameraType cameraType, Size resolution) {
        resolutionMap.put(id, resolution);
        isStartedMap.put(id, false);
        trackerAPIMap.put(id, new HALTrackerAPI());

        switch (cameraType) {
            case INTERNAL:
                internalCameraId = id;
                internalCamera = camera;
                break;
            case EXTERNAL:
                externalCameras.put(id, camera);
                break;
        }
    }

    protected static void addPipelineToAll(HALPipeline pipeline) {
        for (String cameraId : trackerAPIMap.keySet()) {
            addPipeline(cameraId, pipeline);
        }
    }

    protected static void addPipeline(String cameraId, HALPipeline pipeline) {
        if (!cameraExists(cameraId)) {
            Log.e("HAL Camera Error", "Camera " + cameraId + " does not exist.");
            return;
        }

        Objects.requireNonNull(trackerAPIMap.get(cameraId)).addPipeline(pipeline);
    }

    protected static void removePipeline(String cameraId, HALPipeline pipeline) {
        if (cameraExists(cameraId)) {
            Objects.requireNonNull(trackerAPIMap.get(cameraId)).removePipeline(pipeline);
        }
    }

    protected static boolean cameraExists(String cameraId) {
        return cameraId.equals(internalCameraId) || externalCameras.containsKey(cameraId);
    }

    protected static void runPipelines() {
        for (String cameraId : trackerAPIMap.keySet()) {
            runHALTrackerAPI(cameraId, Objects.requireNonNull(trackerAPIMap.get(cameraId)));
        }
    }

    private static void runHALTrackerAPI(String cameraId, HALTrackerAPI halTrackerAPI) {
        if (!cameraExists(cameraId)) {
            Log.e("HAL Camera Error", "Camera " + cameraId + " does not exist.");
            return;
        }
        boolean isInternalCamera = cameraId.equals(internalCameraId);

        OpenCvCamera camera;
        if (isInternalCamera) {
            camera = internalCamera;
            Log.wtf("test", "test");
        } else {
            camera = Objects.requireNonNull(externalCameras.get(cameraId));
        }

        if (!Objects.requireNonNull(isStartedMap.get(cameraId))) {
            camera.setPipeline(halTrackerAPI);
            Size resolution = Objects.requireNonNull(resolutionMap.get(cameraId));

            camera.openCameraDeviceAsync(() -> camera.startStreaming((int) resolution.width, (int) resolution.height));
            isStartedMap.put(cameraId, true);
        }
    }

    protected static void resetManager() {
        externalCameras.clear();
        resolutionMap.clear();
        isStartedMap.clear();
        trackerAPIMap.clear();
        internalCamera = null;
        internalCameraId = null;
    }

    protected static void stopInternalCamera() {
        if (internalCamera != null) {
            internalCamera.stopStreaming();
            internalCamera.closeCameraDevice();
        }
    }

    protected static void overrideInternalCamera(OpenCvCamera newInternalCamera) {
        if (internalCamera != null) {
            internalCamera = newInternalCamera;
            isStartedMap.put(internalCameraId, false);
            runHALTrackerAPI(internalCameraId, trackerAPIMap.get(internalCameraId));
        } else {
            Log.e("HAL Camera Error", "Tried to override internal camera, but there is no defined internal camera.");
        }
    }

    protected static <T extends OpenCvCamera> T getCamera(String cameraId) {
        if (internalCamera != null && cameraId.equals(internalCameraId)) return (T) internalCamera;
        else if (externalCameras.containsKey(cameraId)) return (T) externalCameras.get(cameraId);
        else {
            Log.e("HAL Camera Error", "Tried to find camera with id " + cameraId + " but no camera with that id was registered.");
            return null;
        }
    }
}
