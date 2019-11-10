/*
 * Filename: GUI.java
 * Author: Cole Savage and Dylan Zueck
 * Team Name: Level Up, Crow Force
 * Date: 7/20/19
 */

package com.SCHSRobotics.HAL9001.system.source.GUI;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.util.exceptions.NotBooleanInputException;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Gui class for drawing and handling menus. Think of it like the robot.java class but for graphics.
 */
public class GUI {

    //The current active menu.
    private Menu activeMenu;
    //The hashmap mapping the name of a menu to the actual menu object.
    private Map<String,Menu> menus;
    //The robot using th GUI.
    public Robot robot;
    //The cursor the GUI will use in the menus.
    private Cursor cursor;
    //A boolean value that becomes true when the user attempts to cycle menus.
    private boolean cycle;
    //The character to be drawn to the screen at the coordinates of the cursor.
    private char drawChar;
    //The time at which the last render action occurred in milliseconds.
    private long lastRenderTime;
    //The current state of the cursor's blinking and the index of the active menu in a list of the hashmap's values.
    private int cursorBlinkState, activeMenuIdx;
    //The timestamp of the last blink.
    private double lastBlinkTimeMs;
    //A list of all menu names currently in the GUI.
    private ArrayList<String> menuKeys;
    //Used in a three way toggle to make the cursor blink, I apologize in advance.
    private boolean flag;
    //The customizable gamepad used to customize inputs to the GUI.
    private CustomizableGamepad inputs;
    //The name of the cycle menus button.
    private static final String CYCLE_MENUS = "CycleMenus";

    /**
     * Constructor for GUI.
     *
     * @param robot - The robot using the instance of GUI.
     * @param flipMenu - The button used to cycle between multiple stored menus.
     *
     * @throws NotBooleanInputException - Throws an exception if button does not return boolean values.
     */
    public GUI(Robot robot, Button flipMenu) {
        this.robot = robot;
        this.menus = new HashMap<>();

        this.inputs = new CustomizableGamepad(robot);

        menuKeys = new ArrayList<>();
        
        if(flipMenu.isBoolean) {
            this.inputs.addButton(CYCLE_MENUS, flipMenu);
        }
        else {
            throw new NotBooleanInputException("A non-boolean input was passed to the controller as a boolean input");
        }

        cursorBlinkState = 0;
        lastBlinkTimeMs = System.currentTimeMillis();
        lastRenderTime = 0;
        flag = false;
        cycle = false;
        activeMenuIdx = 0;

        robot.telemetry.setAutoClear(false);
        robot.telemetry.setMsTransmissionInterval(50);
    }

    /**
     * Runs the init() function for every menu contained in the GUI.
     */
    public final void start(){
        if(menus.size() > 0){
            cursor = menus.get(menuKeys.get(activeMenuIdx)).cursor;
        }
        for(Menu m : menus.values()) {
            m.init();
        }
    }

    public final void onStart() {
        for(Menu m : menus.values()) {
            m.onStart();
        }
    }

    /**
     * Draws the current active menu to the screen.
     */
    public void drawCurrentMenu(){
        if(menus.size() != 0) {
            cursor.update();
            if (inputs.getBooleanInput(CYCLE_MENUS) && flag) {
                activeMenuIdx++;
                activeMenuIdx = activeMenuIdx % menuKeys.size();
                setActiveMenu(menuKeys.get(activeMenuIdx));
                cursor.cursorUpdated = true;
                flag = false;
                cycle = true;
            } else if (!inputs.getBooleanInput(CYCLE_MENUS) && !flag) {
                flag = true;
                cycle = false;
            }
            else {
                cycle = false;
            }

            if (System.currentTimeMillis() - lastRenderTime >= cursor.blinkSpeedMs || cursor.cursorUpdated || cycle) {

                if (cursor.cursorUpdated) {
                    cursorBlinkState = 0;
                }

                clearScreen();
                activeMenu.render();
                robot.telemetry.update();
                lastRenderTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * Draws the current active menu to the screen.
     */
    public void drawCurrentMenuInit(){
        if(menus.size() != 0) {
            cursor.update();
            if (inputs.getBooleanInput(CYCLE_MENUS) && flag) {
                activeMenuIdx++;
                activeMenuIdx = activeMenuIdx % menuKeys.size();
                setActiveMenu(menuKeys.get(activeMenuIdx));
                cursor.cursorUpdated = true;
                flag = false;
                cycle = true;
            } else if (!inputs.getBooleanInput(CYCLE_MENUS) && !flag) {
                flag = true;
                cycle = false;
            }
            else {
                cycle = false;
            }

            if (System.currentTimeMillis() - lastRenderTime >= cursor.blinkSpeedMs || cursor.cursorUpdated || cycle) {

                if (cursor.cursorUpdated) {
                    cursorBlinkState = 0;
                }

                clearScreen();
                activeMenu.initLoopRender();
                robot.telemetry.update();
                lastRenderTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * Runs the stop function for every menu contained in the GUI.
     */
    public void stop() {
        for (Menu m : menus.values()) {
            m.stop();
        }
        clearScreen();
    }

    /**
     * Adds a menu to the GUI.
     *
     * @param name - The menu object to be added.
     * @param menu - The name of the menu.
     */
    public void addMenu(String name, Menu menu){
        menus.put(name, menu);
        menuKeys.add(name);
        if(menus.size() == 1){
            setActiveMenu(name);
        }
    }

    /**
     * Removes a menu from the GUI.
     *
     * @param name - The name of the menu to be removed.
     */
    public void removeMenu(String name) {

        if(name.equals(menuKeys.get(activeMenuIdx))) {
            robot.telemetry.clearAll();
            activeMenu.displayNothing();
            robot.telemetry.update();
        }

        if(menuKeys.indexOf(name) > activeMenuIdx && activeMenuIdx != menuKeys.size()-1){
            activeMenuIdx--;
        }
        menuKeys.remove(name);
        menus.remove(name);

        activeMenuIdx = menus.size() != 0 ? activeMenuIdx % menuKeys.size() : 0;
        if(menus.size() != 0) {
            setActiveMenu(menuKeys.get(activeMenuIdx));
        }
        cycle = true;
    }

    /**
     * Sets the active menu.
     *
     * @param menuName - The name of the menu to be set as the active menu.
     */
    public void setActiveMenu(String menuName){

        this.activeMenu = menus.get(menuName);
        this.activeMenuIdx = menuKeys.indexOf(menuName);
        menus.get(menuName).open();
        cursor = menus.get(menuName).cursor;
    }

    /**
     * Causes the cursor to blink on a specified line.
     *
     * @param line - The line object where the cursor is currently located.
     */
    private void blinkCursor(GuiLine line) {
        char[] chars = line.selectionZoneText.toCharArray();

        if(System.currentTimeMillis() - lastBlinkTimeMs >= cursor.getBlinkSpeedMs()) {
            cursorBlinkState++;
            cursorBlinkState = cursorBlinkState % 2;
            lastBlinkTimeMs = System.currentTimeMillis();
        }

        if(!cursor.doBlink || !cursor.forceCursorChar) {
            cursorBlinkState = 1;
        }

        if(chars.length != 0) {
            drawChar = cursorBlinkState == 0 ? cursor.getCursorIcon() : chars[cursor.getX()];
            chars[cursor.getX()] = drawChar;
        }

        robot.telemetry.addLine(line.FormatSelectionZoneText(new String(chars)));
    }

    /**
     * Clears the screen.
     */
    protected void clearScreen() {
        robot.telemetry.clearAll();
        robot.telemetry.update();
    }

    /**
     * Adds a single line to the screen.
     *
     * @param line - The line object to display.
     * @param lineNumber - The line number (starts at 0 at the top).
     */
    protected void displayLine(GuiLine line, int lineNumber){
        if(cursor.getY() == lineNumber){
            blinkCursor(line);
        }
        else {
            robot.telemetry.addLine(line.getLineText());
        }
    }

    /**
     * Gets a menu in the GUI by its name.
     *
     * @param menuName - The name of the menu in the GUI.
     * @return - The menu object corresponding to menuName in the GUI.
     */
    public Menu getMenu(String menuName) {
        return menus.get(menuName);
    }

    /**
     * Overrides the button used to cycle through menus.
     *
     * @param cycleButton - The button that will be used to cycle through menus.
     */
    public void overrideCycleButton(Button cycleButton) {
        inputs.removeButton(CYCLE_MENUS);
        inputs.addButton(CYCLE_MENUS,cycleButton);
    }

    /**
     * Returns if a menu with the provided name is present in the GUI.
     *
     * @param menuName - The name of the menu to search for.
     * @return Whether or not the menu is in the GUI.
     */
    public boolean isMenuPresent(String menuName) {
        return menuKeys.contains(menuName);
    }
}
