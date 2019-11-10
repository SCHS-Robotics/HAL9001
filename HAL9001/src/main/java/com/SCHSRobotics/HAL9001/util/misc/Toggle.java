/*
 * Filename: Toggle.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/17/19
 */

package com.SCHSRobotics.HAL9001.util.misc;

/**
 * A toggle class used to add toggle switches.
 */
public class Toggle {


    public enum ToggleTypes{
          flipToggle, trueOnceToggle, trueOnceToggleAllowTurnOff, trueWhileHeldOnce
    }

    //Boolean values representing the current state of the toggle and whether the toggling button has been released.
    private boolean currentState, flag;

    private ToggleTypes toggleType;

    /**
     * Ctor for toggle class.
     * 
     * @param currentState - Initial toggle state.
     */
    public Toggle(ToggleTypes toggleType, boolean currentState){
        this.currentState = currentState;
        this.toggleType = toggleType;
        flag = true;
    }

    /**
     * Inverts current state if condition changes from false to true from the previous to the current function call.
     *
     * @param condition - The most recent state of the toggling condition.
     */
    public void updateToggle(boolean condition){
        switch(toggleType) {
            case flipToggle:
            case trueOnceToggleAllowTurnOff:
                if (condition && flag) { //when false turns to true
                    currentState = !currentState;
                    flag = false;
                } else if (!condition && !flag) { //when true turns to false
                    flag = true;
                }
                break;
            case trueOnceToggle:
                if(condition && flag){
                    currentState = true;
                    flag = false;
                }
                else if(!condition && !flag){
                    flag = true;
                }
                break;
            case trueWhileHeldOnce:
                if(condition && flag){
                    currentState = true;
                    flag = false;
                }
                else if(!condition && !flag){
                    currentState = false;
                    flag = true;
                }
                break;
        }
    }

    /**
     * Gets current state of the toggle
     *
     * @return - Returns current toggle state
     */
    public boolean getCurrentState(){
        switch(toggleType) {
            case flipToggle:
                return currentState;
            case trueOnceToggleAllowTurnOff:
            case trueOnceToggle:
            case trueWhileHeldOnce:
                if(currentState) {
                    currentState = false;
                    return true;
                }
                return false;
        }
        return false;
    }

    public static interface Params {
    }
}