/*
 * Filename: ConfigCursor.java
 * Author: Dylan Zueck and Cole Savage
 * Team Name: Crow Force, Level Up
 * Date: 8/13/19
 */

package com.SCHSRobotics.HAL9001.system.subsystems.cursors;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.source.GUI.Cursor;
import com.SCHSRobotics.HAL9001.system.source.GUI.Menu;
import com.SCHSRobotics.HAL9001.util.exceptions.NotBooleanInputException;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;

/**
 * A cursor object used in config menus. Contains all the controls needed to use the config system.
 */
public class ConfigCursor extends Cursor {

    //The customizable set of inputs used to control the cursor.
    private CustomizableGamepad inputs;
    //The millisecond timestamps of the last time when select was pressed and when reverse select was pressed, respectively
    private long timeFromLastPressSelectMill, timeFromLastPressRSelectMill;
    //The names of the controls that are used to interact with the cursor.
    public static final String UP = "up", DOWN = "down", LEFT = "left", RIGHT = "right", SELECT = "select", SWITCH_GAMEPAD = "switchgamepad", REVERSE_SELECT = "it's rewind time", BACK_BUTTON = "back", DISABLE_AUTORUN = "disable autorun";
    //A boolean value specifying if the cursor has been set to "Write mode"
    private boolean writeMode;
    //A boolean value used to toggle the controls on and off.
    private boolean flag = true;

    /**
     * Constructor for config cursor.
     *
     * @param robot The robot the cursor is associataed with.
     * @param x The cursor's initial x coordinate.
     * @param y The cursor's initial y coordinate.
     * @param blinkSpeedMs The cursor's blink speed in milliseconds.
     * @param cursorIcon The icon used to represent the cursor.
     */
    public ConfigCursor(Robot robot, int x, int y, int blinkSpeedMs, char cursorIcon) {
        super(x, y, blinkSpeedMs, cursorIcon);
        inputs = new CustomizableGamepad(robot);

        inputs.addButton(UP,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(DOWN,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(LEFT,new Button(1, Button.BooleanInputs.dpad_left));
        inputs.addButton(RIGHT,new Button(1, Button.BooleanInputs.dpad_right));
        inputs.addButton(SELECT, new Button(1, Button.BooleanInputs.a));
        inputs.addButton(REVERSE_SELECT, new Button(1, Button.BooleanInputs.b));
        inputs.addButton(SWITCH_GAMEPAD, new Button(1, Button.BooleanInputs.y));
        inputs.addButton(BACK_BUTTON, new Button(1, Button.BooleanInputs.x));
        inputs.addButton(DISABLE_AUTORUN, new Button(1, Button.BooleanInputs.left_bumper));

        doBlink = true;
        writeMode = false;
    }

    /**
     * Constructor for config cursor.
     *
     * @param robot The robot the cursor is associataed with.
     * @param blinkSpeedMs The cursor's blink speed in milliseconds.
     * @param cursorIcon The icon used to represent the cursor.
     */
    public ConfigCursor(Robot robot, int blinkSpeedMs, char cursorIcon) {
        super(blinkSpeedMs, cursorIcon);
        inputs = new CustomizableGamepad(robot);

        inputs.addButton(UP,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(DOWN,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(LEFT,new Button(1, Button.BooleanInputs.dpad_left));
        inputs.addButton(RIGHT,new Button(1, Button.BooleanInputs.dpad_right));
        inputs.addButton(SELECT, new Button(1, Button.BooleanInputs.a));
        inputs.addButton(REVERSE_SELECT, new Button(1, Button.BooleanInputs.b));
        inputs.addButton(SWITCH_GAMEPAD, new Button(1, Button.BooleanInputs.y));
        inputs.addButton(BACK_BUTTON, new Button(1, Button.BooleanInputs.x));
        inputs.addButton(DISABLE_AUTORUN, new Button(1, Button.BooleanInputs.left_bumper));

        doBlink = true;
        writeMode = false;
    }

    /**
     * Constructor for config cursor.
     *
     * @param robot The robot the cursor is associataed with.
     * @param blinkSpeedMs The cursor's blink speed in milliseconds.
     */
    public ConfigCursor(Robot robot, int blinkSpeedMs) {
        super(blinkSpeedMs);
        inputs = new CustomizableGamepad(robot);

        inputs.addButton(UP,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(DOWN,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(LEFT,new Button(1, Button.BooleanInputs.dpad_left));
        inputs.addButton(RIGHT,new Button(1, Button.BooleanInputs.dpad_right));
        inputs.addButton(SELECT, new Button(1, Button.BooleanInputs.a));
        inputs.addButton(REVERSE_SELECT, new Button(1, Button.BooleanInputs.b));
        inputs.addButton(SWITCH_GAMEPAD, new Button(1, Button.BooleanInputs.y));
        inputs.addButton(BACK_BUTTON, new Button(1, Button.BooleanInputs.x));
        inputs.addButton(DISABLE_AUTORUN, new Button(1, Button.BooleanInputs.left_bumper));

        doBlink = true;
        writeMode = false;
    }

    /**
     * Constructor for config cursor.
     *
     * @param robot The robot the cursor is associataed with.
     * @param x The cursor's initial x coordinate.
     * @param y = The cursor's initial y coordinate.
     * @param blinkSpeedMs The cursor's blink speed in milliseconds.
     */
    public ConfigCursor(Robot robot, int x, int y, int blinkSpeedMs) {
        super(x, y, blinkSpeedMs);
        inputs = new CustomizableGamepad(robot);

        inputs.addButton(UP,new Button(1, Button.BooleanInputs.dpad_up));
        inputs.addButton(DOWN,new Button(1, Button.BooleanInputs.dpad_down));
        inputs.addButton(LEFT,new Button(1, Button.BooleanInputs.dpad_left));
        inputs.addButton(RIGHT,new Button(1, Button.BooleanInputs.dpad_right));
        inputs.addButton(SELECT, new Button(1, Button.BooleanInputs.a));
        inputs.addButton(REVERSE_SELECT, new Button(1, Button.BooleanInputs.b));
        inputs.addButton(SWITCH_GAMEPAD, new Button(1, Button.BooleanInputs.y));
        inputs.addButton(BACK_BUTTON, new Button(1, Button.BooleanInputs.x));
        inputs.addButton(DISABLE_AUTORUN, new Button(1, Button.BooleanInputs.left_bumper));

        doBlink = true;
        writeMode = false;
    }

    /**
     * Sets which buttons will be used to control the cursor.
     *
     * @param up The up button.
     * @param down The down button.
     * @param left The left button.
     * @param right The right button.
     * @param select The select button
     *
     * @throws NotBooleanInputException Throws an exception if button does not return boolean values.
     */
    public void setInputs(Button up, Button down, Button left, Button right, Button select, Button reverseSelect, Button switchGamepad, Button backButton){

        if(up.isBoolean && down.isBoolean && left.isBoolean && right.isBoolean && select.isBoolean && reverseSelect.isBoolean && switchGamepad.isBoolean && backButton.isBoolean) {
            inputs.addButton(UP, up);
            inputs.addButton(DOWN, down);
            inputs.addButton(LEFT, left);
            inputs.addButton(RIGHT, right);
            inputs.addButton(SELECT, select);
            inputs.addButton(REVERSE_SELECT, reverseSelect);
            inputs.addButton(SWITCH_GAMEPAD, switchGamepad);
            inputs.addButton(BACK_BUTTON, backButton);
            inputs.addButton(DISABLE_AUTORUN, new Button(1, Button.BooleanInputs.left_bumper));
        }
        else{
            throw new NotBooleanInputException("ConfigCursor requires all boolean inputs");
        }
    }

    /**
     * Sets whether the cursor is in write mode.
     *
     * @param writeMode Whether the cursor will be set to write mode (true) or not (false).
     */
    public void setWriteMode(boolean writeMode) {
        this.writeMode = writeMode;
    }

    @Override
    public void update() {

        if(inputs.getBooleanInput(SELECT) && (flag || (writeMode && System.currentTimeMillis()-timeFromLastPressSelectMill > 250))){
            menu.onSelect();
            menu.onButton(SELECT,inputs.getButton(SELECT));
            timeFromLastPressSelectMill = System.currentTimeMillis();
            flag = false;
        }
        else if(inputs.getBooleanInput(REVERSE_SELECT) && (flag || (writeMode && System.currentTimeMillis()-timeFromLastPressRSelectMill > 250))) {
            menu.onButton(REVERSE_SELECT,inputs.getButton(REVERSE_SELECT));
            timeFromLastPressRSelectMill = System.currentTimeMillis();
            flag = false;
        }
        else if(inputs.getBooleanInput(SWITCH_GAMEPAD) && flag) {
            menu.onButton(SWITCH_GAMEPAD,inputs.getButton(SWITCH_GAMEPAD));
            flag = false;
        }
        else if (inputs.getBooleanInput(UP) && y - 1 >= 0 && flag) {
            y--;
            if ((y + 1) % Menu.MAXLINESPERSCREEN == 0) {
                super.menu.menuUp();
            }
            flag = false;

            menu.onButton(UP, inputs.getButton(UP));
        }
        else if (inputs.getBooleanInput(DOWN) && y + 1 <= super.menu.getSelectionZoneHeight() - 1 && flag) {
            y++;
            if (y % Menu.MAXLINESPERSCREEN == 0) {
                menu.menuDown();
            }
            flag = false;

            menu.onButton(DOWN, inputs.getButton(DOWN));
        }
        else if (inputs.getBooleanInput(LEFT) && x - 1 >= 0 && flag) {
            x--;
            flag = false;

            menu.onButton(LEFT, inputs.getButton(LEFT));
        }
        else if (inputs.getBooleanInput(RIGHT) && x + 1 <= menu.getSelectionZoneWidth() - 1 && flag) {
            x++;
            flag = false;

            menu.onButton(RIGHT, inputs.getButton(RIGHT));
        }
        else if(inputs.getBooleanInput(BACK_BUTTON) && flag) {
            menu.onButton(BACK_BUTTON, inputs.getButton(BACK_BUTTON));
            flag = false;
        }
        else if(inputs.getBooleanInput(DISABLE_AUTORUN) && flag) {
            menu.onButton(DISABLE_AUTORUN,inputs.getButton(DISABLE_AUTORUN));
            flag = false;
        }
        else if (!inputs.getBooleanInput(SELECT) && !inputs.getBooleanInput(UP) && !inputs.getBooleanInput(DOWN) && !inputs.getBooleanInput(LEFT) && !inputs.getBooleanInput(RIGHT) && !inputs.getBooleanInput(REVERSE_SELECT) && !inputs.getBooleanInput(SWITCH_GAMEPAD) && !inputs.getBooleanInput(BACK_BUTTON) && !inputs.getBooleanInput(DISABLE_AUTORUN) && !flag) {
            flag = true;
        }

        cursorUpdated = !flag;

        if(writeMode) {
            forceCursorChar = !(inputs.getBooleanInput(SELECT) || inputs.getBooleanInput(REVERSE_SELECT));
        }
        else {
            forceCursorChar = true;
        }
    }
}
