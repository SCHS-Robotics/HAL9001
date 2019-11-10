package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.subsystems.MechanumDrive;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class BasicSampleBot extends Robot {

    public TestVisionSystem vision;

    /**
     * Constructor for robot.
     *
     * @param opMode - The opmode the robot is currently running.
     */
    public BasicSampleBot(OpMode opMode) {
        super(opMode);

        vision = new TestVisionSystem(this);
        //sets the drive subSystem to tank drive. This one uses default params
        //drive = new TankDrive(this, new TankDrive.Params("MotorConfigLeft", "MotorConfigRight"));
        //drive = new MechanumDrive(this,new MechanumDrive.Params("forwardLeftMotor","forwardRightMotor","backLeftMotor","backRightMotor"));

        /*
        This is an example of how to setup TankDrive without using default params. Use .set(setting to set) to change a setting from default. Otherwise it will stay default.
        Remember to import button if uncomment this (try alt+enter)

        drive = new TankDrive(this,
                new TankDrive.Params("MotorConfigLeft", "MotorConfigRight")
                        .setConstantSpeedModifier(.5)
                        .setDriveStick(new Button(1,Button.DoubleInputs.right_stick_y))
        );
         */

        putSubSystem("Tank", vision);
    }
}