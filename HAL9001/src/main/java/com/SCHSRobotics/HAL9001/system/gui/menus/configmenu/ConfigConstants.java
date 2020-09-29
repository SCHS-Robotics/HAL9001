package com.SCHSRobotics.HAL9001.system.gui.menus.configmenu;

import com.SCHSRobotics.HAL9001.system.gui.menus.TextSelectionMenu;
import com.SCHSRobotics.HAL9001.util.control.Button;
import com.SCHSRobotics.HAL9001.util.misc.UniqueID;

/**
 * A static class to store the constant values used in the config system.
 *
 * @author Cole Savage, Level Up
 * @since 1.1.0
 * @version 1.0.0
 *
 * Creation Date: 9/11/20
 */
public class ConfigConstants {

    /**
     * The unique ids used to identify different values included in payloads passed between the menus.
     */
    public static final UniqueID
            ROBOT_FILEPATH_ID = new UniqueID("config filepath"),
            SELECTION_MODE_ID = new UniqueID("selection mode"),
            STANDALONE_MODE_ID = new UniqueID("stand alone"),
            CONFIG_FILE_NAME_ID = TextSelectionMenu.ENTERED_TEXT_ID,
            BACK_BUTTON_ID = TextSelectionMenu.BACK_BUTTON_ID,
            FORWARD_BUTTON_ID = TextSelectionMenu.FORWARD_BUTTON_ID,
            TEXT_ENTRY_LEFT_ID = TextSelectionMenu.LEFT_BUTTON_ID,
            TEXT_ENTRY_RIGHT_ID = TextSelectionMenu.RIGHT_BUTTON_ID,
            SELECT_BUTTON_ID = new UniqueID("select button"),
            REVERSE_SELECT_BUTTON_ID = new UniqueID("reverse select button"),
            CHANGE_GAMEPAD_BUTTON_ID = new UniqueID("change gamepad button");

    /**
     * More unique ids used to identify payload values. These are protected instead of public, as they meant to be used only internally.
     */
    protected static final UniqueID
            SAVE_TO_AUTORUN_ID = new UniqueID("save to autorun"),
            NEXT_MENU_ID = new UniqueID("next menu");

    /**
     * The default controls used in the config system.
     */
    public static final Button<Boolean>
            DEFAULT_BACK_BUTTON = new Button<>(1, Button.BooleanInputs.left_bumper),
            DEFAULT_FORWARD_BUTTON = new Button<>(1, Button.BooleanInputs.right_bumper),
            DEFAULT_TEXT_ENTRY_LEFT = new Button<>(1, Button.BooleanInputs.x),
            DEFAULT_TEXT_ENTRY_RIGHT = new Button<>(1, Button.BooleanInputs.b),
            DEFAULT_SELECT_BUTTON = new Button<>(1, Button.BooleanInputs.a),
            DEFAULT_REVERSE_SELECT_BUTTON = new Button<>(1, Button.BooleanInputs.b),
            DEFAULT_CHANGE_GAMEPAD_BUTTON = new Button<>(1, Button.BooleanInputs.y);

    /**
     * The char used to divide the cursor region from the options to select.
     */
    public static final char OPTION_DIVIDER = '|';
    /**
     * The text that is prefixed to all options so that the cursor has an area to move and is separated from the options.
     */
    public static final String OPTION_PREFIX = "#"+OPTION_DIVIDER;

    /**
     * The file extension for config files.
     */
    public static final String CONFIG_FILE_EXTENSION = ".txt";

    /**
     * The reserved name of all config metadata files, where metadata about the config system is stored.
     */
    public static final String CONFIG_METADATA_FILENAME = "robot_info";

    /**
     * An empty private constructor used to make the class static.
     */
    private ConfigConstants() {}
}
