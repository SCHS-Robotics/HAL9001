package com.SCHSRobotics.HAL9001.util.misc;

import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.exceptions.NotARealGamepadException;

import org.jetbrains.annotations.NotNull;

/**
 * A class representing a button on the gamepad.
 *
 * @author Dylan Zueck, Crow Force
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/20/19
 */
@SuppressWarnings({"WeakerAccess","unused"})
public class Button {

    //IsBoolean is ture if is is a boolean input button, isDouble is true if it is a double input button.
    public boolean isBoolean, isDouble, isVector;
    //Number of gamepad to use 1 or 2.
    private int gamepadNumber;
    //Double input to use if it is a double input button.
    private DoubleInputs doubleInput;
    //Boolean input to use if it is a boolean input button.
    private BooleanInputs booleanInput;
    //Vector input to use if it is a vector input.
    private VectorInputs vectorInput;
    //Deadzone to use for the boolean version of the double inputs.
    private double deadzone = 0;

    /**
     * Represents the allowed input methods for controls that return double values.
     */
    public enum DoubleInputs {
        left_stick_x, left_stick_y, left_trigger, right_stick_x, right_stick_y, right_trigger, noButton
    }

    /**
     * Represents the allowed input methods for controls that return boolean values.
     */
    public enum BooleanInputs {
        a, b, back, dpad_down, dpad_left, dpad_right, dpad_up, guide, left_bumper, left_stick_button, right_bumper, right_stick_button, start, x, y, bool_left_stick_x, bool_right_stick_x, bool_left_stick_y, bool_right_stick_y, bool_left_trigger, bool_right_trigger, bool_left_stick_x_right, bool_right_stick_x_right, bool_left_stick_y_up, bool_right_stick_y_up, bool_left_stick_x_left, bool_right_stick_x_left, bool_left_stick_y_down, bool_right_stick_y_down, noButton
    }

    public enum VectorInputs {
        left_stick, right_stick, noButton
    }

    /**
     * Constructor for button that makes a double button.
     *
     * @param gamepadNumber Number of gamepad this button will use.
     * @param inputName DoubleInput that this button will output.
     */
    public Button(int gamepadNumber, @NotNull DoubleInputs inputName){
        this(gamepadNumber,inputName,0.0);
    }

    /**
     * Constructor for button that makes a double button.
     *
     * @param gamepadNumber Number of gamepad this button will use.
     * @param inputName DoubleInput that this button will output.
     * @param deadzone Double between 0 and 1 that sets the deadzone.
     */
    public Button(int gamepadNumber, @NotNull DoubleInputs inputName, double deadzone){
        this.gamepadNumber = gamepadNumber;
        this.doubleInput = inputName;
        this.isDouble = true;
        this.isBoolean = false;
        this.isVector = false;
        this.deadzone = deadzone;

    }

    /**
     * Constructor for button that makes a boolean button.
     *
     * @param gamepadNumber Number of gamepad this button will use.
     * @param inputName BooleanInput that this button will output.
     */
    public Button(int gamepadNumber, @NotNull BooleanInputs inputName){
        this(gamepadNumber, inputName, 0.0);
    }

    /**
     * Constructor for button that makes a boolean button.
     *
     * @param gamepadNumber Number of gamepad this button will use.
     * @param inputName BooleanInput that this button will output.
     * @param deadzone Double between 0 and 1 that sets the deadzone.
     */
    public Button(int gamepadNumber, @NotNull BooleanInputs inputName, double deadzone){
        this.gamepadNumber = gamepadNumber;
        this.booleanInput = inputName;
        this.isDouble = false;
        this.isBoolean = true;
        this.isVector = false;
        this.deadzone = deadzone;
    }

    /**
     * Constructor for button that makes a vector button.
     * 
     * @param gamepadNumber Number of gamepad this button will use.
     *  @param inputName VectorInput that this button will output.
     */
    public Button(int gamepadNumber, @NotNull VectorInputs inputName){
        this.gamepadNumber = gamepadNumber;
        this.vectorInput = inputName;
        this.isDouble = false;
        this.isBoolean = false;
        this.isVector = true;
    }

    /**
     * Returns the enum for this button.
     */
    public Enum getInputEnum(){
        if(isBoolean){
            return booleanInput;
        }
        else if(isDouble){
            return doubleInput;
        }
        else {
            return vectorInput;
        }
    }

    public boolean isBoolean() {
        return isBoolean;
    }

    public boolean isDouble() {
        return isDouble;
    }

    public boolean isVector() {
        return isVector;
    }

    public int getGamepadNumber() {
        return gamepadNumber;
    }

    public double getDeadzone() {
        return deadzone;
    }

    /**
     * Sets the deadzone for the boolean version of the double inputs.
     *
     * @param deadzone Double between 0 and 1 that sets the deadzone.
     */
    public void setDeadzone(double deadzone){
        this.deadzone = deadzone;
    }

    public void setGamepadNumber(int gamepadNumber) {
        ExceptionChecker.assertTrue(gamepadNumber == 1 || gamepadNumber == 2, new NotARealGamepadException("You must use either gamepad 1 or gamepad 2."));
        this.gamepadNumber = gamepadNumber;
    }
}