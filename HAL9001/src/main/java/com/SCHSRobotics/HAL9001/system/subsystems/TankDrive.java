/*
 * Filename: TankDrive.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/17/19
 */

package com.SCHSRobotics.HAL9001.system.subsystems;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.SubSystem;
import com.SCHSRobotics.HAL9001.util.annotations.AutonomousConfig;
import com.SCHSRobotics.HAL9001.util.annotations.TeleopConfig;
import com.SCHSRobotics.HAL9001.util.control.PIDController;
import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.InvalidMoveCommandException;
import com.SCHSRobotics.HAL9001.util.exceptions.NotBooleanInputException;
import com.SCHSRobotics.HAL9001.util.exceptions.NotDoubleInputException;
import com.SCHSRobotics.HAL9001.util.math.Vector;
import com.SCHSRobotics.HAL9001.util.misc.BaseParam;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.ConfigParam;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.misc.Toggle;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.Map;

/**
 * A customizable tankdrive subsystem.
 */
public class TankDrive extends SubSystem {

    //A boolean specifying whether to set numbers to specific values or to use config.
    private static boolean useSpecific = false;
    //The drivetrain's two motors
    private DcMotor left, right;
    //A boolean determining if the robot should be allowed to turn and move simultaneously
    private boolean turnAndMove;
    //A toggle object that detects if a boolean input changes twice (like a square pulse)
    private Toggle speedToggle = new Toggle(Toggle.ToggleTypes.flipToggle, false);
    //Modifiers for speed and joystick operations
    private double currentSpeedModeModifier, speedModeModifier, constantSpeedModifier;
    //Object that stores wanted buttons and is used to retrieve button inputs.
    private CustomizableGamepad inputs;
    //Button names CustomizableGamepad will use.
    private static final String SPEEDMODEBUTTON = "speedModeButton", DRIVESTICK = "driveStick", TURNSTICK = "turnStick";

    /**
     * This constructor creates the drive system without using config.
     *
     * @param robot - The robot we will be using.
     * @param params - The parameters for the drive system.
     */
    public TankDrive(Robot robot, Params params){
        super(robot);

        inputs = new CustomizableGamepad(robot);

        setMotorConfiguration(params.leftMotor, params.rightMotor);
        setSpeedModeModifier(params.speedModeModifier);
        setConstantSpeedModifier(params.constantSpeedModifier);
        setTurnAndMove(params.turnAndMove);
        setDriveStick(params.buttonsToSet[0]);
        setTurnStick(params.buttonsToSet[1]);
        setSpeedMode(params.buttonsToSet[2]);
    }

    /**
     * This constructor creates the drive system with config and specific parameter settings.
     *
     * @param robot - The robot we will be using.
     * @param params - The parameters for the drive system.
     */
    public TankDrive(Robot robot, NumberParams params){
        super(robot);

        setMotorConfiguration(params.leftMotor,params.rightMotor);
        
        setSpeedModeModifier(params.speedModeModifier);
        setConstantSpeedModifier(params.constantSpeedModifier);
        
        usesConfig = true;
    }

    /**
     * his constructor creates the drive system with config.
     *
     * @param robot - The robot we will be using.
     * @param LeftMotorConfig - The left motor configuration name.
     * @param RightMotorConfig - The right motor configuration name.
     */
    public TankDrive(Robot robot, String LeftMotorConfig, String RightMotorConfig){
        super(robot);

        setMotorConfiguration(LeftMotorConfig, RightMotorConfig);

        usesConfig = true;
    }

    @Override
    public void init()
    {
        normalDirection();
        resetEncoders();
    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {
        if(usesConfig && robot.isTeleop()) {
            setUsingConfigs();
        }
        else if(usesConfig && robot.isAutonomous()) {
            setUsingConfigsAutonomous();
        }
    }

    @Override
    public void handle() {
        if(!inputs.checkNoButton(SPEEDMODEBUTTON)){
            speedToggle.updateToggle(inputs.getBooleanInput(SPEEDMODEBUTTON));
            if(speedToggle.getCurrentState()){
                currentSpeedModeModifier = speedModeModifier;
            }
            else {
                currentSpeedModeModifier = 1;
            }
        }
        //drives forward and turns at the same time
        if (turnAndMove) {
            if (inputs.getDoubleInput(DRIVESTICK)!= 0 && inputs.getDoubleInput(TURNSTICK) != 0) {
                turnAndMove(new Vector(inputs.getDoubleInput(DRIVESTICK), inputs.getDoubleInput(TURNSTICK)));
            } else if (inputs.getDoubleInput(DRIVESTICK) != 0) {
                drive(inputs.getDoubleInput(DRIVESTICK));
            } else if (inputs.getDoubleInput(TURNSTICK)!= 0){
                turn(inputs.getDoubleInput(TURNSTICK));
            } else {
                stopMovement();
            }
        }
        //drives forward and turns but not at the same time
        else {
            if (inputs.getDoubleInput(TURNSTICK) != 0) {
                turn(inputs.getDoubleInput(TURNSTICK));
            } else if (inputs.getDoubleInput(DRIVESTICK) != 0) {
                drive(inputs.getDoubleInput(DRIVESTICK));
            } else {
                stopMovement();
            }
        }
    }

    @Override
    public void stop() {
        stopMovement();
    }

    /**
     * Reverses direction of the robot.
     */
    public void reverseDirection(){
        left.setDirection(DcMotor.Direction.FORWARD);
        right.setDirection(DcMotor.Direction.REVERSE);
    }

    /**
     * Sets the direction of the robot(default direction).
     */
    public void normalDirection(){
        left.setDirection(DcMotor.Direction.REVERSE);
        right.setDirection(DcMotor.Direction.FORWARD);
    }

    /**
     * Resents the encoders.
     */
    public void resetEncoders(){
        left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Makes the robot drive.
     *
     * @param speed - Speed to drive. Positive for forward and negative for backwards.
     */
    public void drive(double speed){
        left.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
        right.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
    }

    /**
     * Makes the robot turn.
     *
     * @param speed - Speed to turn at. (positive speed is turn counterclockwise & negative speed is turn clockwise).
     */
    public void turn(double speed){
        left.setPower(-(speed * constantSpeedModifier) * currentSpeedModeModifier);
        right.setPower(((speed * constantSpeedModifier) * currentSpeedModeModifier));
    }

    /**
     * Moves forward and turns at the same time.
     *
     * @param input - Sets direction and rotational speed. (X is left and right, Y is forward and backwards)
     */
    public void turnAndMove(Vector input){
        left.setPower(((input.x - input.y) * constantSpeedModifier) * currentSpeedModeModifier);
        right.setPower(((input.x + input.y) * constantSpeedModifier) * currentSpeedModeModifier);
    }

    /**
     * Stops all movement.
     */
    public void stopMovement(){
        left.setPower(0);
        right.setPower(0);
    }

    /**
     * Sets the power of the left motor.
     *
     * @param speed - Power to set the motor to. Positive for forward and negative for backwards.
     */
    public void setPowerLeft(double speed){
        left.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
    }

    /**
     * Sets the power of the right motor.
     *
     * @param speed - Power to set the motor to. Positive for forward and negative for backwards.
     */
    public void setPowerRight(double speed){
        right.setPower((speed * constantSpeedModifier) * currentSpeedModeModifier);
    }

    /**
     * Moves forward or backwards for a set time.
     *
     * @param timeMs - time to drive for in milliseconds.
     * @param power - power to drive at. Positive for forward and negative for backwards.
     */
    public void driveTime(long timeMs, double power) {

        if(timeMs < 0) {
            throw new DumpsterFireException("HAL is cool, but can't travel back in time. Time must be positive.");
        }
        drive(power);
        waitTime(timeMs);
        stopMovement();
    }

    /**
     * Turns for a set time (positive power for counterclockwise negative power for clockwise).
     *
     * @param timeMs - time to turn for in milliseconds.
     * @param power - power to turn at.
     */
    public void turnTime(long timeMs, double power) {
        if(timeMs < 0) {
            throw new DumpsterFireException("HAL is cool, but can't travel back in time. Time must be positive.");
        }

        turn(power);
        waitTime(timeMs);
        stopMovement();
    }

    /**
     * Drives and turns for a set time (positive power for counterclockwise or forward negative power for clockwise or backwards).
     *
     * @param timeMs - time to turn and drive for in milliseconds.
     * @param input - Sets direction and rotational speed. (X is left and right, Y is forward and backwards)
     */
    public void turnAndMoveTime(long timeMs, Vector input) {
        if(timeMs < 0) {
            throw new DumpsterFireException("HAL is cool, but can't travel back in time. Time must be positive.");
        }

        double startTime = System.currentTimeMillis();
        turnAndMove(input);
        waitTime(timeMs);
        stopMovement();
    }

    /**
     * Dives using encoders.
     *
     * @param encoderDistance - Encoder distance to travel.
     * @param power - Double from (-1)-(1) of intensity of the movement.
     */
    public void driveEncoders(int encoderDistance, double power) {
        if(power == 0 && encoderDistance != 0) {
            throw new InvalidMoveCommandException("Power cannot be zero with a non zero target");
        }

        if (encoderDistance < 0) {
            throw new DumpsterFireException("Where you're going, you don't need roads! (distance must be positive)");
        }

        int startEncoderPos = left.getCurrentPosition();
        drive(power);
        waitWhile(() -> Math.abs(left.getCurrentPosition() - startEncoderPos) <= encoderDistance);
        stopMovement();
    }

    /**
     * Turns using encoders.
     *
     * @param encoderDistance - Encoder distance to travel.
     * @param power - double from (-1)-(1) that represents the speed of turn (positive for counterclockwise negative for clockwise).
     */
    public void turnEncoders(int encoderDistance, double power){

        if(encoderDistance < 0) {
            throw new DumpsterFireException("Where you're going, you don't need roads! (distance must be positive)");
        }

        int leftStartEncoderPos = left.getCurrentPosition();
        int rightStartEncoderPos = right.getCurrentPosition();
        if(power > 0) {
            turn(power);
            waitWhile(() -> Math.abs(left.getCurrentPosition() - leftStartEncoderPos) <= encoderDistance);
            stopMovement();
        }
        if (power < 0){
            turn(power);
            waitWhile(() -> Math.abs(right.getCurrentPosition() - rightStartEncoderPos) <= encoderDistance);
            stopMovement();
        }
    }

    /**
     * Turn while driving using encoders.
     *
     * @param encoderDistance - Encoder distance to travel.
     * @param input - Sets direction and rotational speed. (X is left and right, Y is forward and backwards)
     */
    public void turnAndMoveEncoders(int encoderDistance, Vector input) {

        if(encoderDistance < 0) {
            throw new DumpsterFireException("Where you're going, you don't need roads! (distance must be positive)");
        }

        int leftStartEncoderPos = left.getCurrentPosition();
        int rightStartEncoderPos = right.getCurrentPosition();
        turnAndMove(input);
        waitWhile(() -> Math.abs((left.getCurrentPosition() - leftStartEncoderPos)/2 + (right.getCurrentPosition() - rightStartEncoderPos)/2) <= encoderDistance);
        stopMovement();
    }


    /**TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
     * WIP
     */
    @Deprecated
    public void PIDDrive(double kp, double ki, double kd){
        PIDController pid = new PIDController(kp, ki, kd);
        //turnClockwise(pid.getCorrection());
    }

    @Deprecated
    public void PIDTurn(double kp, double ki, double kd, double targetAngle) {
        PIDController pid = new PIDController(kp, ki, kd);
        pid.setSetpoint(targetAngle);



        //turnClockwise(-pid.getCorrection());
    }

    /**
     * Configures the motors.
     *
     * @param leftConfigurationName - Left motor configuration name.
     * @param rightConfigurationName - Right motor configuration name.
     */
    public void setMotorConfiguration(String leftConfigurationName, String rightConfigurationName){
        left = robot.hardwareMap.dcMotor.get(leftConfigurationName);
        right = robot.hardwareMap.dcMotor.get(rightConfigurationName);
    }

    /**
     * Sets speedModeModifier.
     *
     * @param speedModeModifier - Value to set speedModeModifier to.
     */
    public void setSpeedModeModifier(double speedModeModifier){
        this.speedModeModifier = speedModeModifier;
        currentSpeedModeModifier = 1;
    }

    /**
     * Sets constantSpeedModifier.
     *
     * @param constantSpeedModifier - Value to set constantSpeedModeModifier to.
     */
    public void setConstantSpeedModifier(double constantSpeedModifier){ this.constantSpeedModifier = constantSpeedModifier; }

    /**
     * Sets wether or not to be able to turn and move at the same time.
     *
     * @param turnAndMove - Should the robot turn and move at the same time true or false.
     */
    public void setTurnAndMove(boolean turnAndMove) { this.turnAndMove = turnAndMove; }

    /**
     * Sets the double input responsible for moving forward and backwards.
     *
     * @param button - Double input responsible for moving forward and backwards.
     *
     * @throws NotDoubleInputException - Throws an exception if button does not return double values.
     */
    public void setDriveStick(Button button){
        if(button.isDouble) {
            inputs.addButton(DRIVESTICK, button);
        }
        else {
            throw new NotDoubleInputException("driveStick was not set to a double input");
        }
    }

    /**
     * Sets the double input responsible for turning right and left.
     *
     * @param button - Double input responsible for turning right and left.
     *
     * @throws NotDoubleInputException - Throws an exception if button does not return double values.
     */
    public void setTurnStick(Button button){
        if(button.isDouble) {
            inputs.addButton(TURNSTICK, button);
        }
        else {
            throw new NotDoubleInputException("turnStick was not set to a double input");
        }
    }

    /**
     * Sets the boolean input responsible for toggling speedMode.
     *
     * @param button - The boolean input responsible for toggling speedMode.
     *
     * @throws NotBooleanInputException - Throws an exception if button does not return boolean values.
     */
    public void setSpeedMode(Button button){
        if(button.isBoolean) {
            inputs.addButton(SPEEDMODEBUTTON, button);
        }
        else {
            throw new NotBooleanInputException("speedModeButton was not set to a boolean input");
        }
    }

    /**
     * Pulls the config settings for teleop.
     */
    private void setUsingConfigs() {
        inputs = robot.pullControls(this);
        Map<String, Object> settingsData = robot.pullNonGamepad(this);

        setTurnAndMove((boolean) settingsData.get("Turn and Move"));
        if (!useSpecific) {
            setConstantSpeedModifier((double) settingsData.get("ConstantSpeedModifier"));
            setSpeedModeModifier((double) settingsData.get("SpeedModeModifier"));
        }
    }

    /**
     * Pulls the config settings for autonomous.
     */
    private void setUsingConfigsAutonomous(){
        Map<String, Object> settingsData = robot.pullNonGamepad(this);

        setConstantSpeedModifier((double) settingsData.get("ConstantSpeedModifier"));
    }

    /**
     * Gets the left motor encoder position.
     *
     * @return The left motor encoder position.
     */
    public int getLeftMotorEncoderPos(){
        return left.getCurrentPosition();
    }

    /**
     * Gets the right motor encoder position.
     *
     * @return The right motor encoder position.
     */
    public int getRightMotorEncoderPos(){
        return right.getCurrentPosition();
    }

    /**
     * Gets an array of all the motor encoder positions.
     *
     * @return An array of all the motor encoder positions.
     */
    public int[] getMotorEncoderPoses(){
        return new int[]{left.getCurrentPosition(), right.getCurrentPosition()};
    }

    /**
     * The teleop configuration settings.
     *
     * @return The teleop configuration settings.
     */
    @TeleopConfig
    public static ConfigParam[] teleOpConfig() {
        if(useSpecific) {
            return new ConfigParam[]{
                    new ConfigParam(DRIVESTICK, Button.DoubleInputs.left_stick_y),
                    new ConfigParam(TURNSTICK, Button.DoubleInputs.right_stick_x),
                    new ConfigParam(SPEEDMODEBUTTON, Button.BooleanInputs.noButton),
                    new ConfigParam("Turn and Move", ConfigParam.booleanMap, true)
            };
        }
        else {
            return new ConfigParam[]{
                    new ConfigParam(DRIVESTICK, Button.DoubleInputs.left_stick_y),
                    new ConfigParam(TURNSTICK, Button.DoubleInputs.right_stick_x),
                    new ConfigParam(SPEEDMODEBUTTON, Button.BooleanInputs.noButton),
                    new ConfigParam("Turn and Move", ConfigParam.booleanMap, true),
                    new ConfigParam("SpeedModeModifier", ConfigParam.numberMap(0,100, .05), 1.0),
                    new ConfigParam("ConstantSpeedModifier", ConfigParam.numberMap(0,100, .05), 1.0)
            };
        }
    }

    /**
     * The autonomous configuration settings.
     *
     * @return The autonomous configuration settings.
     */
    @AutonomousConfig
    public static ConfigParam[] autonomousConfig() {
        return new ConfigParam[]{
                new ConfigParam("ConstantSpeedModifier", ConfigParam.numberMap(0,100, .05), 1.0)
        };
    }

    /**
     * A parameters class used to pass parameters to the drive.
     */
    public static final class Params implements BaseParam {
        //Motor config names to be used in TankDrive to set the motors
        private String leftMotor, rightMotor;
        //Array of buttons to set the buttons to for the TankDrive class [0] is driveStick, [1] is turnStick, and [2] is speedModeButton.
        private Button[] buttonsToSet = new Button[3];
        //A boolean value specifying if the drivetrain is allowed to turn and move simultaneously.
        private boolean turnAndMove = true;
        //Various double values for speed control.
        private double speedModeModifier = 1, constantSpeedModifier = 1;

        /**
         * Constructor for Params.
         * 
         * @param leftMotorConfig - The left motor configuration name.
         * @param rightMotorConfig - The right motor configuration name.
         */
        public Params(String leftMotorConfig, String rightMotorConfig) {
            leftMotor = leftMotorConfig;
            rightMotor = rightMotorConfig;
            setDefaultButtons();
        }

        /**
         * Sets if the drive can turn and move simultaneously.
         *
         * @param turnAndMove - Whether the robot can turn and move simultaneously.
         * @return This instance of Params.
         */
        public Params setTurnAndMove(boolean turnAndMove) {
            this.turnAndMove = turnAndMove;
            return this;
        }

        /**
         * Sets the drive's speed mode modifier.
         *
         * @param speedModeModifier - The speed mode modifier.
         * @return This instance of Params.
         */
        public Params setSpeedModeModifier(double speedModeModifier) {
            this.speedModeModifier = speedModeModifier;
            return this;
        }

        /**
         * Sets the drive's constant speed modifier.
         *
         * @param constantSpeedModifier - The constant speed multiplier.
         * @return This instance of Params.
         */
        public Params setConstantSpeedModifier(double constantSpeedModifier) {
            this.constantSpeedModifier = constantSpeedModifier;
            return this;
        }

        /**
         * Sets the drivestick button.
         *
         * @param driveStick - The drivestick button.
         * @return This instance of Params.
         */
        public Params setDriveStick(Button driveStick) {
            if(!driveStick.isDouble) {
                throw new NotDoubleInputException("DriveStick must be a double input.");
            }
            buttonsToSet[0] = driveStick;

            return this;
        }

        /**
         * Sets the turnstick button.
         *
         * @param turnStick - The turnstick button.
         * @return This instance of Params.
         */
        public Params setTurnStick(Button turnStick) {
            if(!turnStick.isDouble) {
                throw new NotDoubleInputException("TurnStick must be a double input.");
            }
            buttonsToSet[1] = turnStick;
            return this;
        }

        /**
         * Sets the speed mode button.
         *
         * @param speedModeButton - The speed mode button.
         * @return This instance of Params.
         */
        public Params setSpeedModeButton(Button speedModeButton) {
            if(!speedModeButton.isBoolean) {
                throw new NotBooleanInputException("SpeedModeButton must be a boolean input.");
            }
            buttonsToSet[2] = speedModeButton;
            return this;
        }

        /**
         * Sets the default button values.
         */
        private void setDefaultButtons(){
            buttonsToSet[0] = new Button(1, Button.DoubleInputs.left_stick_y);
            buttonsToSet[1] = new Button(1, Button.DoubleInputs.right_stick_x);
            buttonsToSet[2] = new Button(1, Button.BooleanInputs.noButton);
        }
    }

    /**
     * A parameters class used when you need to pass specific numbers into the drive but still want to use config.
     */
    public static final class NumberParams implements BaseParam {
        //Motor config names to be used in TankDrive to set the motors
        private String leftMotor, rightMotor;
        //Various double values used for speed control.
        private double speedModeModifier = 1, constantSpeedModifier = 1;

        /**
         * Constructor for NumberParams.
         *
         * @param leftMotorConfig - The left motor config name.
         * @param rightMotorConfig - The right motor config name.
         */
        public NumberParams(String leftMotorConfig, String rightMotorConfig) {
            leftMotor = leftMotorConfig;
            rightMotor = rightMotorConfig;
            useSpecific = true;
        }

        /**
         * Set speed mode modifier.
         *
         * @param speedModeModifier - The speed mode modifier.
         * @return This instance of Params.
         */
        public NumberParams setSpeedModeModifier(double speedModeModifier) {
            this.speedModeModifier = speedModeModifier;
            return this;
        }

        /**
         * Set constant speed modifer.
         *
         * @param constantSpeedModifier - The constant speed modifier.
         * @return This instance of Params.
         */
        public NumberParams setConstantSpeedModifier(double constantSpeedModifier) {
            this.constantSpeedModifier = constantSpeedModifier;
            return this;
        }
    }
}