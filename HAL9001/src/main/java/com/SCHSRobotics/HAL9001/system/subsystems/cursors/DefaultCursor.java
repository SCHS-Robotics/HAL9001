/*
 * Filename: DefaultCursor.java
 * Author: Dylan Zueck and Cole Savage
 * Team Name: Crow Force, Level Up
 * Date: 7/20/19
 */

package com.SCHSRobotics.HAL9001.system.subsystems.cursors;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.source.GUI.Cursor;
import com.SCHSRobotics.HAL9001.system.source.GUI.Menu;
import com.SCHSRobotics.HAL9001.util.exceptions.NotBooleanInputException;
import com.SCHSRobotics.HAL9001.util.misc.BaseParam;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;

/**
 * A default cursor object with normal movement and selection operations.
 */
public class DefaultCursor extends Cursor {

    //The customizeable set of inputs used to control the cursor.
    private CustomizableGamepad inputs;
    //The names of the controls that are used to interact with the cursor.
    public static final String UP = "up", DOWN = "down", LEFT = "left", RIGHT = "right", SELECT = "select";
    //A boolean value used to toggle the controls on and off.
    private boolean flag = true;

    /**
     * Constructor for default cursor.
     *
     * @param robot The robot the cursor is associated with.
     */
    public DefaultCursor(Robot robot, Params params) {
        super(params.x, params.y, params.blinkSpeedMs, params.cursorIcon);
        inputs = new CustomizableGamepad(robot);

        doBlink = params.doBlink;

        setInputs(params.buttons[0], params.buttons[1], params.buttons[2], params.buttons[3], params.buttons[4]);
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
    public void setInputs(Button up, Button down, Button left, Button right, Button select){
        if(up.isBoolean && down.isBoolean && left.isBoolean && right.isBoolean && select.isBoolean) {
            inputs.addButton(UP, up);
            inputs.addButton(DOWN, down);
            inputs.addButton(LEFT, left);
            inputs.addButton(RIGHT, right);
            inputs.addButton(SELECT, select);
        }
        else{
            throw new NotBooleanInputException("DefaultCursor requires all boolean inputs");
        }
    }

    @Override
    public void update() {

        if(inputs.getBooleanInput(SELECT) && flag){
            menu.onSelect();
            menu.onButton(SELECT, inputs.getButton(SELECT));
            flag = false;
        }
        else if(inputs.getBooleanInput(UP) && y-1 >= 0 && flag){

            y--;
            if((y+1) % Menu.MAXLINESPERSCREEN == 0) {
                menu.menuUp();
            }
            menu.onButton(UP,inputs.getButton(UP));
            flag = false;
        }
        else if(inputs.getBooleanInput(DOWN) && y+1 <= menu.getSelectionZoneHeight()-1 && flag){
            y++;
            if(y % Menu.MAXLINESPERSCREEN == 0) {
                menu.menuDown();
            }
            menu.onButton(DOWN,inputs.getButton(DOWN));
            flag = false;
        }
        else if(inputs.getBooleanInput(LEFT) && x-1 >= 0 && flag){
            x--;
            menu.onButton(LEFT,inputs.getButton(LEFT));
            flag = false;
        }
        else if(inputs.getBooleanInput(RIGHT) && x+1 <= menu.getSelectionZoneWidth()-1 && flag){
            x++;
            menu.onButton(RIGHT,inputs.getButton(RIGHT));
            flag = false;
        }
        else if(!inputs.getBooleanInput(SELECT) && !inputs.getBooleanInput(UP) && !inputs.getBooleanInput(DOWN) && !inputs.getBooleanInput(LEFT) && !inputs.getBooleanInput(RIGHT) && !flag){
            flag = true;
        }
        cursorUpdated = !flag;
    }

    /**
     * A class that is used to pass parameters to the cursor when it is created.
     */
    public static final class Params implements BaseParam {

        //Buttons to set cursor buttons to. [0] = up, [1] = down, [2] = left, [3] = right, [4] = select
        private Button[] buttons = {new Button(1, Button.BooleanInputs.dpad_up), new Button(1, Button.BooleanInputs.dpad_down), new Button(1, Button.BooleanInputs.dpad_left), new Button(1, Button.BooleanInputs.dpad_right), new Button(1, Button.BooleanInputs.a)};
        //The cursor's blink speed and starting x,y coordinates.
        private int blinkSpeedMs = 500, x = 0, y = 0;
        //The cursor blink icon.
        private char cursorIcon = '█';
        //Whether or not the cursor should blink.
        private boolean doBlink = true;

        /**
         * Sets the cursor's blink speed.
         *
         * @param blinkSpeedMs The blink speed in miliseconds.
         * @return This instance of the params class.
         */
        public Params setBlinkSpeedMs(int blinkSpeedMs) {
            this.blinkSpeedMs = blinkSpeedMs;
            return this;
        }

        /**
         * Sets the cursor's up button.
         *
         * @param button The up button.
         * @return This instance of the params class.
         */
        public Params setUpButton(Button button) {
            buttons[0] = button;
            return this;
        }

        /**
         * Sets the cursor's down button.
         *
         * @param button The down button.
         * @return This instance of the params class.
         */
        public Params setDownButton(Button button) {
            buttons[1] = button;
            return this;
        }

        /**
         * Sets the cursor's left button.
         *
         * @param button The left button.
         * @return This instance of the params class.
         */
        public Params setLeftButton(Button button) {
            buttons[2] = button;
            return this;
        }

        /**
         * Set the cursor's right button.
         *
         * @param button The right button.
         * @return This instance of the params class.
         */
        public Params setRightButton(Button button) {
            buttons[3] = button;
            return this;
        }

        /**
         * Sets the cursor's select button.
         *
         * @param button The select button.
         * @return This instance of the params class.
         */
        public Params setSelectButton(Button button) {
            buttons[4] = button;
            return this;
        }

        /**
         * Sets the cursor's blink icon.
         *
         * @param cursorIcon The cursor's blink icon.
         * @return This instance of the params class.
         */
        public Params setCursorIcon(char cursorIcon) {
            this.cursorIcon = cursorIcon;
            return this;
        }

        /**
         * Sets whether the cursor should blink.
         *
         * @param doBlink Whether the cursor should blink.
         * @return This instance of the params class.
         */
        public Params setDoBlink(boolean doBlink) {
            this.doBlink = doBlink;
            return this;
        }

        /**
         * Sets the cursor's initial x coordinate.
         *
         * @param x The cursor's initial x coordinate.
         * @return This instance of the params class.
         */
        public Params setX(int x) {
            this.x = x;
            return this;
        }

        /**
         * Sets the cursor's initial y coordinate.
         *
         * @param y The cursor's initial y coordinate.
         * @return This instance of the params class.
         */
        public Params setY(int y) {
            this.y = y;
            return this;
        }
    }
}