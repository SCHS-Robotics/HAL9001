package com.SCHSRobotics.HAL9001.util.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * An exception thrown if a drive train is given an invalid movement command.
 * <p>
 * Creation Date: 9/5/19
 *
 * @author Cole Savage, Level Up
 * @version 1.0.0
 * @see com.SCHSRobotics.HAL9001.system.robot.subsystems.MechanumDrive
 * @see com.SCHSRobotics.HAL9001.system.robot.subsystems.OmniWheelDrive
 * @see com.SCHSRobotics.HAL9001.system.robot.subsystems.TankDrive
 * @see com.SCHSRobotics.HAL9001.system.robot.subsystems.QuadWheelDrive
 * @see RuntimeException
 * @since 1.0.0
 */
public class InvalidMoveCommandException extends RuntimeException {

    /**
     * Constructor for InvalidMoveCommandException.
     *
     * @param message The error message to print to the screen.
     */
    public InvalidMoveCommandException(@Nullable String message) {
        super(message);
    }
}