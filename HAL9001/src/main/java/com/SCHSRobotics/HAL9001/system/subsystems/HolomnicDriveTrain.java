package com.SCHSRobotics.HAL9001.system.subsystems;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.util.math.FakeNumpy;
import com.SCHSRobotics.HAL9001.util.math.Units;
import com.SCHSRobotics.HAL9001.util.math.Vector2D;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.robotcore.external.Supplier;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.abs;
import static java.lang.Math.hypot;

public abstract class HolomnicDriveTrain extends DriveTrain {

    protected static final String DRIVE_STICK = "DriveStick", TURN_STICK = "TurnStick";

    protected enum DriveType {
        STANDARD, FIELD_CENTRIC, STANDARD_TTA, FIELD_CENTRIC_TTA, MATTHEW
    }
    protected DriveType driveType;

    public HolomnicDriveTrain(@NotNull Robot robot, @NotNull Params params) {
        super(robot, params);
        driveType = params.driveType;
        gamepad.addButton(DRIVE_STICK,params.driveStick);
        gamepad.addButton(TURN_STICK,params.turnStick);
    }

    @Override
    public void handle() {
        Vector2D inputVector = gamepad.getInput(DRIVE_STICK);
        double turnStickValue = gamepad.getInput(TURN_STICK);

        switch (driveType) {
            case STANDARD:
                turnAndMove(inputVector,turnStickValue);
                break;
            case FIELD_CENTRIC:
                break;
            case STANDARD_TTA:
                break;
            case FIELD_CENTRIC_TTA:
                break;
            case MATTHEW:
                break;
        }
    }

    public abstract void turnAndMove(Vector2D velocity, double turnPower);

    public void turnAndMoveTime(Vector2D velocity, double turnPower, long timeMs) {
        turnAndMove(velocity,turnPower);
        waitTime(timeMs);
        stopAllMotors();
    }

    public void turnAndMoveTime(Vector2D velocity, double turnPower, long timeMs, Runnable runWhileProgram) {
        turnAndMove(velocity,turnPower);
        waitTime(timeMs, runWhileProgram);
        stopAllMotors();
    }

    public void turnAndMoveUntil(Vector2D velocity, double turnPower, Supplier<Boolean> condition) {
        turnAndMove(velocity,turnPower);
        waitUntil(condition);
        stopAllMotors();
    }

    public void turnAndMoveUntil(Vector2D velocity, double turnPower, Supplier<Boolean> condition, Runnable runWhileProgram) {
        turnAndMove(velocity,turnPower);
        waitUntil(condition, runWhileProgram);
        stopAllMotors();
    }

    public void turnAndMoveWhile(Vector2D velocity, double turnPower, Supplier<Boolean> condition) {
        turnAndMove(velocity,turnPower);
        waitWhile(condition);
        stopAllMotors();
    }

    public void turnAndMoveWhile(Vector2D velocity, double turnPower, Supplier<Boolean> condition, Runnable runWhileProgram) {
        turnAndMove(velocity,turnPower);
        waitWhile(condition, runWhileProgram);
        stopAllMotors();
    }

    public void turnAndMoveEncoders(Vector2D velocity, double turnPower, final int encoders) {
        double distanceSum = 0;
        Pose2d lastPosition = localizer.getPositionEncoders();
        turnAndMove(velocity, turnPower);
        while(distanceSum < abs(encoders)) {
            Pose2d currentPosition = localizer.getPositionEncoders();
            double distance = hypot(currentPosition.getX()-lastPosition.getX(),currentPosition.getY()-lastPosition.getY());
            distanceSum += distance;
            lastPosition = currentPosition;
        }
        stopAllMotors();
    }

    public void turnAndMoveEncoders(Vector2D velocity, double turnPower, final int encoders, Runnable runWhileProgram) {
        double distanceSum = 0;
        Pose2d lastPosition = localizer.getPositionEncoders();
        turnAndMove(velocity, turnPower);
        while(distanceSum < abs(encoders)) {
            Pose2d currentPosition = localizer.getPositionEncoders();
            double distance = hypot(currentPosition.getX()-lastPosition.getX(),currentPosition.getY()-lastPosition.getY());
            distanceSum += distance;
            lastPosition = currentPosition;
            runWhileProgram.run();
        }
        stopAllMotors();
    }

    public void turnAndMoveDistance(Vector2D velocity, double turnPower, double distance, Units distanceUnit) {
        turnAndMoveEncoders(velocity,turnPower,distProcessor.getEncoderAmount(distance,distanceUnit));
    }

    public void turnAndMoveDistance(Vector2D velocity, double turnPower, double distance, Units distanceUnit, Runnable runWhileProgram) {
        turnAndMoveEncoders(velocity,turnPower,distProcessor.getEncoderAmount(distance,distanceUnit),runWhileProgram);
    }

    public void turn(double turnPower) {
        turnAndMove(new Vector2D(0,0), turnPower);
    }

    public void turnTime(double turnPower, long timeMs) {
        turn(turnPower);
        waitTime(timeMs);
        stopAllMotors();
    }

    public void turnTime(double turnPower, long timeMs, Runnable runWhileProgram) {
        turn(turnPower);
        waitTime(timeMs, runWhileProgram);
        stopAllMotors();
    }

    public void turnUntil(double turnPower, Supplier<Boolean> condition) {
        turn(turnPower);
        waitUntil(condition);
        stopAllMotors();
    }

    public void turnUntil(double turnPower, Supplier<Boolean> condition, Runnable runWhileProgram) {
        turn(turnPower);
        waitUntil(condition, runWhileProgram);
        stopAllMotors();
    }

    public void turnWhile(double turnPower, Supplier<Boolean> condition) {
        turn(turnPower);
        waitWhile(condition);
        stopAllMotors();
    }

    public void turnWhile(double turnPower, Supplier<Boolean> condition, Runnable runWhileProgram) {
        turn(turnPower);
        waitWhile(condition, runWhileProgram);
        stopAllMotors();
    }

    public void turnEncoders(double turnPower, final int encoders) {
        final int[] initPosition = localizer.getRawEncoders();
        turnUntil(turnPower, new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                int[] currentPosition = localizer.getRawEncoders();
                int[] deltas = FakeNumpy.absdiff(currentPosition, initPosition);
                boolean turnCommandComplete = false;
                for(double delta : deltas) {
                    turnCommandComplete |= delta >= abs(encoders);
                }
                return turnCommandComplete;
            }
        });
    }

    public void turnEncoders(double turnPower, final int encoders, Runnable runWhileProgram) {
        final int[] initPosition = localizer.getRawEncoders();
        turnUntil(turnPower, new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                int[] currentPosition = localizer.getRawEncoders();
                int[] deltas = FakeNumpy.absdiff(currentPosition, initPosition);
                boolean turnCommandComplete = false;
                for(double delta : deltas) {
                    turnCommandComplete |= delta >= abs(encoders);
                }
                return turnCommandComplete;
            }
        }, runWhileProgram);
    }

    public void turnDistance(double turnPower, double distance, Units distanceUnit) {
        turnEncoders(turnPower,distProcessor.getEncoderAmount(distance,distanceUnit));
    }

    public void turnDistance(double turnPower, double distance, Units distanceUnit, Runnable runWhileProgram) {
        turnEncoders(turnPower,distProcessor.getEncoderAmount(distance,distanceUnit),runWhileProgram);
    }

    public void move(Vector2D velocity) {
        turnAndMove(velocity, 0);
    }

    public void moveTime(Vector2D velocity, long timeMs) {
        move(velocity);
        waitTime(timeMs);
        stopAllMotors();
    }

    public void moveTime(Vector2D velocity, long timeMs, Runnable runWhileProgram) {
        move(velocity);
        waitTime(timeMs, runWhileProgram);
        stopAllMotors();
    }

    public void moveUntil(Vector2D velocity, Supplier<Boolean> condition) {
        move(velocity);
        waitUntil(condition);
        stopAllMotors();
    }

    public void moveUntil(Vector2D velocity, Supplier<Boolean> condition, Runnable runWhileProgram) {
        move(velocity);
        waitUntil(condition, runWhileProgram);
        stopAllMotors();
    }

    public void moveWhile(Vector2D velocity, Supplier<Boolean> condition) {
        move(velocity);
        waitWhile(condition);
        stopAllMotors();
    }

    public void moveWhile(Vector2D velocity, Supplier<Boolean> condition, Runnable runWhileProgram) {
        move(velocity);
        waitWhile(condition, runWhileProgram);
        stopAllMotors();
    }

    public void moveEncoders(Vector2D velocity, final int encoders) {
        final Pose2d initPosition = localizer.getPositionEncoders();
        moveUntil(velocity, new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                Pose2d currentPosition = localizer.getPositionEncoders();
                double distance = hypot(currentPosition.getX()-initPosition.getX(), currentPosition.getY()-initPosition.getY());
                return distance >= abs(encoders);
            }
        });
    }

    public void moveEncoders(Vector2D velocity, final int encoders, Runnable runWhileProgram) {
        final Pose2d initPosition = localizer.getPositionEncoders();
        moveUntil(velocity, new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                Pose2d currentPosition = localizer.getPositionEncoders();
                double distance = hypot(currentPosition.getX()-initPosition.getX(), currentPosition.getY()-initPosition.getY());
                return distance >= abs(encoders);
            }
        }, runWhileProgram);
    }

    public void moveDistance(Vector2D velocity, final int distance, Units distanceUnit) {
        moveEncoders(velocity,distProcessor.getEncoderAmount(distance,distanceUnit));
    }

    public void moveDistance(Vector2D velocity, final int distance, Units distanceUnit, Runnable runWhileProgram) {
        moveEncoders(velocity,distProcessor.getEncoderAmount(distance,distanceUnit), runWhileProgram);
    }

    public void moveMatthew(Vector2D velocity1, Vector2D velocity2) {}

    public void moveMatthewTime(Vector2D velocity1, Vector2D velocity2, long timeMs) {
        moveMatthew(velocity1,velocity2);
        waitTime(timeMs);
        stopAllMotors();
    }

    public void moveMatthewTime(Vector2D velocity1, Vector2D velocity2, long timeMs, Runnable runWhileProgram) {
        moveMatthew(velocity1,velocity2);
        waitTime(timeMs,runWhileProgram);
        stopAllMotors();
    }

    public abstract class Params extends DriveTrain.Params {

        private Button driveStick, turnStick;
        private DriveType driveType;
        public Params(@NotNull Kinematics kinematics, @NotNull Localizer localizer, @NotNull String... config) {
            super(kinematics,localizer, config);
            this.driveType = DriveType.STANDARD;
        }
        public Params setDriveType(DriveType driveType) {
            this.driveType = driveType;
            return this;
        }
        public Params setDriveStick(Button driveStick) {
            this.driveStick = driveStick;
            return this;
        }
        public Params setTurnStick(Button turnStick) {
            this.turnStick = turnStick;
            return this;
        }
    }
}
