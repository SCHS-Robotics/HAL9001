package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.SCHSRobotics.HAL9001.util.math.units.TimeUnit;
import com.SCHSRobotics.HAL9001.util.misc.Timer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

@TeleOp(name = "Monospace")
public class MonospaceTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        OpenCvCamera phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

        phoneCam.setPipeline(new MyPipeline());

        phoneCam.openCameraDeviceAsync(() -> phoneCam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT));

        waitForStart();

        phoneCam.stopStreaming();
        phoneCam.closeCameraDevice();


        Timer timer = new Timer();
        timer.start(10, TimeUnit.SECONDS);
        while (!timer.requiredTimeElapsed() && !isStopRequested()) ;
    }

    public class MyPipeline extends OpenCvPipeline {
        @Override
        public Mat processFrame(Mat input) {
            return input;
        }
    }

    public class MyPipeline2 extends OpenCvPipeline {
        @Override
        public Mat processFrame(Mat input) {
            Mat gray = new Mat();
            Imgproc.cvtColor(input, gray, Imgproc.COLOR_RGBA2GRAY);
            Log.wtf("hello!", "hello world!");
            return gray;
        }
    }
}
