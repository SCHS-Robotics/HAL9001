/*
 * Filename: AnglePIDTunerSystem.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/11/19
 */

package com.SCHSRobotics.HAL9001.util.calib;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.VisionSubSystem;
import com.SCHSRobotics.HAL9001.system.subsystems.MechanumDrive;
import com.SCHSRobotics.HAL9001.system.subsystems.OmniWheelDrive;
import com.SCHSRobotics.HAL9001.system.subsystems.QuadWheelDrive;
import com.SCHSRobotics.HAL9001.system.subsystems.TankDrive;
import com.SCHSRobotics.HAL9001.util.control.PIDController;
import com.SCHSRobotics.HAL9001.util.exceptions.GuiNotPresentException;
import com.SCHSRobotics.HAL9001.util.misc.BaseParam;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.misc.Grapher;
import com.SCHSRobotics.HAL9001.util.misc.Toggle;
import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.opencv.core.Mat;

/**
 * A subsystem used to tune turn-to-angle PID controllers.
 */
public class AnglePIDTunerSystem extends VisionSubSystem {

    //The PID controller used to control the robot's angle.
    private PIDController pidTuner;
    //The customizable gamepad containing all the controls fot the subsystem.
    private CustomizableGamepad inputs;
    //A toggle used to toggle between precision mode and fast mode.
    private Toggle slowModeToggle;
    //A grapher used to graph the controller's error as a function of time.
    private Grapher grapher;
    //The gyroscope used to track the robot's angle.
    private BNO055IMU imu;
    //The menu used to display the current kp, ki, and kd values.
    //private DisplayMenu display;
    //The last time in milliseconds that the PID coefficients were changed.
    private long lastActivatedTimestamp;
    //The PID coefficients.
    private double kp,ki,kd;
    //How much the coefficients will be incremented or decremented by.
    private double increment;
    //The target angle of the controller.
    private double setPoint;
    //The delay between each update to the PID controller's coefficients.
    private int delayMs;
    //The names of all the buttons used to change the PID coefficients.
    private final String SLOWMODE = "slowMode", P_INCREMENT = "PUp", P_DECREMENT = "PDown", I_INCREMENT = "IUp", I_DECREMENT = "IDown", D_INCREMENT = "DUp", D_DECREMENT = "DDown";
    //The type of drive system the robot is using.
    public enum DriveTrain {
        TANK, FOUR_WHEEL, MECHANUM, OMNIWHEEL
    }
    private DriveTrain driveType;

    private AngleUnit units;

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
    public AnglePIDTunerSystem(Robot robot, BaseParam driveParams, PIDController pidController, double setPoint, AngleUnit angleUnit) {
        super(robot);

        slowModeToggle = new Toggle(Toggle.ToggleTypes.flipToggle, false);

        increment = 0.1;
        lastActivatedTimestamp = 0;
        delayMs = 200;

        units = angleUnit;

        grapher = new Grapher(10,angleUnit == AngleUnit.DEGREES ? 360 : 2*Math.PI);

        inputs = new CustomizableGamepad(robot);

        inputs.addButton(P_INCREMENT,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(P_DECREMENT,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(I_INCREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_up));
        inputs.addButton(I_DECREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_down));
        inputs.addButton(D_INCREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_up));
        inputs.addButton(D_DECREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_down));
        inputs.addButton(SLOWMODE,new Button(1, Button.BooleanInputs.x));

        pidTuner = pidController;
        pidTuner.init(setPoint, 0);

        kp = pidTuner.getKp();
        ki = pidTuner.getKi();
        kd = pidTuner.getKd();

        /*if(!robot.usesGUI()) {
            throw new GuiNotPresentException("The GUI must be started to use the PID tuning program");
        }

        display = new DisplayMenu(robot.gui);
        robot.gui.addMenu("display",display);
        robot.gui.setActiveMenu("display");*/

        driveType = driveParams instanceof TankDrive.Params ? DriveTrain.TANK : driveParams instanceof QuadWheelDrive ? DriveTrain.FOUR_WHEEL : driveParams instanceof MechanumDrive.Params ? DriveTrain.MECHANUM : DriveTrain.OMNIWHEEL;

        switch(driveType) {
            case TANK: tankDrive = new TankDrive(robot,(TankDrive.Params) driveParams); break;
            case FOUR_WHEEL: quadWheelDrive = new QuadWheelDrive(robot, (QuadWheelDrive.Params) driveParams); break;
            case MECHANUM: mechanumDrive = new MechanumDrive(robot, (MechanumDrive.Params) driveParams); break;
            case OMNIWHEEL: omniWheelDrive = new OmniWheelDrive(robot, (OmniWheelDrive.Params) driveParams); break;
        }
    }

    @Override
    public void init() {

        imu = robot.hardwareMap.get(BNO055IMU.class,"imu");
        imu.initialize(new BNO055IMU.Parameters());
        while(robot.opModeIsActive() && !imu.isGyroCalibrated()) {
            robot.telemetry.addLine("Calibrating IMU...");
            robot.telemetry.update();
        }
        robot.telemetry.addLine("IMU Calibrated!");
        robot.telemetry.update();
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
        slowModeToggle.updateToggle(inputs.getBooleanInput(SLOWMODE));

        if (slowModeToggle.getCurrentState()) {
            increment = 0.01;
        } else {
            increment = 0.1;
        }

        if(System.currentTimeMillis() - lastActivatedTimestamp >= delayMs) {

            if (inputs.getBooleanInput(P_INCREMENT)) {
                kp += increment;
            } else if (inputs.getBooleanInput(P_DECREMENT)) {
                kp -= increment;
            }
            if (inputs.getBooleanInput(I_INCREMENT)) {
                ki += increment;
            } else if (inputs.getBooleanInput(I_DECREMENT)) {
                ki -= increment;
            }
            if (inputs.getBooleanInput(D_INCREMENT)) {
                kd += increment;
            } else if (inputs.getBooleanInput(D_DECREMENT)) {
                kd -= increment;
            }
            lastActivatedTimestamp = System.currentTimeMillis();
            pidTuner.setTunings(kp,ki,kd);

            double currentAngle = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, units).firstAngle;

            turn(pidTuner.getCorrection(currentAngle));

            robot.telemetry.addData("kp",kp);
            robot.telemetry.addData("ki",ki);
            robot.telemetry.addData("kd",kd);
            robot.telemetry.addData("current angle",currentAngle);
            robot.telemetry.addData("error",pidTuner.getError(currentAngle));
            robot.telemetry.update();
        }
    }

    @Override
    public void stop() {
        stopVision();
        stopMotors();
    }

    @Override
    public Mat onCameraFrame(Mat input) {
        input.release();

        return grapher.getNextFrame(pidTuner.getError(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, units).firstAngle));
    }

    private void turn(double power) {
        if(driveType == DriveTrain.TANK) {
            tankDrive.turn(power);
        }
        else if(driveType == DriveTrain.FOUR_WHEEL) {
            quadWheelDrive.turn(power);
        }
        else if(driveType == DriveTrain.MECHANUM) {
            mechanumDrive.turn(power);
        }
        else {
            omniWheelDrive.turn(power);
        }
    }

    private void stopMotors() {
        if(driveType == DriveTrain.TANK) {
            tankDrive.stopMovement();
        }
        else if(driveType == DriveTrain.FOUR_WHEEL) {
            quadWheelDrive.stopMovement();
        }
        else if(driveType == DriveTrain.MECHANUM) {
            mechanumDrive.stopAllMotors();
        }
        else {
            omniWheelDrive.stopAllMotors();
        }
    }
}