package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.subsystems.MechanumDrive;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class BasicSampleBot2 extends Robot {

    public MechanumDrive drive;
    public SubSystem1 s1;
    public SubSystem2 s2;
    public SubSystem3 s3;
    /**
     * Constructor for robot.
     *
     * @param opMode - The opmode the robot is currently running.
     */
    public BasicSampleBot2(OpMode opMode) {
        super(opMode);

        //vision = new TestVisionSystem(this);
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

        //startGui(new Button(1, Button.BooleanInputs.noButton));
        //enableViewport(new Button(1, Button.BooleanInputs.noButton));
        //PIDController controller = new PIDController(0.01,0,0);
        //controller.setDeadband(5);
        //calib = new AnglePIDTunerSystem(this, new MechanumDrive.Params("forwardLeftMotor","forwardRightMotor","backLeftMotor","backRightMotor"), controller,45, AngleUnit.DEGREES);
        drive = new MechanumDrive(this, new MechanumDrive.SpecificParams("topLeft","topRight","botLeft","botRight"),false);
        s1 = new SubSystem1(this);
        s2 = new SubSystem2(this);
        s3 = new SubSystem3(this);
        putSubSystem("Tank", drive);
        putSubSystem("Test",s1);
        putSubSystem("Test2",s2);
        putSubSystem("Test3",s3);
    }
}