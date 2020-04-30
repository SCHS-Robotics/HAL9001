package com.SCHSRobotics.HAL9001.system.source.BaseRobot;

/**
 * An abstract class used to more easily create opmodes.
 *
 * @author Andrew Liang, Level Up
 * @since 0.0.0
 * @version 1.0.0
 *
 * Creation Date: 2017
 */
@SuppressWarnings("unused")
public abstract class BaseAutonomous extends HALProgram {

    /**
     * An abstract method that contains the code for the robot to run.
     */
    public abstract void main();

    @Override
    public final void runOpMode() {
        super.runOpMode();
        Robot robot = getRobot();

        try {
            robot.init();
            onInit();

            while (!isStarted() && !isStopRequested()) {
                robot.init_loop();
                onInitLoop();
            }

            robot.onStart();
            main();

            onStop();
            robot.stopAllComponents();

        }
        catch (Throwable ex) {
            errorLoop(ex);
        }
    }
}