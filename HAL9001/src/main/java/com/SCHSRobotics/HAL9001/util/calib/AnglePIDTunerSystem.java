/*
 * Filename: AnglePIDTunerSystem.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/11/19
 */

package com.SCHSRobotics.HAL9001.util.calib;

import com.SCHSRobotics.HAL9001.system.menus.DisplayMenu;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.SubSystem;
import com.SCHSRobotics.HAL9001.util.control.PIDController;
import com.SCHSRobotics.HAL9001.util.exceptions.GuiNotPresentException;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.misc.Grapher;
import com.SCHSRobotics.HAL9001.util.misc.Toggle;
import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

/**
 * A subsystem used to tune turn-to-angle PID controllers.
 */
public class AnglePIDTunerSystem extends SubSystem implements CameraBridgeViewBase.CvCameraViewListener2 {

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
    private DisplayMenu display;
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
    public enum DriveType {
        TWOWHEEL, FOURWHEEL
    }
    private DriveType driveType;

    /**
     * Constructor for AnglePIDTunerSystem.
     *
     * @param robot - The robot the subsystem belongs to.
     * @param setPoint - The controller's setpoint.
     *
     * @throws GuiNotPresentException - Throws this exception if the GUI was not started before this subsystem was created.
     */
    public AnglePIDTunerSystem(Robot robot, double setPoint) {
        super(robot);

        inputs = new CustomizableGamepad(robot);

        inputs.addButton(P_INCREMENT,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(P_DECREMENT,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(I_INCREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_up));
        inputs.addButton(I_DECREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_down));
        inputs.addButton(D_INCREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_up));
        inputs.addButton(D_DECREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_down));
        inputs.addButton(SLOWMODE,new Button(1, Button.BooleanInputs.x));

        pidTuner = new PIDController(kp,ki,kd);
        pidTuner.setSetpoint(setPoint);

        if(!robot.usesGUI()) {
            throw new GuiNotPresentException("The GUI must be started to use the PID tuning program");
        }

        display = new DisplayMenu(robot.gui);
        robot.gui.addMenu("display",display);
    }

    /**
     * Constructor for AnglePIDTunerSystem.
     *
     * @param robot - The robot the subsystem belongs to.
     * @param pidController - The pid controller to tune.
     *
     * @throws GuiNotPresentException - Throws this exception if the GUI was not started before this subsystem was created.
     */
    public AnglePIDTunerSystem(Robot robot, PIDController pidController, double setPoint) {
        super(robot);

        inputs = new CustomizableGamepad(robot);

        inputs.addButton(P_INCREMENT,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(P_DECREMENT,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(I_INCREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_up));
        inputs.addButton(I_DECREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_down));
        inputs.addButton(D_INCREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_up));
        inputs.addButton(D_DECREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_down));
        inputs.addButton(SLOWMODE,new Button(1, Button.BooleanInputs.x));

        this.setPoint = setPoint;

        pidTuner = pidController;
        pidTuner.setSetpoint(setPoint);
        pidTuner.setTunings(kp,ki,kd);


        if(!robot.usesGUI()) {
            throw new GuiNotPresentException("The GUI must be started to use the PID tuning program");
        }

        display = new DisplayMenu(robot.gui);
        robot.gui.addMenu("display",display);
    }

    /**
     * Constructor for AnglePIDTunerSystem
     *
     * @param robot - The robot the subsystem belongs to.
     * @param setPoint - The controller's setpoint.
     * @param type - The type of PID controller being used.
     *
     * @throws GuiNotPresentException - Throws this exception if the GUI was not started before this subsystem was created.
     */
    public AnglePIDTunerSystem(Robot robot, double setPoint, PIDController.Type type) {
        super(robot);

        inputs = new CustomizableGamepad(robot);

        inputs.addButton(P_INCREMENT,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(P_DECREMENT,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(I_INCREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_up));
        inputs.addButton(I_DECREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_down));
        inputs.addButton(D_INCREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_up));
        inputs.addButton(D_DECREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_down));
        inputs.addButton(SLOWMODE,new Button(1, Button.BooleanInputs.x));

        this.setPoint = setPoint;

        pidTuner = new PIDController(kp,ki,kd,type);
        pidTuner.setSetpoint(setPoint);

        if(!robot.usesGUI()) {
            throw new GuiNotPresentException("The GUI must be started to use the PID tuning program");
        }

        display = new DisplayMenu(robot.gui);
        robot.gui.addMenu("display",display);
    }

    /**
     * Constructor for AnglePIDTunerSystem.
     *
     * @param robot - The robot the subsystem belongs to.
     * @param setPoint - The controller's setpoint.
     * @param type - The type of PID controller being used.
     * @param iClampLower - The lower value for the clamp on the integral term.
     * @param iClampUpper - The upper value for the clamp on the integral term.
     *
     * @throws GuiNotPresentException - Throws this exception if the GUI was not started before this subsystem was created.
     */
    public AnglePIDTunerSystem(Robot robot, double setPoint, PIDController.Type type, double iClampLower, double iClampUpper) {
        super(robot);

        inputs = new CustomizableGamepad(robot);

        inputs.addButton(P_INCREMENT,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(P_DECREMENT,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(I_INCREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_up));
        inputs.addButton(I_DECREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_down));
        inputs.addButton(D_INCREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_up));
        inputs.addButton(D_DECREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_down));
        inputs.addButton(SLOWMODE,new Button(1, Button.BooleanInputs.x));

        this.setPoint = setPoint;

        pidTuner = new PIDController(kp,ki,kd,type);
        pidTuner.setSetpoint(setPoint);
        pidTuner.setIClamp(iClampLower,iClampUpper);

        if(!robot.usesGUI()) {
            throw new GuiNotPresentException("The GUI must be started to use the PID tuning program");
        }

        display = new DisplayMenu(robot.gui);
        robot.gui.addMenu("display",display);
    }

    /**
     * Constructor for AnglePIDTunerSystem.
     *
     * @param robot - The robot the subsystem belongs to.
     * @param setPoint - The controller's setpoint.
     * @param type - The type of PID controller being used.
     * @param iClampLower - The lower value for the clamp on the integral term.
     * @param iClampUpper - The upper value for the clamp on the integral term.
     * @param outputClampLower - The lower value for the clamp on the controller's output.
     * @param outputClampUpper - The upper value for the clamp on the controller's output.
     *
     * @throws GuiNotPresentException - Throws this exception if the GUI was not started before this subsystem was created.
     */
    public AnglePIDTunerSystem(Robot robot, double setPoint, PIDController.Type type, double iClampLower, double iClampUpper, double outputClampLower, double outputClampUpper) {
        super(robot);

        inputs = new CustomizableGamepad(robot);

        inputs.addButton(P_INCREMENT,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(P_DECREMENT,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(I_INCREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_up));
        inputs.addButton(I_DECREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_down));
        inputs.addButton(D_INCREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_up));
        inputs.addButton(D_DECREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_down));
        inputs.addButton(SLOWMODE,new Button(1, Button.BooleanInputs.x));

        this.setPoint = setPoint;

        pidTuner = new PIDController(kp,ki,kd,type);
        pidTuner.setSetpoint(setPoint);
        pidTuner.setIClamp(iClampLower,iClampUpper);
        pidTuner.setOutputClamp(outputClampLower,outputClampUpper);

        if(!robot.usesGUI()) {
            throw new GuiNotPresentException("The GUI must be started to use the PID tuning program");
        }

        display = new DisplayMenu(robot.gui);
        robot.gui.addMenu("display",display);
    }

    /**
     * Constuctor for AnglePIDTunerSystem.
     *
     * @param robot - The robot the subsystem belongs to.
     * @param setPoint - The controller's setpoint.
     * @param type - The type of PID controller being used.
     * @param iClampLower - The lower value for the clamp on the integral term.
     * @param iClampUpper - The upper value for the clamp on the integral term.
     * @param outputClampLower - The lower value for the clamp on the controller's output.
     * @param outputClampUpper - The upper value for the clamp on the controller's output.
     * @param PonMClampLower - The lower value for the clamp on the controller's proportional term in PonM mode.
     * @param PonMClampUpper - The upper value for the clamp on the controller's proportional term in PonM mode.
     *
     * @throws GuiNotPresentException - Throws this exception if the GUI was not started before this subsystem was created.
     */
    public AnglePIDTunerSystem(Robot robot, double setPoint, PIDController.Type type, double iClampLower, double iClampUpper, double outputClampLower, double outputClampUpper, double PonMClampLower, double PonMClampUpper) {
        super(robot);

        inputs = new CustomizableGamepad(robot);

        inputs.addButton(P_INCREMENT,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(P_DECREMENT,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(I_INCREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_up));
        inputs.addButton(I_DECREMENT,new Button(1, Button.BooleanInputs.bool_left_stick_y_down));
        inputs.addButton(D_INCREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_up));
        inputs.addButton(D_DECREMENT,new Button(1, Button.BooleanInputs.bool_right_stick_y_down));
        inputs.addButton(SLOWMODE,new Button(1, Button.BooleanInputs.x));

        this.setPoint = setPoint;

        pidTuner = new PIDController(kp,ki,kd,type);
        pidTuner.setSetpoint(setPoint);
        pidTuner.setIClamp(iClampLower,iClampUpper);
        pidTuner.setOutputClamp(outputClampLower,outputClampUpper);
        pidTuner.setPonMClamp(PonMClampLower,PonMClampUpper);

        if(!robot.usesGUI()) {
            throw new GuiNotPresentException("The GUI must be started to use the PID tuning program");
        }

        display = new DisplayMenu(robot.gui);
        robot.gui.addMenu("display",display);
    }

    @Override
    public void init() {

        imu = robot.hardwareMap.get(BNO055IMU.class,"imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imu.initialize(parameters);
    }

    @Override
    public void init_loop() {

        display.clear();
        if(!imu.isGyroCalibrated()) {
            display.addLine("Calibrating IMU...");
        }
        else {
            display.addLine("IMU Calibrated!");
        }
    }

    @Override
    public void start() {

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

            display.addData("kp",kp);
            display.addData("ki",ki);
            display.addData("kd",kd);
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        inputFrame.gray().release();
        inputFrame.rgba().release();

        return grapher.getNextFrame(setPoint-imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle);
    }

    @Override
    protected void initVars() {
        slowModeToggle = new Toggle(Toggle.ToggleTypes.flipToggle, false);

        increment = 0.1;
        lastActivatedTimestamp = 0;
        delayMs = 200;

        kp = 0;
        ki = 0;
        kd = 0;

        grapher = new Grapher(10,2*Math.PI);
    }
}