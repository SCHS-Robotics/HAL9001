/*
 * Filename: SubSystem.java
 * Author: Andrew Liang
 * Team Name: Level Up
 * Date: 2017
 */

package com.SCHSRobotics.HAL9001.system.source.BaseRobot;

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
     *
     * @throws InterruptedException - Throws this exception if the program is unexpectedly interrupted.
     */
    public abstract void init() throws InterruptedException;

    /**
     * An abstract method that contains code that runs in a loop on init.
     *
     * @throws InterruptedException - Throws this exception if the program is unexpectedly interrupted.
     */
    public abstract void init_loop() throws InterruptedException;

    /**
     * An abstract method containing the code that the subsystem runs when being start.
     *
     * @throws InterruptedException - Throws this exception if the program is unexpectedly interrupted.
     */
    public abstract void start() throws InterruptedException;

    /**
     * An abstract method containing the code that the subsystem runs every loop in a teleop program.
     *
     * @throws InterruptedException - Throws this exception if the program is unexpectedly interrupted.
     */
    public abstract void handle() throws InterruptedException;

    /**
     * An abstract method containing the code that the subsystem runs when the program is stopped.
     *
     * @throws InterruptedException - Throws this exception if the program is unexpectedly interrupted.
     */
    public abstract void stop() throws InterruptedException;
}
