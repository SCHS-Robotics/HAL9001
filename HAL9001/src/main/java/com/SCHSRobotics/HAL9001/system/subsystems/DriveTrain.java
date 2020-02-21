package com.SCHSRobotics.HAL9001.system.subsystems;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.SubSystem;
import com.SCHSRobotics.HAL9001.util.math.EncoderToDistanceProcessor;
import com.SCHSRobotics.HAL9001.util.math.Units;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class DriveTrain extends SubSystem {

    protected String[] config;
    protected DcMotor[] motors;
    protected Kinematics kinematics;
    protected Localizer localizer;
    protected double encodersPerMeter;
    protected EncoderToDistanceProcessor distProcessor;
    protected CustomizableGamepad gamepad;

    public DriveTrain(@NotNull Robot robot, @NotNull Params params) {
        super(robot);
        config = params.config;
        motors = params.motors;
        kinematics = params.kinematics;
        localizer = params.localizer;
        encodersPerMeter = params.encodersPerMeter;
        distProcessor = new EncoderToDistanceProcessor(params.encodersPerMeter);

        localizer.setEncoders(motors);

        setAllMotorRunModes(DcMotor.RunMode.RUN_USING_ENCODER);
        setAllZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        resetEncoders();
        
        gamepad = new CustomizableGamepad(robot);
    }

    public void reverseMotor(int motorIdx) {
        DcMotor motor = getMotor(motorIdx);
        motor.setDirection(motor.getDirection() == DcMotor.Direction.FORWARD ? DcMotor.Direction.REVERSE : DcMotor.Direction.FORWARD);
    }

    public void setMotorDirection(int motorIdx, DcMotor.Direction direction) {
        getMotor(motorIdx).setDirection(direction);
    }

    public void setMotorRunMode(int motorIdx, DcMotor.RunMode runMode) {
        getMotor(motorIdx).setMode(runMode);
    }

    public void setMotorZeroPowerBehavior(int motorIdx, DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        getMotor(motorIdx).setZeroPowerBehavior(zeroPowerBehavior);
    }

    public DcMotor getMotor(int motorIdx) {
        if(motorIdx > motors.length - 1) {
            throw new ArrayIndexOutOfBoundsException("Invalid motor index");
        }
        return motors[motorIdx];
    }

    public String[] getConfig() {
        return config;
    }

    public DcMotor[] getMotors() {
        return motors;
    }

    public DcMotor[] getRunUsingEncoderMotors() {
        List<DcMotor> goodMotors = new ArrayList<>();
        for(DcMotor motor : motors) {
            if(motor.getMode() == DcMotor.RunMode.RUN_USING_ENCODER) {
                goodMotors.add(motor);
            }
        }
        return (DcMotor[]) goodMotors.toArray();
    }

    public DcMotor[] getRunWithoutEncoderMotors() {
        List<DcMotor> goodMotors = new ArrayList<>();
        for(DcMotor motor : motors) {
            if(motor.getMode() == DcMotor.RunMode.RUN_WITHOUT_ENCODER) {
                goodMotors.add(motor);
            }
        }
        return (DcMotor[]) goodMotors.toArray();
    }

    public boolean anyMotorUsingEncoder() {
        return getRunUsingEncoderMotors().length != 0;
    }

    public void stopAllMotors() {
        for(DcMotor motor : motors) {
            motor.setPower(0);
        }
    }

    public void setAllZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        for(DcMotor motor : motors) {
            motor.setZeroPowerBehavior(zeroPowerBehavior);
        }
    }

    public void setAllMotorRunModes(DcMotor.RunMode runMode) {
        for(DcMotor motor : motors) {
            motor.setMode(runMode);
        }
    }

    public void resetEncoders() {
        localizer.resetEncoders();
    }

    public Pose2d getPositionEncoders() {
        return localizer.getPositionEncoders();
    }

    public Pose2d getVelocityEncoders() {
        return localizer.getVelocityEncoders();
    }

    public Pose2d getPosition(Units distanceUnit) {
        return localizer.getPosition(distanceUnit);
    }

    public Pose2d getVelocity(Units distanceUnit) {
        return localizer.getVelocity(distanceUnit);
    }

    public void setLocalizer(Localizer localizer) {
        this.localizer = localizer;
    }

    public abstract class Params {
        private String[] config;
        private DcMotor[] motors;
        private Kinematics kinematics;
        private Localizer localizer;
        private double encodersPerMeter;
        public Params(@NotNull Kinematics kinematics, @NotNull Localizer localizer, @NotNull String... config) {
            this.config = config;
            this.kinematics = kinematics;
            this.localizer = localizer;
            motors = new DcMotor[config.length];
            for(int i = 0; i < config.length; i++) {
                motors[i] = robot.hardwareMap.dcMotor.get(config[i]);
            }
            encodersPerMeter = 1;
        }

        public Params setLocalizer(@NotNull Localizer localizer) {
            this.localizer = localizer;
            return this;
        }
        public Params setEncodersPerMeter(double encodersPerMeter) {
            this.encodersPerMeter = encodersPerMeter;
            return this;
        }
    }
}
