package com.SCHSRobotics.HAL9001.system.tempmenupackage.newconfigstuff;

import com.SCHSRobotics.HAL9001.util.misc.HALConfig;

/**
 * An enum representing the "selection mode" of the config system. This will be either teleop or autonomous selection mode.
 *
 * @author Cole Savage, Level Up
 * @since 1.1.0
 * @version 1.0.0
 *
 * Creation Date: 9/11/20
 */
public enum ConfigSelectionMode {
    AUTONOMOUS("/autonomous", HALConfig.Mode.AUTONOMOUS), TELEOP("/teleop", HALConfig.Mode.TELEOP);

    /**
     * The filepath extension to the robot filepath. This will allow the config system to enter into the appropriate subfolders for autonomous and teleop
     */
    public final String filepathExtension;

    /**
     * The corresponding "HAL config mode" enum in the HALConfig class. These enums will eventually be merged.
     */
    public final HALConfig.Mode mode;

    /**
     * The constructor for the ConfigSelectionMode enum.
     *
     * @param filepathExtension The filepath extension for the given mode.
     * @param mode The corresponding HALConfig mode enum for this selection mode.
     */
    ConfigSelectionMode(String filepathExtension, HALConfig.Mode mode) {
        this.filepathExtension = filepathExtension;
        this.mode = mode;
    }
}
