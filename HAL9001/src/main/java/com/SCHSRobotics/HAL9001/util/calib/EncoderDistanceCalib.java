package com.SCHSRobotics.HAL9001.util.calib;

import com.SCHSRobotics.HAL9001.system.menus.DisplayMenu;
import com.SCHSRobotics.HAL9001.system.menus.EncoderDistanceCalibMenu;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.SubSystem;
import com.SCHSRobotics.HAL9001.system.subsystems.MechanumDrive;
import com.SCHSRobotics.HAL9001.system.subsystems.OmniWheelDrive;
import com.SCHSRobotics.HAL9001.system.subsystems.QuadWheelDrive;
import com.SCHSRobotics.HAL9001.system.subsystems.TankDrive;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.exceptions.GuiNotPresentException;
import com.SCHSRobotics.HAL9001.util.exceptions.NotAnAlchemistException;
import com.SCHSRobotics.HAL9001.util.math.Units;
import com.SCHSRobotics.HAL9001.util.math.Vector;
import com.SCHSRobotics.HAL9001.util.misc.BaseParam;
import com.SCHSRobotics.HAL9001.util.misc.Button;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

/**
 * Finds the number of encoder ticks per meter that can be used in the encoder.
 * 
 * BeforeHAL: Wow im so bad at coding. I only use one constructor in my classes.
 * AfterHAL: Wow im so good at coding. I only used one constructor in this class.
 *
 * @author Cole Savage, Level Up
 * @author Dylan Zueck, Crow Force
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 9/1/19
 */
@SuppressWarnings("unused")
public class EncoderDistanceCalib extends SubSystem {

    //The drivetrain subsystem being used.
    private SubSystem driveSubSystem;
    //How far the user measured the robot to move.
    private double distance;
    //The unit of distance being entered.
    private Units unit;
    //Map of motor names to encoder positions at the start and end of the program.
    private LinkedHashMap<String, Integer> startEncoderPos, endingEncoderPos;
    //The entry speed mode button.
    private Button switchSpeedButton;

    /**
     * An enum representing the DriveTrain being used.
     */
    public enum DriveTrain {
        TANK_DRIVE, QUAD_WHEEL_DRIVE, MECHANUM_DRIVE, OMNIWHEEL_DRIVE
    }
    private DriveTrain driveTrain;

    /**
     * An enum representing the state of the calibration program.
     */
    private enum State{
        RUNNING, DISPLAYING, DONE
    }
    private State state = State.RUNNING;

    /**
     * Constructor for EncoderDistanceCalib.
     *
     * @param robot The robot using this subsystem.
     * @param driveTrain The drivetrain being used.
     * @param unit The unit of distance to enter.
     * @param params The drivetrain params to use to create the drivetrain.
     * @param switchSpeedButton The speed mode button for distance entry.
     *
     * @throws GuiNotPresentException Throws this exception if the GUI is not activated on the robot.
     * @throws NotAnAlchemistException Throws this exception if the provided parameters are not from a drivetrain.
     */
    public EncoderDistanceCalib(@NotNull Robot robot, @NotNull DriveTrain driveTrain, @NotNull Units unit, @NotNull BaseParam params, @NotNull Button switchSpeedButton) {
        super(robot);

        this.unit = unit;
        this.switchSpeedButton = switchSpeedButton;

        startEncoderPos = new LinkedHashMap<>();
        endingEncoderPos = new LinkedHashMap<>();

        if(!robot.usesGUI()){
            throw new GuiNotPresentException("ColorspaceCalib requires a GUI to correctly run");
        }

        this.driveTrain = driveTrain;

        switch (driveTrain) {
            case TANK_DRIVE:
                if (!(params instanceof TankDrive.Params)) {
                    throw new NotAnAlchemistException("Given param must be a param from passed DriveTrain");
                }
                driveSubSystem = new TankDrive(robot, (TankDrive.Params) params);
                break;
            case MECHANUM_DRIVE:
                if (!(params instanceof MechanumDrive.Params)) {
                    throw new NotAnAlchemistException("Given param must be a param from passed DriveTrain");
                }
                driveSubSystem = new MechanumDrive(robot, (MechanumDrive.Params) params);
                break;
            case OMNIWHEEL_DRIVE:
            if(!(params instanceof OmniWheelDrive.Params)){
                throw new NotAnAlchemistException("Given param must be a param from passed DriveTrain");
            }
            driveSubSystem = new OmniWheelDrive(robot, (OmniWheelDrive.Params) params);
                break;
            case QUAD_WHEEL_DRIVE:
                if (!(params instanceof QuadWheelDrive.Params)) {
                    throw new NotAnAlchemistException("Given param must be a param from passed DriveTrain");
                }
                driveSubSystem = new QuadWheelDrive(robot, (QuadWheelDrive.Params) params);
                break;
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        switch (driveTrain){
            case TANK_DRIVE:
                startEncoderPos.put("Left", ((TankDrive) driveSubSystem).getLeftMotorEncoderPos());
                startEncoderPos.put("Right", ((TankDrive) driveSubSystem).getRightMotorEncoderPos());
                break;
            case MECHANUM_DRIVE:
                startEncoderPos.put("BotLeft", ((MechanumDrive) driveSubSystem).getBotLeftEncoderPos());
                startEncoderPos.put("BotRight", ((MechanumDrive) driveSubSystem).getBotRightEncoderPos());
                startEncoderPos.put("TopLeft", ((MechanumDrive) driveSubSystem).getTopLeftEncoderPos());
                startEncoderPos.put("TopRight", ((MechanumDrive) driveSubSystem).getTopRightEncoderPos());
                break;
            case OMNIWHEEL_DRIVE:
                startEncoderPos.put("BotLeft", ((OmniWheelDrive) driveSubSystem).getBotLeftEncoderPos());
                startEncoderPos.put("BotRight", ((OmniWheelDrive) driveSubSystem).getBotRightEncoderPos());
                startEncoderPos.put("TopLeft", ((OmniWheelDrive) driveSubSystem).getTopLeftEncoderPos());
                startEncoderPos.put("TopRight", ((OmniWheelDrive) driveSubSystem).getTopRightEncoderPos());
                break;
            case QUAD_WHEEL_DRIVE:
                startEncoderPos.put("BotLeft", ((QuadWheelDrive) driveSubSystem).getBotLeftMotorEncoderPos());
                startEncoderPos.put("BotRight", ((QuadWheelDrive) driveSubSystem).getBotRightMotorEncoderPos());
                startEncoderPos.put("TopLeft", ((QuadWheelDrive) driveSubSystem).getTopLeftMotorEncoderPos());
                startEncoderPos.put("TopRight", ((QuadWheelDrive) driveSubSystem).getTopRightMotorEncoderPos());
                break;
        }
    }

    @Override
    public void handle() {
        if(state == State.RUNNING) {
            switch (driveTrain) {
                case TANK_DRIVE:
                    usingTankDrive();
                    break;
                case MECHANUM_DRIVE:
                    usingMechanumDrive();
                    break;
                case OMNIWHEEL_DRIVE:
                    usingOmniWheelDrive();
                    break;
                case QUAD_WHEEL_DRIVE:
                    usingQuadWheelDrive();
                    break;
            }
        }
        else if(state == State.DISPLAYING) {
            DisplayMenu displayMenu1 = new DisplayMenu(robot.gui);
            robot.gui.addMenu("DisplayMenu1", displayMenu1);
            for (String key: startEncoderPos.keySet()) {
                Integer end = endingEncoderPos.get(key);
                Integer start = startEncoderPos.get(key);
                ExceptionChecker.assertNonNull(end, new NullPointerException("Ending encoder value was null."));
                ExceptionChecker.assertNonNull(start, new NullPointerException("Starting encoder value was null."));
                displayMenu1.addData(key, (end - start)/distance);
            }
        }
    }

    @Override
    public void stop() {
    }

    /**
     * Drive forward for 2 seconds using tank drive.
     */
    private void usingTankDrive() {
        ((TankDrive) driveSubSystem).driveTime(2000, 1);
        waitTime(100);
        endingEncoderPos.put("Left", ((TankDrive) driveSubSystem).getLeftMotorEncoderPos());
        endingEncoderPos.put("Right", ((TankDrive) driveSubSystem).getRightMotorEncoderPos());
        robot.gui.addMenu("Getting Menu", new EncoderDistanceCalibMenu(robot.gui, unit, switchSpeedButton, this));
        robot.gui.setActiveMenu("Getting Menu");
        state = State.DISPLAYING;
    }

    /**
     * Drive forward for 2 seconds using mechanum drive.
     */
    private void usingMechanumDrive() {
        ((MechanumDrive) driveSubSystem).driveTime(new Vector(0,1), 2000);
        waitTime(100);
        endingEncoderPos.put("BotLeft", ((MechanumDrive) driveSubSystem).getBotLeftEncoderPos());
        endingEncoderPos.put("BotRight", ((MechanumDrive) driveSubSystem).getBotRightEncoderPos());
        endingEncoderPos.put("TopLeft", ((MechanumDrive) driveSubSystem).getTopLeftEncoderPos());
        endingEncoderPos.put("TopRight", ((MechanumDrive) driveSubSystem).getTopRightEncoderPos());
        robot.gui.addMenu("Getting Menu", new EncoderDistanceCalibMenu(robot.gui, unit, switchSpeedButton, this));
        robot.gui.setActiveMenu("Getting Menu");
        state = State.DISPLAYING;
    }

    /**
     * Drive forward for 2 seconds using omniwheel drive.
     */
    private void usingOmniWheelDrive() {
        ((OmniWheelDrive) driveSubSystem).driveTime(new Vector(0,1), 2000);
        waitTime(100);
        endingEncoderPos.put("BotLeft", ((MechanumDrive) driveSubSystem).getBotLeftEncoderPos());
        endingEncoderPos.put("BotRight", ((MechanumDrive) driveSubSystem).getBotRightEncoderPos());
        endingEncoderPos.put("TopLeft", ((MechanumDrive) driveSubSystem).getTopLeftEncoderPos());
        endingEncoderPos.put("TopRight", ((MechanumDrive) driveSubSystem).getTopRightEncoderPos());
        robot.gui.addMenu("Getting Menu", new EncoderDistanceCalibMenu(robot.gui, unit, switchSpeedButton, this));
        robot.gui.setActiveMenu("Getting Menu");
        state = State.DISPLAYING;
    }

    /**
     * Drive forward for 2 seconds using quad wheel drive.
     */
    private void usingQuadWheelDrive() {
        ((QuadWheelDrive) driveSubSystem).driveTime(2000, 1);
        waitTime(100);
        endingEncoderPos.put("BotLeft", ((QuadWheelDrive) driveSubSystem).getBotLeftMotorEncoderPos());
        endingEncoderPos.put("BotRight", ((QuadWheelDrive) driveSubSystem).getBotRightMotorEncoderPos());
        endingEncoderPos.put("TopLeft", ((QuadWheelDrive) driveSubSystem).getTopLeftMotorEncoderPos());
        endingEncoderPos.put("TopRight", ((QuadWheelDrive) driveSubSystem).getTopRightMotorEncoderPos());
        robot.gui.addMenu("Getting Menu", new EncoderDistanceCalibMenu(robot.gui, unit, switchSpeedButton, this));
        robot.gui.setActiveMenu("Getting Menu");
        state = State.DISPLAYING;
    }

    /**
     * Closes menu and gets the distance from the user.
     *
     * @param distance The distance entered by the user.
     */
    public void numberSelected(double distance){
        this.distance = distance;
        state = State.DISPLAYING;
        robot.gui.removeMenu("Getting Menu");
    }
}
