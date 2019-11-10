/*
 * Filename: BaseTeleop.java
 * Author: Andrew Liang
 * Team Name: Level Up
 * Date: 2017
 */

package com.SCHSRobotics.HAL9001.system.source.BaseRobot;

import android.util.Log;

import com.SCHSRobotics.HAL9001.util.functional_interfaces.BiFunction;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * An abstract class used to more easily create teleop programs
 */
public abstract class BaseTeleop extends LinearOpMode {

    //The robot running the opmode.
    private Robot robot;

    /**
     * An abstract method that is used to instantiate the robot.
     *
     * @return - The robot being used in the opmode.
     */
    protected abstract Robot buildRobot();

    /**
     * Method that runs when the robot is initialized. It is not an abstract method so that it does not have to be implemented if it
     * is unneeded.
     */
    protected void onInit(){}

    /**
     * Method that runs in a loop after the robot is initialized. It is not an abstract method so that it does not have to be implemented if it
     * is unneeded.
     */
    protected void onInitLoop(){}

    /**
     * Method that runs when the robot is started. It is not an abstract method so that it does not have to be implemented if it
     * is unneeded.
     */
    protected void onStart(){}

    /**
     * Method that runs every loop cycle. It is not an abstract method so that it does not have to be implemented if it
     * is unneeded.
     */
    protected void onUpdate(){}

    /**
     * Method that runs when the robot is stopped. It is not an abstract method so that it does not have to be implemented if it
     * is unneeded.
     */
    protected void onStop(){}

    @Override
    public final void runOpMode() {
        robot = buildRobot();
        try {
            robot.init();
            onInit();

            while(!isStarted() && !isStopRequested()) {
                robot.init_loop();
                onInitLoop();
            }

            if(!isStopRequested()) {
                robot.onStart();
                onStart();

                while (!isStopRequested()) {
                    robot.driverControlledUpdate();
                    onUpdate();
                }
            }

            onStop();
            robot.stopAllComponents();
        }
        catch (Exception ex){
            telemetry.clearAll();
            telemetry.addData("ERROR!!!", ex.toString());
            telemetry.update();
            Log.e(this.getClass().getSimpleName(), ex.toString(), ex);
        }
    }

    /**
     * Gets the robot.
     *
     * @return - The robot.
     */
    protected final Robot getRobot(){
        return robot;
    }

    /**
     * Waits for a specified number of milliseconds.
     *
     * @param millis - The number of milliseconds to wait.
     */
    protected final void waitFor(long millis) {
        long stopTime = System.currentTimeMillis() + millis;
        while (opModeIsActive() && System.currentTimeMillis() < stopTime) {
            sleep(1);
        }
    }

    /**
     * Waits for a boolean function with two inputs to return true. param1 and 2 must be updated from separate thread.
     *
     * @param condition - An arbitrary function taking two inputs and outputting a boolean.
     * @param param1 - The function's first parameter.
     * @param param2 - The function's second parameter.
     * @param <T> - The first parameter's object type.
     * @param <X> - The second parameter's object type.
     */
    protected final <T,X> void waitFor(BiFunction<T,X,Boolean> condition, T param1, X param2) {
        while (opModeIsActive() && !condition.apply(param1,param2)) {
            sleep(1);
        }
    }
}
