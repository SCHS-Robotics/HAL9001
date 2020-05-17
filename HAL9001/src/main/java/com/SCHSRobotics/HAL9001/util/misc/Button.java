package com.SCHSRobotics.HAL9001.util.misc;

import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.exceptions.NotARealGamepadException;
import com.SCHSRobotics.HAL9001.util.exceptions.NotBooleanInputException;
import com.SCHSRobotics.HAL9001.util.exceptions.NotDoubleInputException;
import com.SCHSRobotics.HAL9001.util.exceptions.NotVectorInputException;
import com.SCHSRobotics.HAL9001.util.math.Vector2D;

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
public class Button<T> {

    //IsBoolean is true if is is a boolean input button, isDouble is true if it is a double input button.
    private boolean isBoolean, isDouble, isVector;
    //Number of gamepad to use 1 or 2.
    private int gamepadNumber;
    //Double input to use if it is a double input button.
    private DoubleInputs doubleInput;
    //Boolean input to use if it is a boolean input button.
    private BooleanInputs booleanInput;
    //Vector2D input to use if it is a vector input.
    private VectorInputs vectorInput;
    //Deadzone to use for the boolean version of the double inputs.
    private double deadzone = 0;

    private T type;

    /**
     * Represents the allowed input methods for controls that return double values.
     */
    public enum DoubleInputs {
        left_stick_x, left_stick_y, left_trigger,
        right_stick_x, right_stick_y, right_trigger, noButton
    }

    /**
     * Represents the allowed input methods for controls that return boolean values.
     */
    public enum BooleanInputs {
        a, b, back, dpad_down, dpad_left, dpad_right, dpad_up, guide,
        left_bumper, left_stick_button, right_bumper, right_stick_button,
        start, x, y, bool_left_stick_x, bool_right_stick_x, bool_left_stick_y,
        bool_right_stick_y, bool_left_trigger, bool_right_trigger,
        bool_left_stick_x_right, bool_right_stick_x_right, bool_left_stick_y_up,
        bool_right_stick_y_up, bool_left_stick_x_left, bool_right_stick_x_left,
        bool_left_stick_y_down, bool_right_stick_y_down, noButton
    }

    /**
     * Represents the allowed input methods for controls that return vector values.
     */
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
    @SuppressWarnings("unchecked")
    public Button(int gamepadNumber, @NotNull DoubleInputs inputName, double deadzone){
        setGamepadNumber(gamepadNumber);
        doubleInput = inputName;
        isDouble = true;
        isBoolean = false;
        isVector = false;
        this.deadzone = deadzone;

        try {
            T testVal = (T) Double.valueOf(0);
        }
        catch (ClassCastException e) {
            throw new NotDoubleInputException("Constructor for Double button was used for a button of non-double type");
        }
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
    @SuppressWarnings("unchecked")
    public Button(int gamepadNumber, @NotNull BooleanInputs inputName, double deadzone){
        setGamepadNumber(gamepadNumber);
        booleanInput = inputName;
        isDouble = false;
        isBoolean = true;
        isVector = false;
        this.deadzone = deadzone;

        try {
            T testVal = (T) Boolean.valueOf(false);
        }
        catch (ClassCastException e) {
            throw new NotBooleanInputException("Constructor for Boolean button was used for a button of non-boolean type");
        }
    }

    /**
     * Constructor for button that makes a vector button.
     * 
     * @param gamepadNumber Number of gamepad this button will use.
     *  @param inputName VectorInput that this button will output.
     */
    @SuppressWarnings("unchecked")
    public Button(int gamepadNumber, @NotNull VectorInputs inputName){
        setGamepadNumber(gamepadNumber);
        vectorInput = inputName;
        isDouble = false;
        isBoolean = false;
        isVector = true;

        try {
            T testVal = (T) new Vector2D(0,0);
        }
        catch (ClassCastException e) {
            throw new NotVectorInputException("Constructor for Vector button was used for a button of non-vector type");
        }
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

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Button<?>) {
            Button<?> button = (Button<?>) obj;
            return this.hashCode() == button.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int enumCode = getInputEnum().hashCode();
        enumCode = enumCode << 3;
        if(isDouble) {
            enumCode |= 1;
        }
        else if(isVector) {
            enumCode |= 2;
        }
        if(gamepadNumber == 1) {
            enumCode |= 4;
        }
        return enumCode;
    }
}