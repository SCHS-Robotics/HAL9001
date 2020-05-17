package com.SCHSRobotics.HAL9001.system.source.GUI;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Gui class for drawing and handling menus. Think of it like the robot.java class but for graphics.
 *
 * @author Cole Savage, Level Up
 * @author Dylan Zueck, Crow Force
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/20/19
 */
@SuppressWarnings({"WeakerAccess","unused"})
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

    private static GUI INSTANCE = new GUI();

    private GUI() {
        menus = new HashMap<>();
        menuKeys = new ArrayList<>();

        cursorBlinkState = 0;
        lastRenderTime = 0;
        flag = false;
        cycle = false;
        activeMenuIdx = 0;
    }

    public static void setup(Robot robot, Button<Boolean> cycleButton) {
        INSTANCE.robot = robot;

        INSTANCE.inputs = new CustomizableGamepad(robot);
        INSTANCE.inputs.addButton(CYCLE_MENUS, cycleButton);

        INSTANCE.lastBlinkTimeMs = System.currentTimeMillis();
        robot.telemetry.setAutoClear(false);
        robot.telemetry.setMsTransmissionInterval(50);
    }

    @Contract(pure = true)
    public static GUI getInstance() {
        return INSTANCE;
    }

    /**
     * Runs the init() function for every menu contained in the GUI.
     *
     * @throws NullPointerException Throws this exception if the active menu requested does not exist.
     */
    public final void start() {
        if(menus.size() > 0){
            Menu menu = menus.get(menuKeys.get(activeMenuIdx));
            ExceptionChecker.assertNonNull(menu, new NullPointerException("Requested Active Menu does not exist."));
            cursor = menu.cursor;
        }
        for(Menu m : menus.values()) {
            m.init();
        }
    }

    /**
     * Runs the onStart() function for every menu contained in the GUI.
     */
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
            boolean cycleMenus = inputs.getInput(CYCLE_MENUS);
            if (cycleMenus && flag) {
                activeMenuIdx++;
                activeMenuIdx = activeMenuIdx % menuKeys.size();
                setActiveMenu(menuKeys.get(activeMenuIdx));
                cursor.cursorUpdated = true;
                flag = false;
                cycle = true;
            } else if (!cycleMenus && !flag) {
                flag = true;
                cycle = false;
            }
            else {
                cycle = false;
            }

            if (System.currentTimeMillis() - lastRenderTime >= cursor.getBlinkSpeedMs() || cursor.cursorUpdated || cycle) {

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
            boolean cycleMenus = inputs.getInput(CYCLE_MENUS);
            if (cycleMenus && flag) {
                activeMenuIdx++;
                activeMenuIdx = activeMenuIdx % menuKeys.size();
                setActiveMenu(menuKeys.get(activeMenuIdx));
                cursor.cursorUpdated = true;
                flag = false;
                cycle = true;
            } else if (!cycleMenus && !flag) {
                flag = true;
                cycle = false;
            }
            else {
                cycle = false;
            }

            if (System.currentTimeMillis() - lastRenderTime >= cursor.getBlinkSpeedMs() || cursor.cursorUpdated || cycle) {

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
    public void addMenu(@NotNull String name, @NotNull Menu menu){
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
    public void removeMenu(@NotNull String name) {

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
     * @throws NullPointerException Throws this exception when the requested active menu does not exist.
     */
    public void setActiveMenu(@NotNull String menuName){

        this.activeMenu = menus.get(menuName);
        this.activeMenuIdx = menuKeys.indexOf(menuName);
        Menu menu = menus.get(menuName);
        ExceptionChecker.assertNonNull(menu, new NullPointerException("Requested active menu does not exist."));

        menu.open();
        cursor = menu.cursor;
    }

    /**
     * Causes the cursor to blink on a specified line.
     *
     * @param line - The line object where the cursor is currently located.
     */
    private void blinkCursor(@NotNull GuiLine line) {
        char[] chars = line.getSelectionZoneText().toCharArray();

        if(System.currentTimeMillis() - lastBlinkTimeMs >= cursor.getBlinkSpeedMs()) {
            cursorBlinkState++;
            cursorBlinkState = cursorBlinkState % 2;
            lastBlinkTimeMs = System.currentTimeMillis();
        }

        if(!cursor.doBlink || !cursor.forceCursorChar) {
            cursorBlinkState = 1;
        }

        if(chars.length != 0) {
            char drawChar = cursorBlinkState == 0 ? cursor.getCursorIcon() : chars[cursor.getX()];
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
    protected void displayLine(@NotNull GuiLine line, int lineNumber){
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
    public Menu getMenu(@NotNull String menuName) {
        return menus.get(menuName);
    }

    /**
     * Overrides the button used to cycle through menus.
     *
     * @param cycleButton - The button that will be used to cycle through menus.
     */
    public void overrideCycleButton(@NotNull Button<Boolean> cycleButton) {
        inputs.removeButton(CYCLE_MENUS);
        inputs.addButton(CYCLE_MENUS,cycleButton);
    }

    /**
     * Returns if a menu with the provided name is present in the GUI.
     *
     * @param menuName - The name of the menu to search for.
     * @return Whether or not the menu is in the GUI.
     */
    public boolean isMenuPresent(@NotNull String menuName) {
        return menuKeys.contains(menuName);
    }
}