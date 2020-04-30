package com.SCHSRobotics.HAL9001.util.misc;

import android.util.Log;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.exceptions.NotAnAlchemistException;
import com.SCHSRobotics.HAL9001.util.math.Vector2D;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A intermediary class between the robot and the gamepad controls that allows all control systems to be customized.
 *
 * @author Dylan Zueck, Crow Force
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/18/19
 */
@SuppressWarnings("unused")
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
    public CustomizableGamepad(@NotNull Robot robot) {
        this.robot = robot;
        this.inputs = new HashMap<>();
    }

    /**
     * Adds a double input/Button.
     *
     * @param buttonName Key that will be used to get the input of the button.
     * @param button Enum for double input wanted.
     * @param gamepadNumber Number of gamepad this button will use.
     */
    public void addButton(@NotNull String buttonName, @NotNull Button.DoubleInputs button, int gamepadNumber) {
        inputs.put(buttonName, new Button<Double>(gamepadNumber, button));
    }

    /**
     * Adds a boolean input/Button.
     *
     * @param buttonName Key that will be used to get the input of the button.
     * @param button Enum for boolean input wanted.
     * @param gamepadNumber Number of gamepad this button will use.
     */
    public void addButton(@NotNull String buttonName, @NotNull Button.BooleanInputs button, int gamepadNumber) {
        inputs.put(buttonName, new Button<Boolean>(gamepadNumber, button));
    }

    /**
     * Adds a input/Button.
     *
     * @param buttonName Key that will be used to get the input of the button.
     * @param button Button object that relates to an input.
     */
    public void addButton(@NotNull String buttonName, @NotNull Button<?> button) {
        inputs.put(buttonName, button);
    }

    /**
     * Adds a boolean input/Button with deadzone.
     *
     * @param buttonName Key that will be used to get the input of the button.
     * @param button Enum for boolean input wanted.
     * @param gamepadNumber Number of gamepad this button will use.
     * @param deadzone deadzone for boolean version of double inputs.
     */
    public void addButton(@NotNull String buttonName, @NotNull Button.BooleanInputs button, int gamepadNumber, double deadzone) {
        Button<Button> buttonToAdd = new Button<>(gamepadNumber, button);
        buttonToAdd.setDeadzone(deadzone);
        inputs.put(buttonName, buttonToAdd);
    }

    /**
     * Adds a input/Button with deadzone (deadzone does nothing for double inputs).
     *
     * @param buttonName Key that will be used to get the input of the button.
     * @param button Button object that relates to an input.
     */
    public void addButton(@NotNull String buttonName, @NotNull Button<?> button, double deadzone) {
        button.setDeadzone(deadzone);
        inputs.put(buttonName, button);
    }

    /**
     * Removes a input/Button.
     *
     * @param buttonName Key of button to be removed.
     */
    public void removeButton(@NotNull String buttonName) {
        inputs.remove(buttonName);
    }

    /**
     * Returns if a button is set to noButton.
     *
     * @param buttonName Key of button to be checked.
     * @return Whether the button is of nobutton type.
     */
    @SuppressWarnings("all")
    public boolean checkNoButton(@NotNull String buttonName) {
        Button<?> button = getButton(buttonName);
        return button.getInputEnum() == Button.DoubleInputs.noButton || button.getInputEnum() == Button.BooleanInputs.noButton || button.getInputEnum() == Button.VectorInputs.noButton;
    }

    private boolean getBooleanInput(@NotNull Button<Boolean> button) {
        return getBooleanInput(button, false);
    }

    private boolean getBooleanInput(Button<Boolean> button, Boolean defaultReturn) {
        if (button.getGamepadNumber() == 1) {
            switch ((Button.BooleanInputs) button.getInputEnum()) {

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
                case bool_left_trigger: return robot.gamepad1.left_trigger > button.getDeadzone();
                case bool_right_trigger: return robot.gamepad1.right_trigger > button.getDeadzone();
                case bool_left_stick_y_up: return -robot.gamepad1.left_stick_y > button.getDeadzone();
                case bool_left_stick_x_left: return robot.gamepad1.left_stick_x < -button.getDeadzone();
                case bool_left_stick_x_right: return robot.gamepad1.left_stick_x > button.getDeadzone();
                case bool_right_stick_y_up: return -robot.gamepad1.right_stick_y > button.getDeadzone();
                case bool_left_stick_y_down: return -robot.gamepad1.left_stick_y < -button.getDeadzone();
                case bool_right_stick_x_left: return robot.gamepad1.right_stick_x < -button.getDeadzone();
                case bool_right_stick_x_right: return robot.gamepad1.right_stick_x > button.getDeadzone();
                case bool_right_stick_y_down: return -robot.gamepad1.right_stick_y < -button.getDeadzone();
                case bool_left_stick_x: return Math.abs(robot.gamepad1.left_stick_x) > button.getDeadzone();
                case bool_left_stick_y: return Math.abs(robot.gamepad1.left_stick_y) > button.getDeadzone();
                case bool_right_stick_x: return Math.abs(robot.gamepad1.right_stick_x) > button.getDeadzone();
                case bool_right_stick_y: return Math.abs(robot.gamepad1.right_stick_y) > button.getDeadzone();

                default:
                    return defaultReturn;
            }
        } else {
            switch ((Button.BooleanInputs) button.getInputEnum()) {

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
                case bool_left_trigger: return robot.gamepad2.left_trigger > button.getDeadzone();
                case bool_right_trigger: return robot.gamepad2.right_trigger > button.getDeadzone();
                case bool_left_stick_y_up: return -robot.gamepad2.left_stick_y > button.getDeadzone();
                case bool_left_stick_x_left: return robot.gamepad2.left_stick_x < button.getDeadzone();
                case bool_left_stick_x_right: return robot.gamepad2.left_stick_x > button.getDeadzone();
                case bool_right_stick_y_up: return -robot.gamepad2.right_stick_y > button.getDeadzone();
                case bool_left_stick_y_down: return -robot.gamepad2.left_stick_y < -button.getDeadzone();
                case bool_right_stick_x_left: return robot.gamepad2.right_stick_x < button.getDeadzone();
                case bool_right_stick_x_right: return robot.gamepad2.right_stick_x > button.getDeadzone();
                case bool_right_stick_y_down: return -robot.gamepad2.right_stick_y < -button.getDeadzone();
                case bool_left_stick_x: return Math.abs(robot.gamepad2.left_stick_x) > button.getDeadzone();
                case bool_left_stick_y: return Math.abs(robot.gamepad2.left_stick_y) > button.getDeadzone();
                case bool_right_stick_x: return Math.abs(robot.gamepad2.right_stick_x) > button.getDeadzone();
                case bool_right_stick_y: return Math.abs(robot.gamepad2.right_stick_y) > button.getDeadzone();

                default:
                    return defaultReturn;
            }
        }
    }

    private double getDoubleInput(@NotNull Button<Double> button) {
        return getDoubleInput(button, 0.0);
    }

    private double getDoubleInput(@NotNull Button<Double> button, double defaultReturn) {
        if (button.getGamepadNumber() == 1) {
            switch ((Button.DoubleInputs) button.getInputEnum()) {
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
            switch ((Button.DoubleInputs) button.getInputEnum()) {
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

    private Vector2D getVectorInput(@NotNull Button<Vector2D> button, Vector2D defaultReturn) {
        if(button.getGamepadNumber() == 1) {
            switch ((Button.VectorInputs) button.getInputEnum()) {
                case left_stick: return new Vector2D(robot.gamepad1.left_stick_x, -robot.gamepad1.left_stick_y);
                case right_stick: return new Vector2D(robot.gamepad1.right_stick_x, -robot.gamepad1.right_stick_y);

                default:
                    return defaultReturn;
            }
        }
        else {
            switch ((Button.VectorInputs) button.getInputEnum()) {
                case left_stick: return new Vector2D(robot.gamepad2.left_stick_x, -robot.gamepad2.left_stick_y);
                case right_stick: return new Vector2D(robot.gamepad2.right_stick_x, -robot.gamepad2.right_stick_y);

                default:
                    return defaultReturn;
            }
        }
    }

    private Vector2D getVectorInput(@NotNull Button<Vector2D> button) {
        return getVectorInput(button, new Vector2D(0,0));
    }

    @SuppressWarnings("unchecked")
    public <T> T getInput(String buttonName, T defaultReturn) {

        Button<T> button = getButton(buttonName);

        if(button.isBoolean()) {
            boolean defaultVal = (Boolean) defaultReturn;
            boolean val = getBooleanInput((Button<Boolean>) button, defaultVal);
            return (T) Boolean.valueOf(val);
        }
        else if(button.isDouble()) {
            double defaultVal = (Double) defaultReturn;
            double val = getDoubleInput((Button<Double>) button, defaultVal);
            return (T) Double.valueOf(val);
        }
        else if(button.isVector()){
            Vector2D defaultVal = (Vector2D) defaultReturn;
            Vector2D val = getVectorInput((Button<Vector2D>) button, defaultVal);
            return (T) val;
        }
        else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getInput(String buttonName) {
        Button<T> button = getButton(buttonName);

        if(button.isBoolean()) {
            boolean val = getBooleanInput((Button<Boolean>) button);
            return (T) Boolean.valueOf(val);
        }
        else if(button.isDouble()) {
            double val = getDoubleInput((Button<Double>) button);
            return (T) Double.valueOf(val);
        }
        else if(button.isVector()){
            Vector2D val = getVectorInput((Button<Vector2D>) button);
            return (T) val;
        }
        else {
            return null;
        }
    }

    /**
     * Gets a button object from the gamepad.
     *
     * @param buttonName The name of the button.
     * @return The button object corresponding to that name.
     */
    @SuppressWarnings("unchecked")
    public <T> Button<T> getButton(@NotNull String buttonName) {
        T testVal = null;
        try {
            testVal = (T) Boolean.valueOf(false);
            Log.i("HAL9001","boolean conversion test passed");
        }
        catch (ClassCastException e) {
            Log.i("HAL9001","boolean conversion test failed");
        }
        try {
            testVal = (T) Double.valueOf(0);
            Log.i("HAL9001","double conversion test passed");
        }
        catch (ClassCastException e) {
            Log.i("HAL9001","double conversion test failed");
        }
        try {
            testVal = (T) new Vector2D(0,0);
            Log.i("HAL9001","vector conversion test passed");
        }
        catch (ClassCastException e) {
            Log.i("HAL9001","vector conversion test failed");
        }
        ExceptionChecker.assertNonNull(testVal,new DumpsterFireException("Invalid return datatype :("));

        Button<T> button;
        try {
            button = inputs.get(buttonName);
        }
        catch (ClassCastException e) {
            throw new NotAnAlchemistException("Illegal button type conversion detected");
        }
        ExceptionChecker.assertNonNull(button, new NullPointerException("Could not find a button with name "+buttonName+" in the customizable gamepad."));
        return button;
    }
}