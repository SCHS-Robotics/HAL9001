package com.SCHSRobotics.HAL9001.system.robot.subsystems.calib;

import com.SCHSRobotics.HAL9001.system.gui.menus.TelemetryMenu;
import com.SCHSRobotics.HAL9001.system.robot.Camera;
import com.SCHSRobotics.HAL9001.system.robot.HALPipeline;
import com.SCHSRobotics.HAL9001.system.robot.Robot;
import com.SCHSRobotics.HAL9001.system.robot.VisionSubSystem;
import com.SCHSRobotics.HAL9001.system.robot.subsystems.MechanumDrive;
import com.SCHSRobotics.HAL9001.system.robot.subsystems.OmniWheelDrive;
import com.SCHSRobotics.HAL9001.system.robot.subsystems.QuadWheelDrive;
import com.SCHSRobotics.HAL9001.system.robot.subsystems.TankDrive;
import com.SCHSRobotics.HAL9001.util.control.Button;
import com.SCHSRobotics.HAL9001.util.control.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.control.PIDController;
import com.SCHSRobotics.HAL9001.util.control.Toggle;
import com.SCHSRobotics.HAL9001.util.exceptions.GuiNotPresentException;
import com.SCHSRobotics.HAL9001.util.math.units.HALTimeUnit;
import com.SCHSRobotics.HAL9001.util.misc.BaseParam;
import com.SCHSRobotics.HAL9001.util.misc.Grapher;
import com.SCHSRobotics.HAL9001.util.misc.Timer;
import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.jetbrains.annotations.NotNull;
import org.opencv.core.Mat;

/**
 * A subsystem used to tune turn-to-angle PID controllers.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 8/11/19
 */
@SuppressWarnings("unused")
public class AnglePIDTunerSystem extends VisionSubSystem {

    //The PID controller used to control the robot's angle.
    private PIDController pidTuner;
    //The customizable gamepad containing all the controls fot the subsystem.
    private CustomizableGamepad inputs;
    //The names of all the buttons used to change the PID coefficients.
    private static final String SLOWMODE = "slowMode", P_INCREMENT = "PUp", P_DECREMENT = "PDown", I_INCREMENT = "IUp", I_DECREMENT = "IDown", D_INCREMENT = "DUp", D_DECREMENT = "DDown";
    //A grapher used to graph the controller's error as a function of time.
    private Grapher grapher;
    //The gyroscope used to track the robot's angle.
    private BNO055IMU imu;
    //A toggle used to toggle between precision mode and fast mode.
    private Toggle slowModeToggle = new Toggle(Toggle.ToggleTypes.flipToggle, false);
    //The PID coefficients.
    private double kp, ki, kd;
    //The menu used to display the current kp, ki, and kd values.
    private TelemetryMenu display;
    //The target angle of the controller.
    private double setPoint;
    //How much the coefficients will be incremented or decremented by.
    private double increment = 0.1;
    //The delay between each update to the PID controller's coefficients.
    private int delayMs = 200;
    private Timer loopTimer = new Timer();

    //The type of drive system the robot is using.
    public enum DriveTrain {
        TANK, FOUR_WHEEL, MECHANUM, OMNIWHEEL
    }

    private DriveTrain driveType;
    //The angle unit that the PID controller is using.
    private AngleUnit units;
    //The various different types of drive systems that can be tuned.
    private TankDrive tankDrive;
    private QuadWheelDrive quadWheelDrive;
    private MechanumDrive mechanumDrive;
    private OmniWheelDrive omniWheelDrive;

    /**
     * Constructor for AnglePIDTunerSystem.
     *
     * @param robot - The robot the subsystem belongs to.
     * @param pidController - The pid controller to tune.
     *
     * @throws GuiNotPresentException - Throws this exception if the GUI was not started before this subsystem was created.
     */
    public AnglePIDTunerSystem(@NotNull Robot robot, @NotNull BaseParam driveParams, @NotNull PIDController pidController, double setPoint, @NotNull AngleUnit angleUnit) {
        super(robot);

        units = angleUnit;

        grapher = new Grapher(10, angleUnit == AngleUnit.DEGREES ? 360 : 2 * Math.PI);

        inputs = new CustomizableGamepad(robot);

        inputs.addButton(P_INCREMENT, new Button<Boolean>(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(P_DECREMENT, new Button<Boolean>(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(I_INCREMENT, new Button<Boolean>(1, Button.BooleanInputs.bool_left_stick_y_up));
        inputs.addButton(I_DECREMENT, new Button<Boolean>(1, Button.BooleanInputs.bool_left_stick_y_down));
        inputs.addButton(D_INCREMENT, new Button<Boolean>(1, Button.BooleanInputs.bool_right_stick_y_up));
        inputs.addButton(D_DECREMENT, new Button<Boolean>(1, Button.BooleanInputs.bool_right_stick_y_down));
        inputs.addButton(SLOWMODE, new Button<Boolean>(1, Button.BooleanInputs.x));

        pidTuner = pidController;
        pidTuner.init(setPoint, 0);

        kp = pidTuner.getKp();
        ki = pidTuner.getKi();
        kd = pidTuner.getKd();

        if (!robot.usesGUI()) {
            robot.startGui(Button.noButtonBoolean);
        }

        display = new TelemetryMenu();
        robot.gui.addRootMenu(display);

        driveType = driveParams instanceof TankDrive.Params ? DriveTrain.TANK : driveParams instanceof QuadWheelDrive.Params ? DriveTrain.FOUR_WHEEL : driveParams instanceof MechanumDrive.Params ? DriveTrain.MECHANUM : DriveTrain.OMNIWHEEL;

        switch (driveType) {
            case TANK:
                tankDrive = new TankDrive(robot, (TankDrive.Params) driveParams);
                break;
            case FOUR_WHEEL:
                quadWheelDrive = new QuadWheelDrive(robot, (QuadWheelDrive.Params) driveParams);
                break;
            case MECHANUM:
                mechanumDrive = new MechanumDrive(robot, (MechanumDrive.Params) driveParams);
                break;
            case OMNIWHEEL:
                omniWheelDrive = new OmniWheelDrive(robot, (OmniWheelDrive.Params) driveParams);
                break;
        }
    }

    @Override
    public void init() {

        imu = robot.hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(new BNO055IMU.Parameters());
        while (robot.opModeIsActive() && !imu.isGyroCalibrated()) {
            display.addLine("Calibrating IMU...");
            display.update();
        }
        display.addLine("IMU Calibrated!");
        display.update();
    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {

    }

    @Override
    public void handle() {
        boolean slowMode = inputs.getInput(SLOWMODE);
        slowModeToggle.updateToggle(slowMode);

        if (slowModeToggle.getCurrentState()) {
            increment = 0.01;
        } else {
            increment = 0.1;
        }

        if (loopTimer.requiredTimeElapsed()) {
            loopTimer.start(delayMs, HALTimeUnit.MILLISECONDS);

            if (inputs.getInput(P_INCREMENT)) {
                kp += increment;
            } else if (inputs.getInput(P_DECREMENT)) {
                kp -= increment;
            }
            if (inputs.getInput(I_INCREMENT)) {
                ki += increment;
            } else if (inputs.getInput(I_DECREMENT)) {
                ki -= increment;
            }
            if (inputs.getInput(D_INCREMENT)) {
                kd += increment;
            } else if (inputs.getInput(D_DECREMENT)) {
                kd -= increment;
            }

            pidTuner.setTunings(kp, ki, kd);

            double currentAngle = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, units).firstAngle;

            turn(pidTuner.getCorrection(currentAngle));

            display.addData("kp", kp);
            display.addData("ki", ki);
            display.addData("kd", kd);
            display.addData("current angle", currentAngle);
            display.addData("error", pidTuner.getError(currentAngle));
            display.update();
        }
    }

    @Override
    public void stop() {
        stopMotors();
    }

    public void setDelayMs(int delayMs) {
        this.delayMs = delayMs;
    }

    private void turn(double power) {
        if (driveType == DriveTrain.TANK) {
            tankDrive.turn(power);
        } else if (driveType == DriveTrain.FOUR_WHEEL) {
            quadWheelDrive.turn(power);
        } else if (driveType == DriveTrain.MECHANUM) {
            mechanumDrive.turn(power);
        }
        else {
            omniWheelDrive.turn(power);
        }
    }

    private void stopMotors() {
        if(driveType == DriveTrain.TANK) {
            tankDrive.stopMovement();
        } else if (driveType == DriveTrain.FOUR_WHEEL) {
            quadWheelDrive.stopMovement();
        } else if (driveType == DriveTrain.MECHANUM) {
            mechanumDrive.stopAllMotors();
        } else {
            omniWheelDrive.stopAllMotors();
        }
    }

    @Override
    protected HALPipeline[] getPipelines() {
        return new HALPipeline[]{new AnglePIDTunerSystemPipeline()};
    }

    @Camera(id = Robot.ALL_CAMERAS_ID)
    public class AnglePIDTunerSystemPipeline extends HALPipeline {
        @Override
        public boolean useViewport() {
            return robot.isStarted();
        }

        @Override
        public Mat processFrame(Mat input) {
            return grapher.getNextFrame(pidTuner.getError(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, units).firstAngle));
        }
    }
}