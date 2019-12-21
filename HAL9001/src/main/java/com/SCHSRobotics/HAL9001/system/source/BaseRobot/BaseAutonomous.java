package com.SCHSRobotics.HAL9001.system.source.BaseRobot;

import android.util.Log;

import com.SCHSRobotics.HAL9001.util.annotations.LinkTo;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.exceptions.NothingToSeeHereException;
import com.SCHSRobotics.HAL9001.util.misc.AutoTransitioner;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Supplier;

/**
 * An abstract class used to more easily create opmodes.
 *
 * @author Andrew Liang, Level Up
 * @since 0.0.0
 * @version 1.0.0
 *
 * Creation Date: 2017
 */
public abstract class BaseAutonomous extends LinearOpMode {

    //The robot running the opmode.
    private Robot robot;
    //An exception thrown from main.
    private Throwable exception;
    //Whether an exception has been thrown.
    private boolean exceptionThrown = false;

    /**
     * An abstract method that is used to instantiate the robot.
     *
     * @return The robot being used in the opmode.
     */
    protected abstract Robot buildRobot();

    /**
     * Method that runs when the robot is initialized. It is not an abstract method so that it does not have to be implemented if it
     * is unneeded.
     */
    protected void onInit() {}

    /**
     * Method that runs in a loop after the robot is initialized. It is not an abstract method so that it does not have to be implemented if it
     * is unneeded.
     */
    protected void onInitLoop() {}


    /**
     * Method that runs when the robot is stopped. It is not an abstract method so that it does not have to be implemented if it
     * is unneeded.
     */
    protected void onStop() {}

    /**
     * An abstract method that contains the code for the robot to run.
     */
    public abstract void main();

    @Override
    public final void runOpMode() {

        try {
            robot = buildRobot();
            ExceptionChecker.assertNonNull(robot,new NothingToSeeHereException("Robot is null. You need a robot to run the program."));
        }
        catch (Exception ex) {
            exceptionThrown = true;
            exception = ex;
            Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
        }

        if(this.getClass().isAnnotationPresent(LinkTo.class)) {
            if(this.getClass().getAnnotation(LinkTo.class).auto_transition()) {
                AutoTransitioner.transitionOnStop(this, this.getClass().getAnnotation(LinkTo.class).destination());
            }
        }

        try {
            if(!exceptionThrown) {
                robot.init();
                onInit();
            }
            while(!isStarted() && !isStopRequested()) {
                if(!exceptionThrown) {
                    robot.init_loop();
                    onInitLoop();
                }
                else {
                    telemetry.clearAll();
                    telemetry.addData("ERROR!!!", exception.getMessage());
                    telemetry.update();
                }
            }
        } catch (Throwable ex) {
            telemetry.clearAll();
            telemetry.addData("ERROR!!!", ex.getMessage());
            telemetry.update();
            if(!exceptionThrown) {
                Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
                exceptionThrown = true;
                exception = ex;
            }
        }

        if(!isStopRequested()) {
            try {
                if(!exceptionThrown) {
                    robot.onStart();
                    main();
                }
                else {
                    while(opModeIsActive()) {
                        telemetry.clearAll();
                        telemetry.addData("ERROR!", exception.getMessage());
                        telemetry.update();
                    }
                }
            } catch (Throwable ex) {
                Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
                while(opModeIsActive()) {
                    telemetry.clearAll();
                    telemetry.addData("ERROR!", ex.getMessage());
                    telemetry.update();
                }
            }
        }

        if(!exceptionThrown) {
            onStop();
            robot.stopAllComponents();
        }
    }

    /**
     * Gets the robot running the program.
     *
     * @return The robot running this program.
     */
    protected final Robot getRobot() {
        return robot;
    }

    /**
     * Waits for a specified number of milliseconds.
     *
     * @param millis The number of milliseconds to wait.
     */
    protected final void waitTime(long millis) {
        long stopTime = System.currentTimeMillis() + millis;
        while (robot.opModeIsActive() && System.currentTimeMillis() < stopTime) {
            sleep(1);
        }
    }

    /**
     * Waits for a specified number of milliseconds, running a function in a loop while its waiting.
     *
     * @param millis The number of milliseconds to wait.
     * @param runner The code to run each loop while waiting.
     */
    protected final void waitTime(long millis, Runnable runner) {
        long stopTime = System.currentTimeMillis() + millis;
        while (robot.opModeIsActive() && System.currentTimeMillis() < stopTime) {
            runner.run();
            sleep(1);
        }
    }

    /**
     * Waits until a condition returns true.
     *
     * @param condition The boolean condition that must be true in order for the program to stop waiting.
     */
    protected final void waitUntil(Supplier<Boolean> condition) {
        while (robot.opModeIsActive() && !condition.get()) {
            sleep(1);
        }
    }

    /**
     * Waits until a condition returns true, running a function in a loop while its waiting.
     *
     * @param condition The boolean condition that must be true in order for the program to stop waiting.
     * @param runner The code to run each loop while waiting.
     */
    protected final void waitUntil(Supplier<Boolean> condition, Runnable runner) {
        while (robot.opModeIsActive() && !condition.get()) {
            runner.run();
            sleep(1);
        }
    }

    /**
     * Waits while a condition is true.
     *
     * @param condition The boolean condition that must become false for the program to stop waiting.
     */
    protected final void waitWhile(Supplier<Boolean> condition) {
        while (robot.opModeIsActive() && condition.get()) {
            sleep(1);
        }
    }

    /**
     * Waits while a condition is true, running a function in a loop while its waiting.
     *
     * @param condition The boolean condition that must become false for the program to stop waiting.
     * @param runner The code to run each loop while waiting.
     */
    protected final void waitWhile(Supplier<Boolean> condition, Runnable runner) {
        while (robot.opModeIsActive() && condition.get()) {
            runner.run();
            sleep(1);
        }
    }

    /**
     * Waits a certain amount of time.
     *
     * @param millis The amount of time in milliseconds to wait.
     * @deprecated Renamed to waitTime
     */
    @Deprecated
    protected final void waitFor(long millis) {
        waitTime(millis);
    }
}
