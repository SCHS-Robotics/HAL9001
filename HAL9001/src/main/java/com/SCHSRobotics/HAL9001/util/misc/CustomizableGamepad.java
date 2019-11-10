/*
 * Filename: CustomizableGamepad.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/18/19
 */

package com.SCHSRobotics.HAL9001.util.misc;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.util.math.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * A intermediary class between the robot and the gamepad controls that allows all control systems to be customized.
 */
public class CustomizableGamepad {

    //The robot running this.
    private Robot robot;
    //HashMap of Buttons that are related to inputs from the controller.
    private Map<String, Button> inputs;

    /**
     * Constructor for CustomizableGamepad.
     *
     * @param robot - The robot this program is running on.
     */
    public CustomizableGamepad(Robot robot) {
        this.robot = robot;
        this.inputs = new HashMap<>();
    }

    /**
     * Adds a double input/Button.
     *
     * @param buttonName - Key that will be used to get the input of the button.
     * @param button - Enum for double input wanted.
     * @param gamepadNumber - Number of gamepad this button will use.
     */
    public void addButton(String buttonName, Button.DoubleInputs button, int gamepadNumber) {
        inputs.put(buttonName, new Button(gamepadNumber, button));
    }

    /**
     * Adds a boolean input/Button.
     *
     * @param buttonName - Key that will be used to get the input of the button.
     * @param button - Enum for boolean input wanted.
     * @param gamepadNumber - Number of gamepad this button will use.
     */
    public void addButton(String buttonName, Button.BooleanInputs button, int gamepadNumber) {
        inputs.put(buttonName, new Button(gamepadNumber, button));
    }

    /**
     * Adds a input/Button.
     *
     * @param buttonName - Key that will be used to get the input of the button.
     * @param button - Button object that relates to an input.
     */
    public void addButton(String buttonName, Button button) {
        inputs.put(buttonName, button);
    }

    /**
     * Adds a boolean input/Button with deadzone.
     *
     * @param buttonName - Key that will be used to get the input of the button.
     * @param button - Enum for boolean input wanted.
     * @param gamepadNumber - Number of gamepad this button will use.
     * @param deadzone - deadzone for boolean version of double inputs.
     */
    public void addButton(String buttonName, Button.BooleanInputs button, int gamepadNumber, double deadzone) {
        inputs.put(buttonName, new Button(gamepadNumber, button));
        inputs.get(buttonName).setDeadzone(deadzone);
    }

    /**
     * Adds a input/Button with deadzone(deadzone does nothing for double inputs).
     *
     * @param buttonName - Key that will be used to get the input of the button.
     * @param button - Button object that relates to an input.
     */
    public void addButton(String buttonName, Button button, double deadzone) {
        inputs.put(buttonName, button);
        inputs.get(buttonName).setDeadzone(deadzone);
    }

    /**
     * Removes a input/Button.
     *
     * @param buttonName - Key of button to be removed.
     */
    public void removeButton(String buttonName) {
        inputs.remove(buttonName);
    }

    /**
     * Returns if a button is set to noButton.
     *
     * @param buttonName - Key of button to be checked.
     */
    public boolean checkNoButton(String buttonName) {
        return inputs.get(buttonName).getInputEnum() == Button.DoubleInputs.noButton || inputs.get(buttonName).getInputEnum() == Button.BooleanInputs.noButton || inputs.get(buttonName).getInputEnum() == Button.VectorInputs.noButton;
    }

    /**
     * Returns boolean input of button.
     *
     * @param buttonName - Key of boolean button to get input from.
     */
    public boolean getBooleanInput(String buttonName) {

        if (inputs.get(buttonName).gamepadNumber == 1) {
            switch ((Button.BooleanInputs) inputs.get(buttonName).getInputEnum()) {

                case a: return robot.gamepad1.a;
                case b: return robot.gamepad1.b;
                case x: return robot.gamepad1.x;
                case y: return robot.gamepad1.y;
                case back: return robot.gamepad1.back;
                case start: return robot.gamepad1.start;
                case guide: return robot.gamepad1.guide;
                case dpad_up: return robot.gamepad1.dpad_up;
                case dpad_down: return robot.gamepad1.dpad_down;
                case dpad_left: return robot.gamepad1.dpad_left;
                case dpad_right: return robot.gamepad1.dpad_right;
                case left_bumper: return robot.gamepad1.left_bumper;
                case right_bumper: return robot.gamepad1.right_bumper;
                case left_stick_button: return robot.gamepad1.left_stick_button;
                case right_stick_button: return robot.gamepad1.right_stick_button;
                case bool_left_trigger: return robot.gamepad1.left_trigger > inputs.get(buttonName).deadzone;
                case bool_right_trigger: return robot.gamepad1.right_trigger > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_up: return -robot.gamepad1.left_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_x_left: return robot.gamepad1.left_stick_x < inputs.get(buttonName).deadzone;
                case bool_left_stick_x_right: return robot.gamepad1.left_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_up: return -robot.gamepad1.right_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_down: return -robot.gamepad1.left_stick_y < -inputs.get(buttonName).deadzone;
                case bool_right_stick_x_left: return robot.gamepad1.right_stick_x < inputs.get(buttonName).deadzone;
                case bool_right_stick_x_right: return robot.gamepad1.right_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_down: return -robot.gamepad1.right_stick_y < -inputs.get(buttonName).deadzone;
                case bool_left_stick_x: return Math.abs(robot.gamepad1.left_stick_x) > inputs.get(buttonName).deadzone;
                case bool_left_stick_y: return Math.abs(robot.gamepad1.left_stick_y) > inputs.get(buttonName).deadzone;
                case bool_right_stick_x: return Math.abs(robot.gamepad1.right_stick_x) > inputs.get(buttonName).deadzone;
                case bool_right_stick_y: return Math.abs(robot.gamepad1.right_stick_y) > inputs.get(buttonName).deadzone;

                default:
                    return false;
            }
        } else {
            switch ((Button.BooleanInputs) inputs.get(buttonName).getInputEnum()) {

                case a: return robot.gamepad2.a;
                case b: return robot.gamepad2.b;
                case x: return robot.gamepad2.x;
                case y: return robot.gamepad2.y;
                case back: return robot.gamepad2.back;
                case start: return robot.gamepad2.start;
                case guide: return robot.gamepad2.guide;
                case dpad_up: return robot.gamepad2.dpad_up;
                case dpad_down: return robot.gamepad2.dpad_down;
                case dpad_left: return robot.gamepad2.dpad_left;
                case dpad_right: return robot.gamepad2.dpad_right;
                case left_bumper: return robot.gamepad2.left_bumper;
                case right_bumper: return robot.gamepad2.right_bumper;
                case left_stick_button: return robot.gamepad2.left_stick_button;
                case right_stick_button: return robot.gamepad2.right_stick_button;
                case bool_left_trigger: return robot.gamepad2.left_trigger > inputs.get(buttonName).deadzone;
                case bool_right_trigger: return robot.gamepad2.right_trigger > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_up: return -robot.gamepad2.left_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_x_left: return robot.gamepad2.left_stick_x < inputs.get(buttonName).deadzone;
                case bool_left_stick_x_right: return robot.gamepad2.left_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_up: return -robot.gamepad2.right_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_down: return -robot.gamepad2.left_stick_y < -inputs.get(buttonName).deadzone;
                case bool_right_stick_x_left: return robot.gamepad2.right_stick_x < inputs.get(buttonName).deadzone;
                case bool_right_stick_x_right: return robot.gamepad2.right_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_down: return -robot.gamepad2.right_stick_y < -inputs.get(buttonName).deadzone;
                case bool_left_stick_x: return Math.abs(robot.gamepad2.left_stick_x) > inputs.get(buttonName).deadzone;
                case bool_left_stick_y: return Math.abs(robot.gamepad2.left_stick_y) > inputs.get(buttonName).deadzone;
                case bool_right_stick_x: return Math.abs(robot.gamepad2.right_stick_x) > inputs.get(buttonName).deadzone;
                case bool_right_stick_y: return Math.abs(robot.gamepad2.right_stick_y) > inputs.get(buttonName).deadzone;

                default:
                    return false;
            }
        }
    }

    /**
     * Returns boolean input of button with a set defaultReturn.
     *
     * @param buttonName - Key of button to get input from.
     * @param defaultReturn - Default return.
     */
    public boolean getBooleanInput(String buttonName, boolean defaultReturn) {
        if (inputs.get(buttonName).gamepadNumber == 1) {
            switch ((Button.BooleanInputs) inputs.get(buttonName).getInputEnum()) {

                case a: return robot.gamepad1.a;
                case b: return robot.gamepad1.b;
                case x: return robot.gamepad1.x;
                case y: return robot.gamepad1.y;
                case back: return robot.gamepad1.back;
                case start: return robot.gamepad1.start;
                case guide: return robot.gamepad1.guide;
                case dpad_up: return robot.gamepad1.dpad_up;
                case dpad_down: return robot.gamepad1.dpad_down;
                case dpad_left: return robot.gamepad1.dpad_left;
                case dpad_right: return robot.gamepad1.dpad_right;
                case left_bumper: return robot.gamepad1.left_bumper;
                case right_bumper: return robot.gamepad1.right_bumper;
                case left_stick_button: return robot.gamepad1.left_stick_button;
                case right_stick_button: return robot.gamepad1.right_stick_button;
                case bool_left_trigger: return robot.gamepad1.left_trigger > inputs.get(buttonName).deadzone;
                case bool_right_trigger: return robot.gamepad1.right_trigger > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_up: return -robot.gamepad1.left_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_x_left: return robot.gamepad1.left_stick_x < -inputs.get(buttonName).deadzone;
                case bool_left_stick_x_right: return robot.gamepad1.left_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_up: return -robot.gamepad1.right_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_down: return -robot.gamepad1.left_stick_y < -inputs.get(buttonName).deadzone;
                case bool_right_stick_x_left: return robot.gamepad1.right_stick_x < -inputs.get(buttonName).deadzone;
                case bool_right_stick_x_right: return robot.gamepad1.right_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_down: return -robot.gamepad1.right_stick_y < -inputs.get(buttonName).deadzone;
                case bool_left_stick_x: return Math.abs(robot.gamepad1.left_stick_x) > inputs.get(buttonName).deadzone;
                case bool_left_stick_y: return Math.abs(robot.gamepad1.left_stick_y) > inputs.get(buttonName).deadzone;
                case bool_right_stick_x: return Math.abs(robot.gamepad1.right_stick_x) > inputs.get(buttonName).deadzone;
                case bool_right_stick_y: return Math.abs(robot.gamepad1.right_stick_y) > inputs.get(buttonName).deadzone;

                default:
                    return defaultReturn;
            }
        } else {
            switch ((Button.BooleanInputs) inputs.get(buttonName).getInputEnum()) {

                case a: return robot.gamepad2.a;
                case b: return robot.gamepad2.b;
                case x: return robot.gamepad2.x;
                case y: return robot.gamepad2.y;
                case back: return robot.gamepad2.back;
                case start: return robot.gamepad2.start;
                case guide: return robot.gamepad2.guide;
                case dpad_up: return robot.gamepad2.dpad_up;
                case dpad_down: return robot.gamepad2.dpad_down;
                case dpad_left: return robot.gamepad2.dpad_left;
                case dpad_right: return robot.gamepad2.dpad_right;
                case left_bumper: return robot.gamepad2.left_bumper;
                case right_bumper: return robot.gamepad2.right_bumper;
                case left_stick_button: return robot.gamepad2.left_stick_button;
                case right_stick_button: return robot.gamepad2.right_stick_button;
                case bool_left_trigger: return robot.gamepad2.left_trigger > inputs.get(buttonName).deadzone;
                case bool_right_trigger: return robot.gamepad2.right_trigger > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_up: return -robot.gamepad2.left_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_x_left: return robot.gamepad2.left_stick_x < inputs.get(buttonName).deadzone;
                case bool_left_stick_x_right: return robot.gamepad2.left_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_up: return -robot.gamepad2.right_stick_y > inputs.get(buttonName).deadzone;
                case bool_left_stick_y_down: return -robot.gamepad2.left_stick_y < -inputs.get(buttonName).deadzone;
                case bool_right_stick_x_left: return robot.gamepad2.right_stick_x < inputs.get(buttonName).deadzone;
                case bool_right_stick_x_right: return robot.gamepad2.right_stick_x > inputs.get(buttonName).deadzone;
                case bool_right_stick_y_down: return -robot.gamepad2.right_stick_y < -inputs.get(buttonName).deadzone;
                case bool_left_stick_x: return Math.abs(robot.gamepad2.left_stick_x) > inputs.get(buttonName).deadzone;
                case bool_left_stick_y: return Math.abs(robot.gamepad2.left_stick_y) > inputs.get(buttonName).deadzone;
                case bool_right_stick_x: return Math.abs(robot.gamepad2.right_stick_x) > inputs.get(buttonName).deadzone;
                case bool_right_stick_y: return Math.abs(robot.gamepad2.right_stick_y) > inputs.get(buttonName).deadzone;

                default:
                    return defaultReturn;
            }
        }
    }

    /**
     * Returns double input of button.
     *
     * @param buttonName - Key of button to get input from.
     */
    public double getDoubleInput(String buttonName) {
        if (inputs.get(buttonName).gamepadNumber == 1) {
            switch ((Button.DoubleInputs) inputs.get(buttonName).getInputEnum()) {
                case left_stick_x: return robot.gamepad1.left_stick_x;
                case left_trigger: return robot.gamepad1.left_trigger;
                case left_stick_y: return -robot.gamepad1.left_stick_y;
                case right_stick_x: return robot.gamepad1.right_stick_x;
                case right_trigger: return robot.gamepad1.right_trigger;
                case right_stick_y: return -robot.gamepad1.right_stick_y;

                default:
                    return 0;
            }
        } else {
            switch ((Button.DoubleInputs) inputs.get(buttonName).getInputEnum()) {
                case left_stick_x: return robot.gamepad2.left_stick_x;
                case left_trigger: return robot.gamepad2.left_trigger;
                case left_stick_y: return -robot.gamepad2.left_stick_y;
                case right_stick_x: return robot.gamepad2.right_stick_x;
                case right_trigger: return robot.gamepad2.right_trigger;
                case right_stick_y: return -robot.gamepad2.right_stick_y;

                default:
                    return 0;
            }
        }
    }

    /**
     * Returns double input of button with a set defaultReturn.
     *
     * @param buttonName - Key of button to get input from.
     * @param defaultReturn - Default return.
     */
    public double getDoubleInput(String buttonName, double defaultReturn) {
        if (inputs.get(buttonName).gamepadNumber == 1) {
            switch ((Button.DoubleInputs) inputs.get(buttonName).getInputEnum()) {
                case left_stick_x: return robot.gamepad1.left_stick_x;
                case left_trigger: return robot.gamepad1.left_trigger;
                case left_stick_y: return -robot.gamepad1.left_stick_y;
                case right_stick_x: return robot.gamepad1.right_stick_x;
                case right_trigger: return robot.gamepad1.right_trigger;
                case right_stick_y: return -robot.gamepad1.right_stick_y;

                default:
                    return defaultReturn;
            }
        } else {
            switch ((Button.DoubleInputs) inputs.get(buttonName).getInputEnum()) {
                case left_stick_x: return robot.gamepad2.left_stick_x;
                case left_trigger: return robot.gamepad2.left_trigger;
                case left_stick_y: return -robot.gamepad2.left_stick_y;
                case right_stick_x: return robot.gamepad2.right_stick_x;
                case right_trigger: return robot.gamepad2.right_trigger;
                case right_stick_y: return -robot.gamepad2.right_stick_y;

                default:
                    return defaultReturn;
            }
        }
    }

    public Vector getVectorInput(String buttonName) {
        if(inputs.get(buttonName).gamepadNumber == 1) {
            switch ((Button.VectorInputs) inputs.get(buttonName).getInputEnum()) {
                case left_stick: return new Vector(robot.gamepad1.left_stick_x, -robot.gamepad1.left_stick_y);
                case right_stick: return new Vector(robot.gamepad1.right_stick_x, -robot.gamepad1.right_stick_y);

                default:
                    return new Vector(0,0);
            }
        }
        else {
            switch ((Button.VectorInputs) inputs.get(buttonName).getInputEnum()) {
                case left_stick: return new Vector(robot.gamepad2.left_stick_x, -robot.gamepad2.left_stick_y);
                case right_stick: return new Vector(robot.gamepad2.right_stick_x, -robot.gamepad2.right_stick_y);

                default:
                    return new Vector(0,0);
            }
        }
    }

    /**
     * Gets a button object from the gamepad.
     *
     * @param name - The name of the button.
     * @return - The button object corresponding to that name.
     */
    public Button getButton(String name) {
        return inputs.get(name);
    }

}

