/*
 * Filename: SubSystem.java
 * Author: Andrew Liang
 * Team Name: Level Up
 * Date: 2017
 */

package com.SCHSRobotics.HAL9001.system.source.BaseRobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Supplier;

/**
 * An abstract class representing a subsystem on the robot.
 */
public abstract class SubSystem {

    //The robot the subsystem belongs to.
    protected Robot robot;
    //A boolean specifying whether or not the subsystem should use the configuration menu.
    protected boolean usesConfig;

    /**
     * Constructor for subsystem.
     *
     * @param robot - The robot the subsystem is contained within.
     */
    public SubSystem(Robot robot) {
        this.robot = robot;
        usesConfig = false;
        initVars();
    }

    /**
     * An overridable method that allows you to easily initialize variables at the beginning of every constructor for the subsystem.
     */
    protected void initVars() {}

    /**
     * An abstract method containing the code that the subsystem runs when being initialized.
     */
    public abstract void init();

    /**
     * An abstract method that contains code that runs in a loop on init.
     */
    public abstract void init_loop();

    /**
     * An abstract method containing the code that the subsystem runs when being start.
     */
    public abstract void start();

    /**
     * An abstract method containing the code that the subsystem runs every loop in a teleop program.
     */
    public abstract void handle();

    /**
     * An abstract method containing the code that the subsystem runs when the program is stopped.
     */
    public abstract void stop();

    /**
     * Waits for a specified number of milliseconds.
     *
     * @param millis - The number of milliseconds to wait.
     */
    protected final void waitTime(long millis) {
        long stopTime = System.currentTimeMillis() + millis;
        while (robot.opModeIsActive() && System.currentTimeMillis() < stopTime) {
            ((LinearOpMode) robot.getOpMode()).sleep(1);
        }
    }

    protected final void waitTime(long millis, Runnable runner) {
        long stopTime = System.currentTimeMillis() + millis;
        while (robot.opModeIsActive() && System.currentTimeMillis() < stopTime) {
            runner.run();
            ((LinearOpMode) robot.getOpMode()).sleep(1);
        }
    }

    protected final void waitUntil(Supplier<Boolean> condition) {
        while (robot.opModeIsActive() && !condition.get()) {
            ((LinearOpMode) robot.getOpMode()).sleep(1);
        }
    }

    protected final void waitUntil(Supplier<Boolean> condition, Runnable runner) {
        while (robot.opModeIsActive() && !condition.get()) {
            runner.run();
            ((LinearOpMode) robot.getOpMode()).sleep(1);
        }
    }

    protected final void waitWhile(Supplier<Boolean> condition) {
        while (robot.opModeIsActive() && condition.get()) {
            ((LinearOpMode) robot.getOpMode()).sleep(1);
        }
    }

    protected final void waitWhile(Supplier<Boolean> condition, Runnable runner) {
        while (robot.opModeIsActive() && condition.get()) {
            runner.run();
            ((LinearOpMode) robot.getOpMode()).sleep(1);
        }
    }
}
