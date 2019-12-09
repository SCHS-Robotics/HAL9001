/*
 * Filename: MechanumDrive.java
 * Author: Dylan Zueck and Cole Savage
 * Team Name: Crow Force, Level Up
 * Date: 9/1/19
 */

package com.SCHSRobotics.HAL9001.system.subsystems;

import com.SCHSRobotics.HAL9001.system.menus.DisplayMenu;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.SubSystem;
import com.SCHSRobotics.HAL9001.util.annotations.AutonomousConfig;
import com.SCHSRobotics.HAL9001.util.annotations.TeleopConfig;
import com.SCHSRobotics.HAL9001.util.control.PIDController;
import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.GuiNotPresentException;
import com.SCHSRobotics.HAL9001.util.exceptions.InvalidMoveCommandException;
import com.SCHSRobotics.HAL9001.util.exceptions.NotAnAlchemistException;
import com.SCHSRobotics.HAL9001.util.exceptions.NotBooleanInputException;
import com.SCHSRobotics.HAL9001.util.exceptions.NotDoubleInputException;
import com.SCHSRobotics.HAL9001.util.exceptions.NotVectorInputException;
import com.SCHSRobotics.HAL9001.util.exceptions.WrongDrivetypeException;
import com.SCHSRobotics.HAL9001.util.functional_interfaces.BiFunction;
import com.SCHSRobotics.HAL9001.util.math.ArrayMath;
import com.SCHSRobotics.HAL9001.util.math.EncoderToDistanceProcessor;
import com.SCHSRobotics.HAL9001.util.math.Units;
import com.SCHSRobotics.HAL9001.util.math.Vector;
import com.SCHSRobotics.HAL9001.util.misc.BaseParam;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.ConfigData;
import com.SCHSRobotics.HAL9001.util.misc.ConfigParam;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.misc.Toggle;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import static java.lang.Math.PI;

/**
 * A built in mechanum drive class with 7 drive modes.
 */
public class MechanumDrive extends SubSystem {

    //Names of all the controls.
    private static final String DRIVESTICK = "drivestick", LEFT_DRIVESTICK = "drivestick_left", RIGHT_DRIVESTICK = "drivestick_right", TURNSTICK = "turnstick", TURN_LEFT = "turn_left", TURN_RIGHT = "turn_right", TTA_STICK = "tta_stick", SPEED_MODE = "speed_mode_toggle", TURN_SPEED_MODE = "turn_speed_mode_toggle";
    //Motors used to control the robot.
    private DcMotorEx topRight, topLeft, botRight, botLeft;
    //Gyroscope used to get the robot's current angle.
    private BNO055IMU imu;
    //The motor config. [0] is top left, [1] is top right, [2] is bottom left, [3] is bottom right.
    private String[] config;
    //Power bias to add or subtract for turning left and right.
    private double turnRightPower, turnLeftPower;
    //The customizable gamepad used to control the robot.
    private CustomizableGamepad inputs;
    //PID controllers for turning to specific angles and driving in a straight line, respectively.
    private PIDController turnPID, stabilityPID;
    //A boolean designating whether or not the drive will use the gyroscope.
    private boolean usesGyro;
    //The number of encoder ticks per meter traveled.
    private double encodersPerMeter;
    //Which IMU the robot should use when configuring the rev hub internal gyroscope.
    private int imuNumber;
    //Speed mode multipliers for use in speed toggle and speed reduction/amplification.
    private double constantSpeedMultiplier, currentSpeedModeMultiplier, slowModeMultiplier;
    //Speed mode multipliers for use in turning speed toggle and turning speed reduction/amplification.
    private double constantTurnSpeedMultiplier, currentTurnSpeedModeMultiplier, slowTurnModeMultiplier;
    //A toggle that turns speed mode on and off.
    private Toggle speedModeToggle, turnSpeedModeToggle;
    //A boolean specifying whether the drive system is using specific values for the configurable settings.
    private static boolean useSpecific = false;
    //Boolean values specifying whether the turn and stability PID controllers use degrees.
    private boolean useDegreesTurn, useDegreesStability;
    //A boolean specifying whether to use a displaymenu to display data.
    private boolean useDisplayMenu;
    //A displaymenu used to display data to the screen.
    private DisplayMenu displayMenu;
    //Specifies the type of drive the user will use.
    public enum DriveType {
        STANDARD, FIELD_CENTRIC, MATTHEW, ARCADE, STANDARD_TTA, FIELD_CENTRIC_TTA, ARCADE_TTA
    }
    private DriveType driveType;

    public enum ReverseType {
        REVERSE_LEFT, REVERSE_RIGHT, REVERSE_FRONT, REVERSE_BACK
    }
    private ReverseType reverseType;

    private DcMotorEx.RunMode runMode;
    private DcMotorEx.ZeroPowerBehavior zeroPowerBehavior;

    /**
     * A constructor for the mechanum drive that takes parameters as input.
     *
     * @param robot  - The robot the drive is currently being used on.
     * @param params - The parameters for the drive.
     */
    public MechanumDrive(Robot robot, Params params) {
        super(robot);

        runMode = params.runMode;
        zeroPowerBehavior = params.zeroPowerBehavior;

        driveType = params.driveType;

        constantSpeedMultiplier = params.constantSpeedMultiplier;
        slowModeMultiplier = params.slowModeMultiplier;
        currentSpeedModeMultiplier = 1;

        constantTurnSpeedMultiplier = params.constantTurnSpeedMultiplier;
        slowTurnModeMultiplier = params.turnSpeedModeMultiplier;
        currentTurnSpeedModeMultiplier = 1;

        usesGyro = params.useGyro;

        //Gyro should only be used if the robot is in field centric mode, one of the turn to angle modes, or explicitly uses the gyroscope.
        if (usesGyro) {
            imu = robot.hardwareMap.get(BNO055IMU.class, params.imuNumber == 1 ? "imu" : "imu 1");
        }
        imuNumber = params.imuNumber;

        turnPID = params.turnPID;
        stabilityPID = params.stabilityPID;

        this.config = params.config.clone();

        topLeft = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[0]);
        topRight = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[1]);
        botLeft = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[2]);
        botRight = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[3]);

        topLeft.setMode(params.runMode);
        topRight.setMode(params.runMode);
        botLeft.setMode(params.runMode);
        botRight.setMode(params.runMode);

        topLeft.setZeroPowerBehavior(zeroPowerBehavior);
        topRight.setZeroPowerBehavior(zeroPowerBehavior);
        botLeft.setZeroPowerBehavior(zeroPowerBehavior);
        botRight.setZeroPowerBehavior(zeroPowerBehavior);

        resetAllEncoders();

        reverseType = params.reverseType;
        switch(reverseType) {
            case REVERSE_LEFT: reverseLeft(); break;
            case REVERSE_RIGHT: reverseRight(); break;
            case REVERSE_FRONT: reverseFront(); break;
            case REVERSE_BACK: reverseBack(); break;
        }

        if (params.changeVelocityPID) {
            topLeft.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp, params.vki, params.vkd, params.vkf));
            topRight.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp, params.vki, params.vkd, params.vkf));
            botLeft.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp, params.vki, params.vkd, params.vkf));
            botRight.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp, params.vki, params.vkd, params.vkf));
        }

        //2*PI*0.05 is theoretical circumference of a gobilda mechanum wheel.
        encodersPerMeter = params.encodersPerMeter > 0 ? params.encodersPerMeter : topLeft.getMotorType().getTicksPerRev()/(2*PI*0.05);

        //Add buttons to controller.
        inputs = new CustomizableGamepad(robot);
        inputs.addButton(DRIVESTICK, params.driveStick);
        inputs.addButton(LEFT_DRIVESTICK, params.driveStickLeft);
        inputs.addButton(RIGHT_DRIVESTICK, params.driveStickRight);
        inputs.addButton(TURNSTICK, params.turnStick);
        inputs.addButton(TURN_LEFT, params.turnLeft);
        inputs.addButton(TURN_RIGHT, params.turnRight);
        inputs.addButton(TTA_STICK, params.ttaStick);
        inputs.addButton(SPEED_MODE, params.speedMode);
        inputs.addButton(TURN_SPEED_MODE, params.turnSpeedMode);

        speedModeToggle = new Toggle(Toggle.ToggleTypes.flipToggle, false);
        turnSpeedModeToggle = new Toggle(Toggle.ToggleTypes.flipToggle, false);

        turnLeftPower = params.turnLeftPower;
        turnRightPower = params.turnRightPower;

        useDegreesTurn = params.useDegreesTurn;
        useDegreesStability = params.useDegreesStability;

        useDisplayMenu = robot.usesGUI();
        if(useDisplayMenu) {
            displayMenu = new DisplayMenu(robot.gui);
            robot.gui.addMenu("Mechanum Display",displayMenu);
        }
    }

    /**
     * A constructor for the mechanum drive that takes parameters as input and uses config.
     *
     * @param robot         - The robot the drive is currently being used on.
     * @param params        - The parameters for the drive.
     * @param usingSpecific - Whether or not specific parameters were used instead of the configuration increment system.
     */
    public MechanumDrive(Robot robot, SpecificParams params, boolean usingSpecific) {
        super(robot);
        usesConfig = true;
        useSpecific = usingSpecific;

        turnLeftPower = params.turnLeftPower;
        turnRightPower = params.turnRightPower;

        constantSpeedMultiplier = params.constantSpeedMultipler;
        slowModeMultiplier = params.slowModeMultiplier;
        currentSpeedModeMultiplier = 1;

        constantTurnSpeedMultiplier = params.constantTurnSpeedMultiplier;
        slowTurnModeMultiplier = params.slowTurnModeMultiplier;
        currentTurnSpeedModeMultiplier = 1;

        usesGyro = false;

        config = params.config.clone();

        topLeft = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[0]);
        topRight = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[1]);
        botLeft = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[2]);
        botRight = (DcMotorEx) robot.hardwareMap.dcMotor.get(params.config[3]);

        resetAllEncoders();

        if (params.changeVelocityPID) {
            topLeft.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp, params.vki, params.vkd, params.vkf));
            topRight.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp, params.vki, params.vkd, params.vkf));
            botLeft.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp, params.vki, params.vkd, params.vkf));
            botRight.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(params.vkp, params.vki, params.vkd, params.vkf));
        }

        //2*PI*0.05 is theoretical circumference of a gobilda mechanum wheel.
        this.encodersPerMeter = params.encodersPerMeter > 0 ? params.encodersPerMeter : topLeft.getMotorType().getTicksPerRev()/(2*PI*0.05);

        speedModeToggle = new Toggle(Toggle.ToggleTypes.flipToggle, false);
        turnSpeedModeToggle = new Toggle(Toggle.ToggleTypes.flipToggle, false);

        stabilityPID = params.stabilityPID;
        turnPID = params.turnPID;

        useDegreesTurn = params.useDegreesTurn;
        useDegreesStability = params.useDegreesStability;

        useDisplayMenu = robot.usesGUI();
        if(useDisplayMenu) {
            displayMenu = new DisplayMenu(robot.gui);
            robot.gui.addMenu("Mechanum Display",displayMenu);
        }
    }

    /**
     * A constructor for MechanumDrive that uses config and default settings.
     *
     * @param robot - The robot the drive is currently being used on.
     * @param topLeft - The top left motor config name.
     * @param topRight - The top right motor config name.
     * @param botLeft - The bottom left motor config name.
     * @param botRight - The bottom right motor config name.
     */
    public MechanumDrive(Robot robot, String topLeft, String topRight, String botLeft, String botRight) {
        this(robot,new SpecificParams(topLeft,topRight,botLeft,botRight), false);
    }

    /**
     * A constructor for MechanumDrive that uses config and encoders per meter.
     *
     * @param robot - The robot the drive is currently being used on.
     * @param topLeft - The top left motor config name.
     * @param topRight - The top right motor config name.
     * @param botLeft - The bottom left motor config name.
     * @param botRight - The bottom right motor config name.
     * @param encodersPerMeter - The number of encoder ticks per meter.
     */
    public MechanumDrive(Robot robot, String topLeft, String topRight, String botLeft, String botRight, double encodersPerMeter) {
        this(robot,new SpecificParams(topLeft,topRight,botLeft,botRight).setEncodersPerMeter(encodersPerMeter), false);
    }

    @Override
    public void init() {

        if(useDisplayMenu) {
            displayMenu.addLine("Calibrating...");
        }
        else {
            robot.telemetry.addLine("Calibrating...");
            robot.telemetry.update();
        }

        if (usesGyro && !usesConfig) {
            imu.initialize(new BNO055IMU.Parameters());
            waitUntil(() -> imu.isGyroCalibrated());
        }

        if(useDisplayMenu) {
            displayMenu.addLine("Done!");
        }
        else {
            robot.telemetry.addLine("Done!");
            robot.telemetry.update();
        }
    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {
        if (usesConfig && robot.isTeleop()) {
            inputs = robot.pullControls(this);
            ConfigData data = robot.pullNonGamepad2(this);

            imuNumber = (int) Math.round(data.getData("ImuNumber",Double.class));

            setDriveMode(data.getData("DriveType", DriveType.class));
            setUseGyro(data.getData("UseGyro",Boolean.class), imuNumber);

            DcMotor.RunMode runMode = data.getData("MotorRunMode",DcMotor.RunMode.class);
            topLeft.setMode(runMode);
            topRight.setMode(runMode);
            botLeft.setMode(runMode);
            botRight.setMode(runMode);

            DcMotor.ZeroPowerBehavior zeroPower = data.getData("MotorZeroPower",DcMotor.ZeroPowerBehavior.class);
            topLeft.setZeroPowerBehavior(zeroPower);
            topRight.setZeroPowerBehavior(zeroPower);
            botLeft.setZeroPowerBehavior(zeroPower);
            botRight.setZeroPowerBehavior(zeroPower);

            reverseType = data.getData("ReverseType",ReverseType.class);

            switch(reverseType) {
                case REVERSE_LEFT: reverseLeft(); break;
                case REVERSE_RIGHT: reverseRight(); break;
                case REVERSE_FRONT: reverseFront(); break;
                case REVERSE_BACK: reverseBack(); break;
            }

            if(!useSpecific) {
                turnLeftPower = data.getData("turnLeftPower",Double.class);
                turnRightPower = data.getData("turnRightPower",Double.class);
                constantSpeedMultiplier = data.getData("ConstantSpeedMultiplier",Double.class);
                slowModeMultiplier = data.getData("SlowModeMultiplier",Double.class);
                constantTurnSpeedMultiplier = data.getData("ConstantTurnSpeedMultiplier",Double.class);
                slowTurnModeMultiplier = data.getData("SlowTurnModeMultiplier",Double.class);
            }
        }
        else if (usesConfig && robot.isAutonomous()) {
            ConfigData data = robot.pullNonGamepad2(this);

            imuNumber = (int) Math.round(data.getData("ImuNumber",Double.class));

            setDriveMode(data.getData("DriveType", DriveType.class));
            setUseGyro(data.getData("UseGyro",Boolean.class), imuNumber);

            DcMotor.RunMode runMode = data.getData("MotorRunMode",DcMotor.RunMode.class);
            topLeft.setMode(runMode);
            topRight.setMode(runMode);
            botLeft.setMode(runMode);
            botRight.setMode(runMode);

            DcMotor.ZeroPowerBehavior zeroPower = data.getData("MotorZeroPower",DcMotor.ZeroPowerBehavior.class);
            topLeft.setZeroPowerBehavior(zeroPower);
            topRight.setZeroPowerBehavior(zeroPower);
            botLeft.setZeroPowerBehavior(zeroPower);
            botRight.setZeroPowerBehavior(zeroPower);

            reverseType = data.getData("ReverseType",ReverseType.class);

            switch(reverseType) {
                case REVERSE_LEFT: reverseLeft(); break;
                case REVERSE_RIGHT: reverseRight(); break;
                case REVERSE_FRONT: reverseFront(); break;
                case REVERSE_BACK: reverseBack(); break;
            }

            if(!useSpecific) {
                constantSpeedMultiplier = data.getData("ConstantSpeedMultiplier",Double.class);
            }
        }
        else if(usesGyro) {
            turnPID.init(0,0);
            stabilityPID.init(0,0);
        }
    }

    @Override
    public void handle() {
        speedModeToggle.updateToggle(inputs.getBooleanInput(SPEED_MODE));
        turnSpeedModeToggle.updateToggle(inputs.getBooleanInput(TURN_SPEED_MODE));

        if (speedModeToggle.getCurrentState()) {
            currentSpeedModeMultiplier = slowModeMultiplier;
        }
        else {
            currentSpeedModeMultiplier = 1;
        }

        if(turnSpeedModeToggle.getCurrentState()) {
            currentTurnSpeedModeMultiplier = slowTurnModeMultiplier;
        }
        else {
            currentTurnSpeedModeMultiplier = 1;
        }

        Vector input = inputs.getVectorInput(DRIVESTICK);

        Vector left = inputs.getVectorInput(LEFT_DRIVESTICK);
        Vector right = inputs.getVectorInput(RIGHT_DRIVESTICK);

        input.scalarMultiply(constantSpeedMultiplier * currentSpeedModeMultiplier);
        left.scalarMultiply(constantSpeedMultiplier * currentSpeedModeMultiplier);
        right.scalarMultiply(constantSpeedMultiplier * currentSpeedModeMultiplier);

        Vector tta = inputs.getVectorInput(TTA_STICK);

        double turnPower = inputs.getDoubleInput(TURNSTICK) * constantTurnSpeedMultiplier * currentTurnSpeedModeMultiplier;
        boolean turnLeft = inputs.getBooleanInput(TURN_LEFT);
        boolean turnRight = inputs.getBooleanInput(TURN_RIGHT);

        double correction, turnCorrection;

        switch (driveType) {

            //Standard vector drive. 1 control for driving, one for turning.
            case STANDARD:
            //Standard vector drive where the front of the robot is fixed.
            case FIELD_CENTRIC:
                correction = usesGyro ? stabilityPID.getCorrection(getCurrentAngle(useDegreesStability ? AngleUnit.DEGREES : AngleUnit.RADIANS)) : 0;

                if ((turnPower != 0 || turnLeft || turnRight) && usesGyro) {
                    stabilityPID.setSetpoint(getCurrentAngle(useDegreesStability ? AngleUnit.DEGREES : AngleUnit.RADIANS));
                    correction = 0;
                }
                if (!turnLeft && !turnRight) {
                    turnAndMove(input,(turnPower*constantTurnSpeedMultiplier*currentTurnSpeedModeMultiplier)-correction);
                } else if (turnLeft) {
                    turnAndMove(input,-turnLeftPower*currentTurnSpeedModeMultiplier);
                } else {
                    turnAndMove(input,turnRightPower*currentTurnSpeedModeMultiplier);
                }
                break;

            //Standard drive, but the turn control is a joystick that tells the robot what angle to turn to.
            case STANDARD_TTA:
            //Standard vector drive where the front of the robot is fixed and the turn control is a joystick that gives the robot an angle to turn to.
            case FIELD_CENTRIC_TTA:
                double angleStability = getCurrentAngle(useDegreesStability ? AngleUnit.DEGREES : AngleUnit.RADIANS);
                double angleTurn = getCurrentAngle(useDegreesTurn ? AngleUnit.DEGREES : AngleUnit.RADIANS);

                correction = stabilityPID.getCorrection(angleStability);
                turnCorrection = turnPID.getCorrection(angleTurn);

                if ((!tta.isZeroVector() || turnLeft || turnRight) && usesGyro) {
                    turnPID.setSetpoint(useDegreesTurn ? Math.toDegrees(tta.theta) : tta.theta);
                    stabilityPID.setSetpoint(angleStability);
                    correction = 0;
                    turnCorrection = 0;
                }

                if (!turnLeft && !turnRight) {
                    turnAndMove(input, -turnCorrection - correction);
                }
                else if (turnLeft) {
                    turnAndMove(input,-turnLeftPower*currentTurnSpeedModeMultiplier);
                }
                else {
                    turnAndMove(input, turnRightPower*currentTurnSpeedModeMultiplier);
                }

                break;

            //Arcade drive.
            case ARCADE:
                correction = usesGyro ? stabilityPID.getCorrection(getCurrentAngle(useDegreesStability ? AngleUnit.DEGREES : AngleUnit.RADIANS)) : 0;

                if ((turnPower != 0 || turnLeft || turnRight) && usesGyro) {
                    stabilityPID.setSetpoint(getCurrentAngle(useDegreesStability ? AngleUnit.DEGREES : AngleUnit.RADIANS));
                    correction = 0;
                }

                if (!turnLeft && !turnRight) {
                    if (input.isZeroVector()) {
                        turn(-turnPower*constantTurnSpeedMultiplier*currentTurnSpeedModeMultiplier);
                    }
                    else {
                        turnAndMove(input,(turnPower*constantTurnSpeedMultiplier*currentTurnSpeedModeMultiplier) - correction);
                    }
                } else if (turnLeft) {
                    if (input.isZeroVector()) {
                        turn(turnLeftPower*currentTurnSpeedModeMultiplier);
                    }
                    else {
                        turnAndMove(input,-turnLeftPower*currentTurnSpeedModeMultiplier);
                    }
                } else {
                    if (input.isZeroVector()) {
                        turn(-turnRightPower*currentTurnSpeedModeMultiplier);
                    }
                    else {
                        turnAndMove(input,turnRightPower*currentTurnSpeedModeMultiplier);
                    }
                }

                break;

            //Arcade drive with turn to angle functionality.
            case ARCADE_TTA:
                double angleStabilityArcade = getCurrentAngle(useDegreesStability ? AngleUnit.DEGREES : AngleUnit.RADIANS);
                double angleTurnArcade = getCurrentAngle(useDegreesTurn ? AngleUnit.DEGREES : AngleUnit.RADIANS);

                correction = stabilityPID.getCorrection(angleStabilityArcade);
                turnCorrection = turnPID.getCorrection(angleTurnArcade);

                if ((!tta.isZeroVector() || turnLeft || turnRight) && usesGyro) {
                    turnPID.setSetpoint(useDegreesTurn ? Math.toDegrees(tta.theta) : tta.theta);
                    stabilityPID.setSetpoint(angleStabilityArcade);
                    correction = 0;
                    turnCorrection = 0;
                }

                if(!turnLeft && !turnRight) {
                    if(input.isZeroVector()) {
                        turn(turnCorrection);
                    }
                    else {
                        turnAndMove(input, -turnCorrection - correction);
                    }
                }
                else if(turnLeft) {
                    if(input.isZeroVector()) {
                        turn(turnLeftPower*currentTurnSpeedModeMultiplier);
                    }
                    else {
                        turnAndMove(input, -turnLeftPower*currentTurnSpeedModeMultiplier);
                    }
                }
                else {
                    if(input.isZeroVector()) {
                        turn(-turnRightPower*currentTurnSpeedModeMultiplier);
                    }
                    else {
                        turnAndMove(input, turnRightPower*currentTurnSpeedModeMultiplier);
                    }
                }

                break;

            //Special driving mode requested by Matthew. Two joysticks, one controlling each side of the robot. Stability PID and turn to angle PID do not matter here.
            case MATTHEW:
                left.scalarMultiply(Math.sqrt(2));
                right.scalarMultiply(Math.sqrt(2));

                left.rotate(-(PI / 4));
                right.rotate(-(PI / 4));

                if (!turnLeft && !turnRight) {
                    double[] powersLeft = new double[] {left.x, left.y};
                    double maxLeft = ArrayMath.max(ArrayMath.abs(powersLeft));
                    ArrayMath.divide(powersLeft, maxLeft > 1 ? maxLeft : 1);

                    double[] powersRight = new double[] {right.x, right.y};
                    double maxRight = ArrayMath.max(ArrayMath.abs(powersRight));
                    ArrayMath.divide(powersLeft, maxRight > 1 ? maxRight : 1);

                    topLeft.setPower(powersLeft[0]);
                    botLeft.setPower(powersLeft[1]);

                    topRight.setPower(powersRight[1]);
                    botRight.setPower(powersRight[0]);
                } else if (turnLeft) {
                    double[] powersLeft = new double[] {left.x - turnLeftPower, left.y - (turnLeftPower*currentTurnSpeedModeMultiplier)};
                    double maxLeft = ArrayMath.max(ArrayMath.abs(powersLeft));
                    ArrayMath.divide(powersLeft, maxLeft > 1 ? maxLeft : 1);

                    double[] powersRight = new double[] {right.x + turnLeftPower, right.y + (turnLeftPower*currentTurnSpeedModeMultiplier)};
                    double maxRight = ArrayMath.max(ArrayMath.abs(powersRight));
                    ArrayMath.divide(powersLeft, maxRight > 1 ? maxRight : 1);

                    topLeft.setPower(powersLeft[0]);
                    botLeft.setPower(powersLeft[1]);

                    topRight.setPower(powersRight[1]);
                    botRight.setPower(powersRight[0]);
                } else {
                    double[] powersLeft = new double[] {left.x + turnRightPower, left.y + (turnRightPower*currentTurnSpeedModeMultiplier)};
                    double maxLeft = ArrayMath.max(ArrayMath.abs(powersLeft));
                    ArrayMath.divide(powersLeft, maxLeft > 1 ? maxLeft : 1);

                    double[] powersRight = new double[] {right.x - turnRightPower, right.y - (turnRightPower*currentTurnSpeedModeMultiplier)};
                    double maxRight = ArrayMath.max(ArrayMath.abs(powersRight));
                    ArrayMath.divide(powersLeft, maxRight > 1 ? maxRight : 1);

                    topLeft.setPower(powersLeft[0]);
                    botLeft.setPower(powersLeft[1]);

                    topRight.setPower(powersRight[1]);
                    botRight.setPower(powersRight[0]);
                }
                break;
        }
    }

    @Override
    public void stop() {

    }

    /**
     * Resets all encoders affiliated with the drive train.
     */
    public void resetAllEncoders() {
        topLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        topRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        botLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        botRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        topLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        topRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        botLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        botRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Stops all the motors.
     */
    public void stopAllMotors() {
        topLeft.setPower(0);
        topRight.setPower(0);
        botLeft.setPower(0);
        botRight.setPower(0);
    }

    /**
     * Sets the motors to the reverse direction.
     */
    public void reverseLeft() {
        topLeft.setDirection(DcMotor.Direction.REVERSE);
        topRight.setDirection(DcMotor.Direction.FORWARD);
        botLeft.setDirection(DcMotor.Direction.REVERSE);
        botRight.setDirection(DcMotor.Direction.FORWARD);
    }

    /**
     * Sets the motors to the forward direction.
     */
    public void reverseRight() {
        topLeft.setDirection(DcMotor.Direction.FORWARD);
        topRight.setDirection(DcMotor.Direction.REVERSE);
        botLeft.setDirection(DcMotor.Direction.FORWARD);
        botRight.setDirection(DcMotor.Direction.REVERSE);
    }

    public void reverseFront() {
        topLeft.setDirection(DcMotor.Direction.REVERSE);
        topRight.setDirection(DcMotor.Direction.REVERSE);
        botLeft.setDirection(DcMotor.Direction.FORWARD);
        botRight.setDirection(DcMotor.Direction.FORWARD);
    }

    public void reverseBack() {
        topLeft.setDirection(DcMotor.Direction.FORWARD);
        topRight.setDirection(DcMotor.Direction.FORWARD);
        botLeft.setDirection(DcMotor.Direction.REVERSE);
        botRight.setDirection(DcMotor.Direction.REVERSE);
    }
    /**
     * Drive method for driving for a certain amount of time with matthew drive.
     *
     * @param leftVector  - The vector for controlling the left side of the robot.
     * @param rightVector - The vector for controlling the right side of the robot.
     * @param timeMs      - The total time to run in ms.
     */
    public void driveTime(Vector leftVector, Vector rightVector, long timeMs) {
        drive(leftVector, rightVector);
        waitTime(timeMs);
        stopAllMotors();
    }

    /**
     * Drive method for driving for a certain distance with matthew drive.
     *
     * @param leftVector    - The left motor vector.
     * @param rightVector   - The right motor vector.
     * @param distanceLeft  - The distance for the left side of the robot to travel.
     * @param distanceRight - The distance for the right side of the robot to travel.
     * @param unit          - The unit that the distance is being provided in.
     */
    public void turnAndMoveDistance(Vector leftVector, Vector rightVector, double distanceLeft, double distanceRight, Units unit) {
        EncoderToDistanceProcessor processor = new EncoderToDistanceProcessor(encodersPerMeter);
        turnAndMoveEncoders(leftVector, rightVector, processor.getEncoderAmount(distanceLeft,unit), processor.getEncoderAmount(distanceRight,unit));
    }

    /**
     * Turn and move at the same time for a certain amount of encoder ticks.
     *
     * @param leftVector - The left motor vector.
     * @param rightVector - The right motor vector.
     * @param encodersLeft - The amount of encoders that the left side of the robot should travel.
     * @param encodersRight - The amount of encoders that the right side of the robot should travel.
     */
    public void turnAndMoveEncoders(Vector leftVector, Vector rightVector, double encodersLeft, double encodersRight) {
            if ((leftVector.isZeroVector() && encodersLeft != 0) || (rightVector.isZeroVector() && encodersRight != 0)) {
                throw new InvalidMoveCommandException("You can't move anywhere if you aren't trying to move ;)");
            }

            if (encodersLeft < 0 || encodersRight < 0) {
                throw new DumpsterFireException("Where you're going, you don't need roads! (distance must be positive)");
            }

            resetAllEncoders();

            Vector leftDisplacement = new Vector(encodersLeft, leftVector.theta, Vector.CoordinateType.POLAR);
            Vector rightDisplacement = new Vector(encodersRight, rightVector.theta, Vector.CoordinateType.POLAR);

            leftVector.scalarMultiply(constantSpeedMultiplier * Math.sqrt(2));
            rightVector.scalarMultiply(constantSpeedMultiplier * Math.sqrt(2));

            leftVector.rotate(-(PI / 4));
            rightVector.rotate(-(PI / 4));

            double[] powersLeft = new double[] {leftVector.x, leftVector.y};
            double maxLeft = ArrayMath.max(ArrayMath.abs(powersLeft));
            ArrayMath.divide(powersLeft, maxLeft > 1 ? maxLeft : 1);

            double[] powersRight = new double[] {rightVector.x, rightVector.y};
            double maxRight = ArrayMath.max(ArrayMath.abs(powersRight));
            ArrayMath.divide(powersLeft, maxRight > 1 ? maxRight : 1);

            leftDisplacement.rotate(-(PI / 4));
            rightDisplacement.rotate(-(PI / 4));

            double thresh1Left = Math.abs(leftDisplacement.x);
            double thresh2Left = Math.abs(leftDisplacement.y);

            double thresh1Right = Math.abs(rightDisplacement.x);
            double thresh2Right = Math.abs(rightDisplacement.y);

            while (robot.opModeIsActive() && ((Math.abs(topLeft.getCurrentPosition()) < thresh1Left && Math.abs(botLeft.getCurrentPosition()) < thresh2Left) || (Math.abs(botRight.getCurrentPosition()) < thresh1Right && Math.abs(topRight.getCurrentPosition()) < thresh2Right))) {
                if (Math.abs(topLeft.getCurrentPosition()) < thresh1Left && Math.abs(botLeft.getCurrentPosition()) < thresh2Left) {
                    topLeft.setPower(powersLeft[0]);
                    botLeft.setPower(powersLeft[1]);
                } else {
                    topLeft.setPower(0);
                    botLeft.setPower(0);
                }
                if (Math.abs(botRight.getCurrentPosition()) < thresh1Right && Math.abs(topRight.getCurrentPosition()) < thresh2Right) {
                    topRight.setPower(powersRight[1]);
                    botRight.setPower(powersRight[0]);
                } else {
                    topRight.setPower(0);
                    botRight.setPower(0);
                }
            }

        stopAllMotors();
    }

    /**
     * Turn and move at the same time for a certain amount of time.
     *
     * @param leftVector - The left motor vector.
     * @param rightVector - The right motor vector.
     * @param timeMs - The time to turn and move in milliseconds.
     */
    public void turnAndMoveTime(Vector leftVector, Vector rightVector, long timeMs) {
        turnAndMove(leftVector,rightVector);
        waitTime(timeMs);
        stopAllMotors();
    }

    /**
     * Turns and moves at the same time.
     *
     * @param left - The left motor vector.
     * @param right - The right motor vector.
     */
    public void turnAndMove(Vector left, Vector right) {
        drive(left, right);
    }

    /**
     * Turn and move at the same time for a certain distance.
     *
     * @param v - The velocity vector of the robot.
     * @param turnPower - The power to turn at.
     * @param distance - The distance to travel.
     * @param unit - The unit of distance.
     */
    public void turnAndMoveDistance(Vector v, double turnPower, double distance, Units unit) {
        EncoderToDistanceProcessor processor = new EncoderToDistanceProcessor(encodersPerMeter);
        turnAndMoveEncoders(v,turnPower,processor.getEncoderAmount(distance,unit));
    }

    /**
     * Turn and move at the same time for a certain amount of encoder ticks.
     *
     * @param v - The velocity vector of the robot.
     * @param turnPower - The power to turn at.
     * @param encoders - The amount of encoder ticks to travel.
     */
    public void turnAndMoveEncoders(Vector v, double turnPower, double encoders) {
        int[] initVals = getEncoderPos();
        turnAndMove(v, turnPower);
        waitWhile(() -> (Math.abs(getTopLeftEncoderPos()-initVals[0]) + Math.abs(getTopRightEncoderPos() - initVals[1]) + Math.abs(getBotLeftEncoderPos() - initVals[2]) + Math.abs(getBotRightEncoderPos() - initVals[3]))/4.0 < encoders);
        stopAllMotors();
    }

    /**
     * Turn and move at the same time for a certain amount of time.
     *
     * @param v - The robot's velocity vector.
     * @param turnPower - The power to turn at.
     * @param timeMs - The amount of time to run in milliseconds.
     */
    public void turnAndMoveTime(Vector v, double turnPower, long timeMs)  {
        turnAndMove(v, turnPower);
        waitTime(timeMs);
        stopAllMotors();
    }

    /**
     * Turn and move at the same time.
     *
     * @param v - The robot's velocity vector.
     * @param turnPower - The power to turn at.
     */
    public void turnAndMove(Vector v, double turnPower) {
        Vector vcpy = v.clone();
        vcpy.scalarMultiply(Math.sqrt(2));

        switch(driveType) {
            case STANDARD:
            case STANDARD_TTA:
                vcpy.rotate(-PI / 4);
                setPower(vcpy.x + turnPower, vcpy.y - turnPower, vcpy.y + turnPower, vcpy.x - turnPower);
                break;
            case ARCADE:
            case ARCADE_TTA:
                if(vcpy.isZeroVector()) {
                    stopAllMotors();
                }
                else if (vcpy.theta < PI / 4 || vcpy.theta > (7 * PI) / 4) { //right side of the square
                    setPower(vcpy.x + turnPower, -vcpy.y - turnPower, -vcpy.y + turnPower, vcpy.x - turnPower);
                }
                else if (vcpy.theta > PI / 4 && vcpy.theta < (3 * PI) / 4) { //top side of the square
                    setPower(vcpy.r + turnPower,vcpy.r - turnPower,vcpy.r + turnPower,vcpy.r - turnPower);
                }
                else if (vcpy.theta > (3 * PI) / 4 && vcpy.theta < (5 * PI) / 4) { //left side of the square
                    setPower(-vcpy.r + turnPower,vcpy.r - turnPower,vcpy.r + turnPower,-vcpy.r - turnPower);
                }
                else if (vcpy.theta > (5 * PI) / 4 && vcpy.theta < (7 * PI) / 4) { //Bottom side of the square
                    setPower(-vcpy.r + turnPower,-vcpy.r - turnPower,-vcpy.r + turnPower,-vcpy.r - turnPower);
                }
                break;
            case FIELD_CENTRIC:
            case FIELD_CENTRIC_TTA:
                vcpy.rotate(-((PI / 4) + getCurrentAngle()));
                setPower(vcpy.x + turnPower, vcpy.y - turnPower, vcpy.y + turnPower, vcpy.x - turnPower);
                break;
        }

    }

    /**
     * Makes the robot move and/or turn. This is only used if the drive is being controlled matthew-style.
     *
     * @param leftVector  - The left input vector.
     * @param rightVector - The right input vector.
     */
    public void drive(Vector leftVector, Vector rightVector) {

        leftVector.scalarMultiply(constantSpeedMultiplier * Math.sqrt(2));
        rightVector.scalarMultiply(constantSpeedMultiplier * Math.sqrt(2));

        leftVector.rotate(-(PI / 4));
        rightVector.rotate(-(PI / 4));

        double[] powersLeft = new double[] {leftVector.x, leftVector.y};
        double maxLeft = ArrayMath.max(ArrayMath.abs(powersLeft));
        ArrayMath.divide(powersLeft, maxLeft > 1 ? maxLeft : 1);

        double[] powersRight = new double[] {rightVector.x, rightVector.y};
        double maxRight = ArrayMath.max(ArrayMath.abs(powersRight));
        ArrayMath.divide(powersLeft, maxRight > 1 ? maxRight : 1);

        topLeft.setPower(powersLeft[0]);
        botLeft.setPower(powersLeft[1]);

        topRight.setPower(powersRight[1]);
        botRight.setPower(powersRight[0]);
    }


    /**
     * Makes the robot move for a certain amount of time. Use this for any non-matthew drive mode.
     *
     * @param v                - Makes the robot move a certain distance. Use this for any non-matthew drive mode.
     * @param timeMs           - The amount of time in ms the robot should move.
     * @param stabilityControl - Whether the robot should use stability control.
     */
    public void driveTime(Vector v, long timeMs, boolean stabilityControl) {
        if (stabilityControl) {
            stabilityPID.setSetpoint(getCurrentAngle(useDegreesStability ? AngleUnit.DEGREES : AngleUnit.RADIANS));
        }
        waitTime(timeMs, () -> drive(v,stabilityControl));
        stopAllMotors();
    }

    /**
     * Makes the robot move for a certain amount of time. Use this for any non-matthew drive mode.
     *
     * @param v      - The direction and power that the robot should move at.
     * @param timeMs - The time in ms that the robot should move for.
     */
    public void driveTime(Vector v, long timeMs) {
        driveTime(v, timeMs, false);
    }

    /**
     * Makes the robot move a certain distance. Use this for any non-matthew drive mode.
     *
     * @param v                - The direction and power that the robot should move at.
     * @param distance         - The distance the robot should travel.
     * @param unit             - The unit of distance the robot should travel.
     * @param stabilityControl - Whether the robot should use stability control.
     */
    public void driveDistance(Vector v, double distance, Units unit, boolean stabilityControl) {
        EncoderToDistanceProcessor processor = new EncoderToDistanceProcessor(encodersPerMeter);
        driveEncoders(v,processor.getEncoderAmount(distance,unit),stabilityControl);
    }

    /**
     * Makes the robot drive a specified distance in a specified direction.
     *
     * @param v        - The input velocity vector.
     * @param distance - The distance the robot should travel.
     * @param unit     - The units of distance.
     */
    public void driveDistance(Vector v, double distance, Units unit) {
        driveDistance(v, distance, unit, false);
    }

    /**
     * Drive a certain number of encoder ticks.
     *
     * @param v - The input velocity vector.
     * @param encoders - The amount of encoder ticks to travel.
     * @param stabilityControl - Whether or not to use the stability PID.
     */
    public void driveEncoders(Vector v, double encoders, boolean stabilityControl) {
        if (v.isZeroVector() && encoders != 0) {
            throw new InvalidMoveCommandException("You can't move anywhere if you aren't trying to move ;)");
        }
        if (encoders < 0) {
            throw new DumpsterFireException("Where you're going, you don't need roads! (encoders must be positive)");
        }

        Vector displacement = new Vector(encoders, v.theta, Vector.CoordinateType.POLAR);

        double thresh1;
        double thresh2;

        int[] initVals = getEncoderPos();

        if (stabilityControl) {
            stabilityPID.setSetpoint(getCurrentAngle(useDegreesStability ? AngleUnit.DEGREES : AngleUnit.RADIANS));
        }

        switch (driveType) {
            case STANDARD_TTA:
            case STANDARD:

                displacement.rotate(-(PI / 4));

                thresh1 = Math.abs(displacement.x);
                thresh2 = Math.abs(displacement.y);

                waitWhile(() -> Math.abs(getTopLeftEncoderPos() - initVals[0]) < thresh1 && Math.abs(getTopRightEncoderPos() - initVals[1]) < thresh2 && Math.abs(getBotLeftEncoderPos() - initVals[2]) < thresh2 && Math.abs(getBotRightEncoderPos() - initVals[3]) < thresh1,
                        () -> drive(v,stabilityControl));
                break;
            case FIELD_CENTRIC_TTA:
            case FIELD_CENTRIC:
                if (!usesGyro) {
                    throw new WrongDrivetypeException("Field Centric Drive Must uses the IMU but the IMU was never set up");
                }

                displacement.rotate(-((PI / 4) + getCurrentAngle()));

                thresh1 = Math.abs(displacement.x);
                thresh2 = Math.abs(displacement.y);

                waitWhile(() -> Math.abs(getTopLeftEncoderPos() - initVals[0]) < thresh1 && Math.abs(getTopRightEncoderPos() - initVals[1]) < thresh2 && Math.abs(getBotLeftEncoderPos() - initVals[2]) < thresh2 && Math.abs(getBotRightEncoderPos() - initVals[3]) < thresh1,
                        () -> drive(v,stabilityControl));
                break;
            case ARCADE_TTA:
            case ARCADE:

                double thresh = encoders * Math.sqrt(2) / 2;

                waitWhile(() -> Math.abs(getTopLeftEncoderPos() - initVals[0]) < thresh && Math.abs(getTopRightEncoderPos() - initVals[1]) < thresh && Math.abs(getBotLeftEncoderPos() - initVals[2]) < thresh && Math.abs(getBotRightEncoderPos() - initVals[3]) < thresh,
                        () -> drive(v,stabilityControl));
                break;
        }

        stopAllMotors();
    }

    /**
     * Drive a certain number of encoder ticks.
     *
     * @param v - The input velocity vector.
     * @param encoders - The amount of encoder ticks to travel.
     */
    public void driveEncoders(Vector v, double encoders) {
        driveEncoders(v,encoders,false);
    }

    /**
     * Makes the robot move. Use this for any non-matthew drive mode. You must set the stability control target manually for this to work with stability control.
     *
     * @param v - The direction and power that the robot should move at.
     * @param stabilityControl - Whether or not to use the drive's stability control system.
     */
    public void drive(Vector v, boolean stabilityControl){

        Vector vcpy = v.clone();

        vcpy.scalarMultiply(constantSpeedMultiplier * Math.sqrt(2));

        double correction = stabilityControl && usesGyro ? stabilityPID.getCorrection(getCurrentAngle(useDegreesStability ? AngleUnit.DEGREES : AngleUnit.RADIANS)) : 0;

        switch (driveType) {
            case STANDARD_TTA:
            case STANDARD:
                vcpy.rotate(-(PI / 4));
                setPower(vcpy.x - correction, vcpy.y + correction, vcpy.y - correction, vcpy.x + correction);

                break;
            case FIELD_CENTRIC_TTA:
            case FIELD_CENTRIC:
                if(!usesGyro) {
                    throw new WrongDrivetypeException("Field Centric Drive Must uses the IMU but the IMU was never set up");
                }
                vcpy.rotate(-((PI / 4) + getCurrentAngle()));
                setPower(vcpy.x - correction, vcpy.y + correction, vcpy.y - correction, vcpy.x + correction);
                break;
            case ARCADE_TTA:
            case ARCADE:
                if (vcpy.isZeroVector()) {
                    stopAllMotors();
                }
                else if (vcpy.theta < PI / 4 || vcpy.theta > (7 * PI) / 4) { //right side of the square
                    setPower(vcpy.r - correction, -vcpy.r + correction, -vcpy.r - correction, vcpy.r + correction);
                } else if (vcpy.theta > PI / 4 && vcpy.theta < (3 * PI) / 4) { //top side of the square
                    setPower(vcpy.r - correction, vcpy.r + correction, vcpy.r - correction, vcpy.r + correction);
                } else if (vcpy.theta > (3 * PI) / 4 && vcpy.theta < (5 * PI) / 4) { //left side of the square
                    setPower(-vcpy.r - correction, vcpy.r + correction, vcpy.r - correction, -vcpy.r + correction);
                } else if (vcpy.theta > (5 * PI) / 4 && vcpy.theta < (7 * PI) / 4) { //Bottom side of the square
                    setPower(-vcpy.r - correction, -vcpy.r + correction, -vcpy.r - correction, -vcpy.r + correction);
                }
                break;
        }
    }

    /**
     * Makes the robot move. Use this for any non-matthew drive mode. You must set the stability control target manually for this to work with stability control.
     *
     * @param v - The direction vector indicating how the robot should move.
     */
    public void drive(Vector v) {
        drive(v,false);
    }

    /**
     * Turns for a certain amount of time.
     *
     * @param turnPower - The power to turn at.
     * @param timeMs - The time to turn in milliseconds.
     */
    public void turnTime(double turnPower, long timeMs){
        turn(turnPower);
        waitTime(timeMs);
        stopAllMotors();
    }

    /**
     * Turn a certain distance.
     *
     * @param turnPower - The power to turn at.
     * @param distance - The distance to turn.
     * @param unit - The unit of distance.
     */
    public void turnDistance(double turnPower, double distance, Units unit) {
        EncoderToDistanceProcessor processor = new EncoderToDistanceProcessor(encodersPerMeter);
        double encoders = Math.abs(processor.getEncoderAmount(distance,unit));
        turnEncoders(turnPower,encoders);
    }

    /**
     * Turn a certain number of encoder ticks.
     *
     * @param turnPower - The power to turn at.
     * @param encoders - The number of encoder ticks to turn.
     */
    public void turnEncoders(double turnPower, double encoders) {
        if (encoders < 0) {
            throw new DumpsterFireException("Where you're going, you don't need roads! (encoders must be positive)");
        }

        int[] initVals = getEncoderPos();
        turn(turnPower);
        waitWhile(() -> (Math.abs(getTopLeftEncoderPos() - initVals[0]) + Math.abs(getTopRightEncoderPos() - initVals[1]) + Math.abs(getBotLeftEncoderPos() - initVals[2]) + Math.abs(getBotRightEncoderPos() - initVals[3]))/4.0 < encoders);
        stopAllMotors();
    }

    /**
     * Makes the robot turn.
     *
     * @param turnPower - The power to turn at.
     */
    public void turn(double turnPower) {
        topLeft.setPower(Range.clip(-turnPower,-1,1));
        topRight.setPower(Range.clip(turnPower,-1,1));
        botLeft.setPower(Range.clip(-turnPower,-1,1));
        botRight.setPower(Range.clip(turnPower,-1,1));
    }

    /**
     * Turns to a specified angle within a specified tolerance.
     *
     * @param angle - The angle to turn to.
     * @param tolerance - The tolerance that the angle must be within.
     */
    public void turnTo(double angle, double tolerance) {
        if(!usesGyro) {
            throw new GuiNotPresentException("turnTo must use a gyroscope");
        }
        double prevDeadband = turnPID.deadband;
        turnPID.setDeadband(tolerance);
        turnPID.setSetpoint(angle);
        double correction = 1;
        //TODO replace with waitTime statement
        while(robot.opModeIsActive() && correction != 0) {
            correction = turnPID.getCorrection(getCurrentAngle(useDegreesTurn ? AngleUnit.DEGREES : AngleUnit.RADIANS));
            turn(correction);
        }
        turnPID.setDeadband(prevDeadband);
        stopAllMotors();
    }

    public void turnTo(double angle) {
        turnTo(angle, turnPID.deadband);
    }

    /**
     * Gets the current encoder position of the top left motor.
     *
     * @return The current encoder position of the top left motor.
     */
    public int getTopLeftEncoderPos() {
        return topLeft.getCurrentPosition();
    }

    /**
     * Gets the current encoder position of the top right motor.
     *
     * @return The current encoder position of the top right motor.
     */
    public int getTopRightEncoderPos() {
        return topRight.getCurrentPosition();
    }

    /**
     * Gets the current encoder position of the bottom left motor.
     *
     * @return The current encoder position of the bottom left motor.
     */
    public int getBotLeftEncoderPos() {
        return botLeft.getCurrentPosition();
    }

    /**
     * Gets the current encoder position of the bottom right motor.
     *
     * @return The current encoder position of the bottom right motor.
     */
    public int getBotRightEncoderPos() {
        return botRight.getCurrentPosition();
    }

    /**
     * Gets an array of length 4 representing the current encoder position of all 4 motors. [0] is top left, [1] is top right, [2] is bottom left, [3] is bottom right
     *
     * @return An array of length 4 representing the current encoder position of all 4 motors.
     */
    public int[] getEncoderPos() {
        return new int[] {topLeft.getCurrentPosition(), topRight.getCurrentPosition(), botLeft.getCurrentPosition(), botRight.getCurrentPosition()};
    }

    /**
     * Updates the mechanum drive's mode.
     *
     * @param driveType - The driving mode that the drivetrain will be set to.
     */
    public void setDriveMode(DriveType driveType) {
        boolean useGyro = driveType == DriveType.STANDARD_TTA || driveType == DriveType.FIELD_CENTRIC || driveType == DriveType.FIELD_CENTRIC_TTA || driveType == DriveType.ARCADE_TTA;
        setUseGyro(useGyro, imuNumber);
        this.driveType = driveType;
    }

    /**
     * Sets the gyroscope mode.
     *
     * @param useGyro - Whether or not to use the gyroscope.
     * @param imuNumber - The imu number referring to which IMU is being used as a gyro. It must be either 1 or 2.
     */
    public void setUseGyro(boolean useGyro, int imuNumber) {
        if(!usesGyro && useGyro) {
            imu = robot.hardwareMap.get(BNO055IMU.class,imuNumber == 1 ? "imu" : "imu 1");
            imu.initialize(new BNO055IMU.Parameters());
            waitUntil(() -> imu.isGyroCalibrated());
        }
        else if(usesGyro) {
            double angle = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle;
            turnPID.init(angle, angle);
            stabilityPID.init(angle, angle);
        }
        usesGyro = useGyro;
    }

    /**
     * Gets the motor config.
     *
     * @return - The motor config.
     */
    public String[] getConfig() {
        return config;
    }

    /**
     * Sets the scaled powers of all 4 motors.
     *
     * @param topLeftPower - The top left motor power.
     * @param topRightPower - The top right motor power.
     * @param botLeftPower - The top left motor power.
     * @param botRightPower - The bottom right motor power.
     */
    public void setPower(double topLeftPower, double topRightPower, double botLeftPower, double botRightPower) {
        double[] powers = new double[] {topLeftPower, topRightPower, botLeftPower, botRightPower};
        double max = ArrayMath.max(ArrayMath.abs(powers));
        ArrayMath.divide(powers, max > 1 ? max : 1);

        topLeft.setPower(powers[0]);
        topRight.setPower(powers[1]);
        botLeft.setPower(powers[2]);
        botRight.setPower(powers[3]);
    }

    public void setTopLeftPower(double power) {
        topLeft.setPower(power);
    }

    public void setTopRightPower(double power) {
        topRight.setPower(power);
    }

    public void setBotLeftPower(double power) {
        botLeft.setPower(power);
    }

    public void setBotRightPower(double power) {
        botRight.setPower(power);
    }

    public void setTopLeftMode(DcMotor.RunMode mode) {
        topLeft.setMode(mode);
    }

    public void setTopRightMode(DcMotor.RunMode mode) {
        topRight.setMode(mode);
    }

    public void setBotLeftMode(DcMotor.RunMode mode) {
        botLeft.setMode(mode);
    }

    public void setBotRightMode(DcMotor.RunMode mode) {
        botRight.setMode(mode);
    }

    public void setTopLeftZeroMode(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        topLeft.setZeroPowerBehavior(zeroPowerBehavior);
    }

    public void setTopRightZeroMode(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        topRight.setZeroPowerBehavior(zeroPowerBehavior);
    }

    public void setBotLeftZeroMode(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        botLeft.setZeroPowerBehavior(zeroPowerBehavior);
    }

    public void setBotRightZeroMode(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        botRight.setZeroPowerBehavior(zeroPowerBehavior);
    }

    public double getCurrentAngle(AngleUnit angleUnit) {
        return usesGyro ? imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,angleUnit).firstAngle : 0;
    }

    public double getCurrentAngle() {
        return getCurrentAngle(AngleUnit.RADIANS);
    }

    public double getCurrentPitch(AngleUnit angleUnit) {
        return usesGyro ? imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,angleUnit).secondAngle : 0;
    }

    public double getCurrentPitch() {
        return getCurrentPitch(AngleUnit.RADIANS);
    }

    public double getCurrentRoll(AngleUnit angleUnit) {
        return usesGyro ? imu.getAngularOrientation(AxesReference.INTRINSIC,AxesOrder.ZYX,angleUnit).secondAngle : 0;
    }

    public double getCurrentRoll() {
        return getCurrentRoll(AngleUnit.RADIANS);
    }

    public Orientation getOrientation(AngleUnit angleUnit, AxesOrder order, AxesReference reference) {
        return usesGyro ? imu.getAngularOrientation(reference,order,angleUnit) : new Orientation();
    }

    public Orientation getOrientation(AngleUnit angleUnit, AxesOrder order) {
        return getOrientation(angleUnit,order,AxesReference.INTRINSIC);
    }

    public Orientation getOrientation(AngleUnit angleUnit, AxesReference reference) {
        return getOrientation(angleUnit,AxesOrder.ZYX,reference);
    }

    public Orientation getOrientation(AxesOrder order, AxesReference reference) {
        return getOrientation(AngleUnit.RADIANS, order, reference);
    }

    public Orientation getOrientation(AngleUnit angleUnit) {
        return getOrientation(angleUnit,AxesOrder.ZYX,AxesReference.INTRINSIC);
    }

    public Orientation getOrientation(AxesOrder order) {
        return getOrientation(AngleUnit.RADIANS,order,AxesReference.INTRINSIC);
    }

    public Orientation getOrientation(AxesReference reference) {
        return getOrientation(AngleUnit.RADIANS,AxesOrder.ZYX,reference);
    }

    public Orientation getOrientation() {
        return getOrientation(AngleUnit.RADIANS,AxesOrder.ZYX,AxesReference.INTRINSIC);
    }

    /**
     * Teleop configuration settings.
     *
     * @return The teleop configuration.
     */
    @TeleopConfig
    public static ConfigParam[] teleopConfig() {
        if(useSpecific) {
            return new ConfigParam[]{
                    new ConfigParam("DriveType", DriveType.STANDARD),
                    new ConfigParam(DRIVESTICK, Button.VectorInputs.right_stick),
                    new ConfigParam(LEFT_DRIVESTICK, Button.VectorInputs.noButton),
                    new ConfigParam(RIGHT_DRIVESTICK, Button.VectorInputs.noButton),
                    new ConfigParam(TURNSTICK, Button.DoubleInputs.left_stick_x),
                    new ConfigParam(TURN_LEFT, Button.BooleanInputs.noButton),
                    new ConfigParam(TURN_RIGHT, Button.BooleanInputs.noButton),
                    new ConfigParam(TTA_STICK, Button.VectorInputs.noButton),
                    new ConfigParam(SPEED_MODE, Button.BooleanInputs.noButton),
                    new ConfigParam(TURN_SPEED_MODE, Button.BooleanInputs.noButton),
                    new ConfigParam("ReverseType", ReverseType.REVERSE_RIGHT),
                    new ConfigParam("UseGyro", ConfigParam.booleanMap, false),
                    new ConfigParam("ImuNumber", ConfigParam.numberMap(1, 2, 1), 1),
                    new ConfigParam("MotorRunMode", new DcMotor.RunMode[] {
                            DcMotor.RunMode.RUN_USING_ENCODER,
                            DcMotor.RunMode.RUN_WITHOUT_ENCODER},
                            DcMotor.RunMode.RUN_USING_ENCODER),
                    new ConfigParam("MotorZeroPower", DcMotor.ZeroPowerBehavior.BRAKE)
            };
        }
        else {
            return new ConfigParam[]{
                    new ConfigParam("DriveType", DriveType.STANDARD),
                    new ConfigParam(DRIVESTICK, Button.VectorInputs.right_stick),
                    new ConfigParam(LEFT_DRIVESTICK, Button.VectorInputs.noButton),
                    new ConfigParam(RIGHT_DRIVESTICK, Button.VectorInputs.noButton),
                    new ConfigParam(TURNSTICK, Button.DoubleInputs.left_stick_x),
                    new ConfigParam(TURN_LEFT, Button.BooleanInputs.noButton),
                    new ConfigParam(TURN_RIGHT, Button.BooleanInputs.noButton),
                    new ConfigParam(TTA_STICK, Button.VectorInputs.noButton),
                    new ConfigParam(SPEED_MODE, Button.BooleanInputs.noButton),
                    new ConfigParam(TURN_SPEED_MODE, Button.BooleanInputs.noButton),
                    new ConfigParam("turnLeftPower", ConfigParam.numberMap(0, 1, 0.05), 0.3),
                    new ConfigParam("turnRightPower", ConfigParam.numberMap(0, 1, 0.05), 0.3),
                    new ConfigParam("ReverseType", ReverseType.REVERSE_RIGHT),
                    new ConfigParam("UseGyro", ConfigParam.booleanMap, false),
                    new ConfigParam("ImuNumber", ConfigParam.numberMap(1, 2, 1), 1),
                    new ConfigParam("ConstantSpeedMultiplier", ConfigParam.numberMap(0, 10, 0.05), 1.0),
                    new ConfigParam("SlowModeMultiplier", ConfigParam.numberMap(0, 10, 0.05), 1.0),
                    new ConfigParam("ConstantTurnSpeedMultiplier", ConfigParam.numberMap(0, 10, 0.05), 1.0),
                    new ConfigParam("SlowTurnModeMultiplier", ConfigParam.numberMap(0, 10, 0.05), 1.0),
                    new ConfigParam("MotorRunMode", new DcMotor.RunMode[] {
                            DcMotor.RunMode.RUN_USING_ENCODER,
                            DcMotor.RunMode.RUN_WITHOUT_ENCODER},
                            DcMotor.RunMode.RUN_USING_ENCODER),
                    new ConfigParam("MotorZeroPower", DcMotor.ZeroPowerBehavior.BRAKE)
            };
        }
    }

    /**
     * Autonomous configuration settings.
     *
     * @return The teleop configuration.
     */
    @AutonomousConfig
    public static ConfigParam[] autonomousConfig() {
        if(useSpecific) {
            return new ConfigParam[]{
                    new ConfigParam("DriveType", DriveType.STANDARD),
                    new ConfigParam("UseGyro", ConfigParam.booleanMap, false),
                    new ConfigParam("ImuNumber", ConfigParam.numberMap(1, 2, 1), 1),
                    new ConfigParam("MotorRunMode", new DcMotor.RunMode[] {
                            DcMotor.RunMode.RUN_USING_ENCODER,
                            DcMotor.RunMode.RUN_WITHOUT_ENCODER},
                            DcMotor.RunMode.RUN_USING_ENCODER),
                    new ConfigParam("MotorZeroPower", DcMotor.ZeroPowerBehavior.BRAKE),
                    new ConfigParam("ReverseType", ReverseType.REVERSE_RIGHT)
            };
        }
        else {
            return new ConfigParam[]{
                    new ConfigParam("DriveType", DriveType.STANDARD),
                    new ConfigParam("UseGyro", ConfigParam.booleanMap, false),
                    new ConfigParam("ImuNumber", ConfigParam.numberMap(1, 2, 1), 1),
                    new ConfigParam("ConstantSpeedMultiplier", ConfigParam.numberMap(0, 1, 0.05), 1.0),
                    new ConfigParam("MotorRunMode", new DcMotor.RunMode[] {
                            DcMotor.RunMode.RUN_USING_ENCODER,
                            DcMotor.RunMode.RUN_WITHOUT_ENCODER},
                            DcMotor.RunMode.RUN_USING_ENCODER),
                    new ConfigParam("MotorZeroPower", DcMotor.ZeroPowerBehavior.BRAKE),
                    new ConfigParam("ReverseType", ReverseType.REVERSE_RIGHT)
            };
        }
    }

    /**
     * A parameter class for passing all desired options into mechanum drive.
     */
    public static final class Params implements BaseParam {
        //The driveType of the mechanum drive.
        private DriveType driveType;
        //The buttons used in the various drive modes.
        private Button driveStick, driveStickLeft, driveStickRight, turnStick, turnLeft, turnRight, ttaStick, speedMode, turnSpeedMode;
        //The motor config.
        private String[] config;
        //The imu number being used for the gyroscope.
        private int imuNumber;
        //The turning power bias for left and right turns.
        private double turnLeftPower, turnRightPower;
        //The PID controllers for turning to angles and stability, respectively.
        private PIDController turnPID, stabilityPID;
        //A boolean specifying whether or not to use the gyroscope.
        private boolean useGyro;
        //A boolean specifying whether to change the motor velocity PID.
        private boolean changeVelocityPID;
        //The new velocity PID coefficients.
        private double vkp, vki, vkd, vkf;
        //The number of encoder ticks per meter traveled.
        private double encodersPerMeter;
        //The constants used to scale the drive's speed and to change the robot's speed in speed mode.
        private double constantSpeedMultiplier, slowModeMultiplier;
        //The constants used to scale the drive's turn speed and to change the robot's turn speed in turn speed mode.
        private double constantTurnSpeedMultiplier, turnSpeedModeMultiplier;
        //Boolean values specifying whether or not degrees should be used for the stability and turn PID controllers.
        private boolean useDegreesStability, useDegreesTurn;
        private DcMotorEx.RunMode runMode;
        private DcMotorEx.ZeroPowerBehavior zeroPowerBehavior;
        private ReverseType reverseType;

        /**
         * A constructor for the parameters object. Sets default parameter values.
         *
         * @param topLeft  - The top left motor config name.
         * @param topRight - The top right motor config name.
         * @param botLeft  - The bottom left motor config name.
         * @param botRight - The bottom right motor config name.
         */
        public Params(String topLeft, String topRight, String botLeft, String botRight) {

            config = new String[4];
            config[0] = topLeft;
            config[1] = topRight;
            config[2] = botLeft;
            config[3] = botRight;

            this.driveType = DriveType.STANDARD;

            driveStick = new Button(1, Button.VectorInputs.right_stick);
            driveStickLeft = new Button(1, Button.VectorInputs.noButton);
            driveStickRight = new Button(1, Button.VectorInputs.noButton);
            turnStick = new Button(1, Button.DoubleInputs.left_stick_x);
            turnLeft = new Button(1, Button.BooleanInputs.noButton);
            turnRight = new Button(1, Button.BooleanInputs.noButton);
            ttaStick = new Button(1, Button.VectorInputs.noButton);
            speedMode = new Button(1, Button.BooleanInputs.noButton);
            turnSpeedMode = new Button(1, Button.BooleanInputs.noButton);

            imuNumber = 1;

            turnLeftPower = 0;
            turnRightPower = 0;

            useGyro = false;

            changeVelocityPID = false;

            turnPID = new PIDController(0, 0, 0);
            stabilityPID = new PIDController(0, 0, 0);

            encodersPerMeter = -1;

            constantSpeedMultiplier = 1;
            slowModeMultiplier = 1;

            constantTurnSpeedMultiplier = 1;
            turnSpeedModeMultiplier = 1;

            useDegreesStability = false;
            useDegreesTurn = false;

            runMode = DcMotorEx.RunMode.RUN_USING_ENCODER;
            zeroPowerBehavior = DcMotorEx.ZeroPowerBehavior.BRAKE;

            reverseType = ReverseType.REVERSE_RIGHT;
        }

        /**
         * Sets the drive type to be used.
         *
         * @param driveType - The driveType that will be used.
         * @return - This instance of Params.
         */
        public Params setDriveType(DriveType driveType) {
            this.driveType = driveType;
            useGyro = driveType == DriveType.STANDARD_TTA || driveType == DriveType.FIELD_CENTRIC || driveType == DriveType.FIELD_CENTRIC_TTA || driveType == DriveType.ARCADE_TTA;
            return this;
        }

        /**
         * Sets the driving input button. Must be a vector input.
         *
         * @param driveStick - The vector input used to control the robot.
         * @return - This instance of Params.
         * @throws NotVectorInputException - Throws this if the input button is not a vector input.
         */
        public Params setDriveStick(Button driveStick) {
            if (!driveStick.isVector) {
                throw new NotVectorInputException("DriveStick must be a vector input");
            }
            this.driveStick = driveStick;
            return this;
        }

        /**
         * Sets the driving input button for the left side of the robot. Must be a vector input.
         *
         * @param driveStickLeft - The vector input used to control the left side of the robot.
         * @return - This instance of Params.
         * @throws NotVectorInputException - Throws this if input button is not a vector input.
         */
        public Params setDriveStickLeft(Button driveStickLeft) {
            if (!driveStickLeft.isVector) {
                throw new NotVectorInputException("DriveStickLeft must be a vector input.");
            }
            this.driveStickLeft = driveStickLeft;
            return this;
        }

        /**
         * Sets the driving input button for the right side of the robot. Must be a vector input.
         *
         * @param driveStickRight - The vector input used to control the right side of the robot.
         * @return - This instance of Params.
         * @throws NotVectorInputException - Throws this if input button is not a vector input.
         */
        public Params setDriveStickRight(Button driveStickRight) {
            if (!driveStickRight.isVector) {
                throw new NotVectorInputException("DriveStickRight must be a vector input.");
            }
            this.driveStickRight = driveStickRight;
            return this;
        }

        /**
         * Sets the turning input button. Must be a double input.
         *
         * @param turnStick - The double input used to control the robot's turning speed.
         * @return - This instance of Params.
         * @throws NotDoubleInputException - Throws this if the input button is not a double input.
         */
        public Params setTurnStick(Button turnStick) {
            if (!turnStick.isDouble) {
                throw new NotDoubleInputException("TurnStick must be a double input.");
            }
            this.turnStick = turnStick;
            return this;
        }

        /**
         * Sets the turn to angle input button. Must be a vector input.
         *
         * @param ttaStick - The vector input used to control what angle the robot turns to.
         * @return - This instance of Params.
         * @throws NotVectorInputException - Throws this is the input button is not a vector input.
         */
        public Params setTTAStick(Button ttaStick) {
            if (!driveStickRight.isVector) {
                throw new NotVectorInputException("TTA Stick must be a vector input.");
            }
            this.ttaStick = ttaStick;
            return this;
        }

        /**
         * Sets left turn button input. Must be a boolean input.
         *
         * @param turnLeft  - The button used to make the robot turn left.
         * @param turnSpeed - The speed at which the robot should turn left when the button is pressed.
         * @return - This instance of Params.
         * @throws NotBooleanInputException - Throws this if the input button is not a boolean input.
         */
        public Params setTurnLeftButton(Button turnLeft, double turnSpeed) {
            if (!turnLeft.isBoolean) {
                throw new NotBooleanInputException("TurnLeft button must be a boolean input.");
            }
            this.turnLeft = turnLeft;
            turnLeftPower = Range.clip(turnSpeed, 0, 1);
            return this;
        }

        /**
         * Sets right turn button input. Must be a boolean input.
         *
         * @param turnRight - The button used to make the robot turn right.
         * @param turnSpeed - The speec at which the robot should turn right when the button is pressed.
         * @return - This instance of Params.
         * @throws NotBooleanInputException - Throws this if the input button is not a boolean input.
         */
        public Params setTurnRightButton(Button turnRight, double turnSpeed) {
            if (!turnRight.isBoolean) {
                throw new NotBooleanInputException("TurnRight button must be a boolean input");
            }
            this.turnRight = turnRight;
            turnRightPower = Range.clip(turnSpeed, 0, 1);
            return this;
        }

        /**
         * Sets the button used to activate/deactivate speed mode.
         *
         * @param speedMode - The speed mode button.
         * @return This instance of Params.
         */
        public Params setSpeedModeButton(Button speedMode) {
            if (!speedMode.isBoolean) {
                throw new NotBooleanInputException("SpeedMode button must be a boolean input");
            }
            this.speedMode = speedMode;
            return this;
        }

        /**
         * Sets the button used to toggle speed mode.
         *
         * @param turnSpeedMode - The button that will be used to toggle turn speed mode.
         * @return - This instance of Params.
         */
        public Params setTurnSpeedModeButton(Button turnSpeedMode) {
            if (!speedMode.isBoolean) {
                throw new NotBooleanInputException("TurnSpeedMode button must be a boolean input");
            }
            this.turnSpeedMode = turnSpeedMode;
            return this;
        }

        /**
         * Sets the number imu for the drive system to use.
         *
         * @param imuNumber - The imu's number. Must be either 1 or 2.
         * @return - This instance of Params.
         * @throws NotAnAlchemistException - Throws this if the imu number is not 1 or 2. Can't make something out of nothing.
         */
        public Params setImuNumber(int imuNumber) {
            if (imuNumber != 1 && imuNumber != 2) {
                throw new NotAnAlchemistException("IMU number must be either 1 or 2");
            }
            this.imuNumber = imuNumber;
            return this;
        }

        /**
         * Sets the coefficients for the turnPID controller.
         *
         * @param kp - Proportional gain.
         * @param ki - Integral gain.
         * @param kd - Derivative gain.
         * @return - This instance of Params.
         */
        public Params setTurnPIDCoeffs(double kp, double ki, double kd) {
            return setTurnPIDCoeffs(kp, ki, kd, false);
        }

        /**
         * Sets the coefficients for the turnPID controller.
         *
         * @param kp         - Proportional gain.
         * @param ki         - Integral gain.
         * @param kd         - Derivative gain.
         * @param useDegrees - A boolean specifying if the units are in degrees.
         * @return - This instance of Params.
         */
        public Params setTurnPIDCoeffs(double kp, double ki, double kd, boolean useDegrees) {
            return setTurnPIDCoeffs(kp,ki,kd,0.05,useDegrees);
        }

        public Params setTurnPIDCoeffs(double kp, double ki, double kd, double deadband) {
            return setTurnPIDCoeffs(kp,ki,kd,deadband,false);
        }

        public Params setTurnPIDCoeffs(double kp, double ki, double kd, double deadband, boolean useDegrees){
            useGyro = true;
            useDegreesTurn = useDegrees;
            turnPID = new PIDController(kp, ki, kd, (Double target, Double current) -> {
                BiFunction<Double, Double, Double> mod = (Double x, Double m) -> (x % m + m) % m;

                double m = useDegrees ? 360 : 2 * PI;

                //cw - ccw +
                double cw = -mod.apply(mod.apply(current, m) - mod.apply(target, m), m);
                double ccw = mod.apply(mod.apply(target, m) - mod.apply(current, m), m);

                return Math.abs(ccw) < Math.abs(cw) ? ccw : cw;
            });
            turnPID.setDeadband(deadband);
            return this;
        }

        /**
         * Sets the PID controller used for turning to specific angles.
         *
         * @param turnPID - The PID to use for turning to specific angles.
         * @return This instance of Params.
         */
        public Params setTurnPID(PIDController turnPID) {
            return setTurnPID(turnPID, false);
        }

        /**
         * Sets the PID controller used for turning to specific angles.
         *
         * @param turnPID    - The PID to use for turning to specific angles.
         * @param useDegrees - Whether the PID controller uses degrees.
         * @return This instance of Params.
         */
        public Params setTurnPID(PIDController turnPID, boolean useDegrees) {
            useGyro = true;
            useDegreesTurn = useDegrees;
            this.turnPID = turnPID;
            return this;
        }

        /**
         * Sets the coefficients for the stability PID controller.
         *
         * @param kp - Proportional gain.
         * @param ki - Integral gain.
         * @param kd - Derivative gain.
         * @return - This instance of Params.
         */
        public Params setStabilityPIDCoeffs(double kp, double ki, double kd) {
            return setStabilityPIDCoeffs(kp, ki, kd, false);
        }

        public Params setStabilityPIDCoeffs(double kp, double ki, double kd, boolean useDegrees) {
            return setStabilityPIDCoeffs(kp,ki,kd,0,useDegrees);
        }

        public Params setStabilityPIDCoeffs(double kp, double ki, double kd, double deadband) {
            return setStabilityPIDCoeffs(kp,ki,kd,deadband,false);
        }

        /**
         * Sets the coefficients for the stabilityPID controller.
         *
         * @param kp         - Proportional gain.
         * @param ki         - Integral gain.
         * @param kd         - Derivative gain.
         * @param useDegrees - A boolean specifying if the units are in degrees.
         * @return - This instance of Params.
         */
        public Params setStabilityPIDCoeffs(double kp, double ki, double kd, double deadband, boolean useDegrees) {
            useGyro = true;
            useDegreesStability = useDegrees;
            stabilityPID = new PIDController(kp, ki, kd, (Double target, Double current) -> {
                BiFunction<Double, Double, Double> mod = (Double x, Double m) -> (x % m + m) % m;

                double m = useDegrees ? 360 : 2 * PI;

                //cw - ccw +
                double cw = -mod.apply(mod.apply(current, m) - mod.apply(target, m), m);
                double ccw = mod.apply(mod.apply(target, m) - mod.apply(current, m), m);

                return Math.abs(ccw) < Math.abs(cw) ? ccw : cw;
            });
            stabilityPID.setDeadband(deadband);
            return this;
        }

        /**
         * Sets the PID controller used for stability control.
         *
         * @param stabilityPID - The PID to use for stability control.
         * @return This instance of Params.
         */
        public Params setStabilityPID(PIDController stabilityPID) {
            return setStabilityPID(stabilityPID, false);
        }

        /**
         * Sets the PID controller that will be used for stability control.
         *
         * @param stabilityPID - The PID controller that will be used for stability control.
         * @param useDegrees   - Whether or not the PID controller uses degrees.
         * @return This instance of SpecificParams.
         */
        public Params setStabilityPID(PIDController stabilityPID, boolean useDegrees) {
            useGyro = true;
            useDegreesStability = useDegrees;
            this.stabilityPID = stabilityPID;
            return this;
        }

        /**
         * Sets the velocity PID coefficients.
         *
         * @param kp - Proportional gain.
         * @param ki - Integral gain.
         * @param kd - Derivative gain.
         * @param kf - Feedforward gain.
         * @return - This instance of Params.
         */
        public Params setVelocityPID(double kp, double ki, double kd, double kf) {
            changeVelocityPID = true;
            vkp = kp;
            vki = ki;
            vkd = kd;
            vkf = kf;
            return this;
        }

        /**
         * Sets the number of encoder ticks per meter distance traveled.
         *
         * @param encodersPerMeter - The number of encoder ticks per meter distance traveled.
         * @return - This instance of Params.
         */
        public Params setEncodersPerMeter(double encodersPerMeter) {
            this.encodersPerMeter = encodersPerMeter;
            return this;
        }

        /**
         * Sets a constant speed multiplier to scale the calculated linear velocities by.
         *
         * @param constantSpeedModifier - The value to multiply the speed by.
         * @return - This instance of Params.
         */
        public Params setConstantSpeedModifier(double constantSpeedModifier) {
            this.constantSpeedMultiplier = constantSpeedModifier;
            return this;
        }

        /**
         * Sets the multiplier that will be applied when speed mode is entered. If the multiplier is < 1 then the robot will slow down, otherwise it will speed up.
         *
         * @param speedModeMultiplier - The multiplier that will be applied when speed mode is entered.
         * @return - This instance of Params.
         */
        public Params setSpeedModeMultiplier(double speedModeMultiplier) {
            this.slowModeMultiplier = speedModeMultiplier;
            return this;
        }

        /**
         * Sets a constant multiplier that will be applied to every speed while turning.
         *
         * @param constantTurnSpeedMultiplier - The constant turn speed multiplier.
         * @return - This instance of params.
         */
        public Params setConstantTurnSpeedMultiplier(double constantTurnSpeedMultiplier) {
            this.constantTurnSpeedMultiplier = constantTurnSpeedMultiplier;
            return this;
        }

        /**
         * Sets the multiplier that will be applied to the turn speed when turn speed mode is activated.
         *
         * @param turnSpeedModeMultiplier - The turn speed mode multiplier.
         * @return - This instance of params.
         */
        public Params setTurnSpeedModeMultiplier(double turnSpeedModeMultiplier) {
            this.turnSpeedModeMultiplier = turnSpeedModeMultiplier;
            return this;
        }

        public Params setMotorRunMode(DcMotor.RunMode runMode) {
            this.runMode = runMode;
            return this;
        }

        public Params setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
            zeroPowerBehavior = behavior;
            return this;
        }
        public Params setReverseType(ReverseType reverseType) {
            this.reverseType = reverseType;
            return this;
        }
    }

    /**
     * A class used for entering necessary params while in config mode or for using more specific versions of params than can be set with the config.
     */
    public static final class SpecificParams {
        //An array of length 4 containing the motor config. [0] = topLeft, [1] = topRight, [2] = bottomLeft, [3] = bottomRight.
        private String[] config;
        //Constants used in various parts of mechanum drive.
        private double encodersPerMeter, turnLeftPower, turnRightPower, constantSpeedMultipler, slowModeMultiplier, constantTurnSpeedMultiplier, slowTurnModeMultiplier;
        //Velocity PID coefficients.
        private double vkp, vki, vkd, vkf;
        //A boolean specifying if the velocity PID was changed.
        private boolean changeVelocityPID;
        //Two PID controllers used to stabilize linear motion and to turn to specific angles.
        private PIDController stabilityPID, turnPID;
        //Boolean value specifying whether or not degrees should be used for the turn and stability PID controller.
        private boolean useDegreesStability, useDegreesTurn;

        /**
         * A constructor for SpecificParams.
         *
         * @param topLeft - The top left motor config name.
         * @param topRight - The top right motor config name.
         * @param botLeft - The bottom left motor config name.
         * @param botRight - The bottom right motor config name
         */
        public SpecificParams(String topLeft, String topRight, String botLeft, String botRight) {
            config = new String[4];
            config[0] = topLeft;
            config[1] = topRight;
            config[2] = botLeft;
            config[3] = botRight;

            vkp = 0;
            vki = 0;
            vkd = 0;
            vkf = 0;
            changeVelocityPID = false;

            turnPID = new PIDController(0,0,0);
            stabilityPID = new PIDController(0,0,0);

            useDegreesTurn = false;
            useDegreesStability = false;

            encodersPerMeter = -1;

            turnLeftPower = 0.5;
            turnRightPower = 0.5;

            constantSpeedMultipler = 1;
            slowModeMultiplier = 1;

            constantTurnSpeedMultiplier = 1;
            slowTurnModeMultiplier = 1;
        }

        /**
         * Sets the number of encoders ticks per meter distance.
         *
         * @param encodersPerMeter - The number of encoders ticks per meter distance
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setEncodersPerMeter(double encodersPerMeter) {
            this.encodersPerMeter = encodersPerMeter;
            return this;
        }

        /**
         * Set the turn left power.
         *
         * @param turnLeftPower - The turn left power.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setTurnLeftPower(double turnLeftPower) {
            this.turnLeftPower = turnLeftPower;
            return this;
        }

        /**
         * Set the turn right power.
         *
         * @param turnRightPower - The turn right power.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setTurnRightPower(double turnRightPower) {
            this.turnRightPower = turnRightPower;
            return this;
        }

        /**
         * Set the constant speed multiplier.
         *
         * @param constantSpeedMultipler - The constant speed multiplier. Always multiplied by velocity.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setConstantSpeedMultipler(double constantSpeedMultipler) {
            this.constantSpeedMultipler = constantSpeedMultipler;
            return this;
        }

        /**
         * Set the slow mode multiplier.
         *
         * @param slowModeMultiplier - The slow mode multiplier. Applied to velocity when in slow/speed mode.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setSlowModeMultiplier(double slowModeMultiplier) {
            this.slowModeMultiplier = slowModeMultiplier;
            return this;
        }

        /**
         * Sets a constant multiplier that will be applied to every speed while turning.
         *
         * @param constantTurnSpeedMultiplier - The constant turn speed multiplier.
         * @return - This instance of params.
         */
        public SpecificParams setConstantTurnSpeedMultiplier(double constantTurnSpeedMultiplier) {
            this.constantTurnSpeedMultiplier = constantTurnSpeedMultiplier;
            return this;
        }

        /**
         * Sets the multiplier that will be applied to the turn speed when turn speed mode is activated.
         *
         * @param slowTurnModeMultiplier - The turn speed mode multiplier.
         * @return - This instance of params.
         */
        public SpecificParams setSlowTurnModeMultiplier(double slowTurnModeMultiplier) {
            this.slowTurnModeMultiplier = slowTurnModeMultiplier;
            return this;
        }

        /**
         * Set the coefficients for the motors' velocity PID.
         *
         * @param kp - Proportional gain for velocity PID.
         * @param ki - Integral gain for velocity PID.
         * @param kd - Derivative gain for velocity PID.
         * @param kf - Feedforward gain for velocity PID.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setVelocityPID(double kp, double ki, double kd, double kf) {
            changeVelocityPID = true;
            vkp = kp;
            vki = ki;
            vkd = kd;
            vkf = kf;
            return this;
        }

        /**
         * Set the coefficients for the turn PID.
         *
         * @param kp - Proportional gain for velocity PID.
         * @param ki - Integral gain for velocity PID.
         * @param kd - Derivative gain for velocity PID.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setTurnPIDCoeffs(double kp, double ki, double kd) {
            return setTurnPIDCoeffs(kp, ki, kd, false);
        }

        public SpecificParams setTurnPIDCoeffs(double kp, double ki, double kd, boolean useDegrees) {
            return setTurnPIDCoeffs(kp,ki,kd,0.05,useDegrees);
        }

        public SpecificParams setTurnPIDCoeffs(double kp, double ki, double kd, double deadband) {
            return setTurnPIDCoeffs(kp,ki,kd,deadband,false);
        }

        /**
         * Sets the coefficients for the turnPID controller.
         *
         * @param kp - Proportional gain.
         * @param ki - Integral gain.
         * @param kd - Derivative gain.
         * @param useDegrees - A boolean specifying if the units are in degrees.
         * @return - This instance of Params.
         */
        public SpecificParams setTurnPIDCoeffs(double kp, double ki, double kd, double deadband, boolean useDegrees) {
            useDegreesTurn = useDegrees;
            turnPID = new PIDController(kp, ki, kd, (Double target, Double current) -> {
                BiFunction<Double, Double, Double> mod = (Double x, Double m) -> (x % m + m) % m;

                double m = useDegrees ? 360 : 2*PI;

                //cw - ccw +
                double cw = -mod.apply(mod.apply(current,m) - mod.apply(target,m), m);
                double ccw = mod.apply(mod.apply(target,m) - mod.apply(current,m), m);

                return Math.abs(ccw) < Math.abs(cw) ? ccw : cw;
            });
            turnPID.setDeadband(deadband);
            return this;
        }

        /**
         * Sets the PID controller to use for turning to specific angles.
         *
         * @param turnPID - The PID controller that will be used for turning.
         * @return This instance of SpecificParams.
         */
        public SpecificParams setTurnPID(PIDController turnPID) {
            return setTurnPID(turnPID, false);
        }

        /**
         * Sets the PID controller to use for turning to specific angles.
         *
         * @param turnPID - The PID controller that will be used for turning.
         * @param useDegrees- Whether or not the PID controller uses degrees.
         * @return This instance of SpecificParams.
         */
        public SpecificParams setTurnPID(PIDController turnPID, boolean useDegrees) {
            useDegreesTurn = useDegrees;
            this.turnPID = turnPID;
            return this;
        }

        /**
         * Sets the coefficients for the stabilityPID controller.
         *
         * @param kp - Proportional gain.
         * @param ki - Integral gain.
         * @param kd - Derivative gain.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setStabilityPIDCoeffs(double kp, double ki, double kd) {
            return setStabilityPIDCoeffs(kp, ki, kd, false);
        }

        public SpecificParams setStabilityPIDCoeffs(double kp, double ki, double kd, boolean useDegrees) {
            return setStabilityPIDCoeffs(kp,ki,kd,0,useDegrees);
        }

        public SpecificParams setStabilityPIDCoeffs(double kp, double ki, double kd, double deadband) {
            return setStabilityPIDCoeffs(kp,ki,kd,deadband,false);
        }

        /**
         * Sets the coefficients for the stability PID controller.
         *
         * @param kp - Proportional gain.
         * @param ki - Integral gain.
         * @param kd - Derivative gain.
         * @param useDegrees - A boolean specifying if the units are in degrees.
         * @return - This instance of SpecificParams.
         */
        public SpecificParams setStabilityPIDCoeffs(double kp, double ki, double kd, double deadband, boolean useDegrees) {
            useDegreesStability = useDegrees;
            stabilityPID = new PIDController(kp, ki, kd, (Double target, Double current) -> {
                BiFunction<Double, Double, Double> mod = (Double x, Double m) -> (x % m + m) % m;

                double m = useDegrees ? 360 : 2*PI;

                //cw - ccw +
                double cw = -mod.apply(mod.apply(current,m) - mod.apply(target,m), m);
                double ccw = mod.apply(mod.apply(target,m) - mod.apply(current,m), m);

                return Math.abs(ccw) < Math.abs(cw) ? ccw : cw;
            });
            stabilityPID.setDeadband(deadband);
            return this;
        }

        /**
         * Sets the PID controller that will be used for stability control.
         *
         * @param stabilityPID - The PID controller that will be used for stability control.
         * @return This instance of SpecificParams.
         */
        public SpecificParams setStabilityPID(PIDController stabilityPID) {
            return setStabilityPID(stabilityPID, false);
        }

        /**
         * Sets the PID controller that will be used for stability control.
         *
         * @param stabilityPID - The PID controller that will be used for stability control.
         * @param useDegrees - Whether or not the PID controller uses degrees.
         * @return This instance of SpecificParams.
         */
        public SpecificParams setStabilityPID(PIDController stabilityPID, boolean useDegrees) {
            useDegreesStability = useDegrees;
            this.stabilityPID = stabilityPID;
            return this;
        }
    }
}