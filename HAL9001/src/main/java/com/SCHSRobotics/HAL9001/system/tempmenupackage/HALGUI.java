package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.misc.Toggle;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

/**
 * A GUI class to control the menu system.
 * This class follows a singleton pattern.
 *
 * @author Cole Savage, Level Up
 * @since 1.1.0
 * @version 1.1.0
 *
 * Creation Date: 4/29/20
 *
 * @see HALMenu
 */
public class HALGUI {
    //A queue to store all currently active trees of menus.
    private Queue<Stack<HALMenu>> menuStacks;
    //A queue to store all controls for the cursor. Controls apply to each menu tree. Controls default to the dpad.
    private Queue<ArrayList<Button<Boolean>>> cursorControlQueue;
    //The current menu stack. Shows history of menus visited within a tree. Does not contain currently active menu.
    private Stack<HALMenu> currentStack;
    //Stores backups of "undo-ed" menus in a tree so that they can be "redo-ed".
    private Stack<HALMenu> forwardStack;
    //The currently selected menu
    private HALMenu currentMenu;
    //The robot the gui uses to send messages to telemetry.
    private Robot robot;
    //The last millisecond timestamp of when the render function was called.
    private long lastRenderTime;
    //The customizable gamepad used to cycle between menu keys.
    private CustomizableGamepad cycleControls;
    //The button used to cycle between menu keys.
    private Button<Boolean> cycleButton;
    //The toggle object used to correctly track button presses.
    private Toggle cycleToggle;
    //The single static instance of the gui.
    private static HALGUI INSTANCE = new HALGUI();

    private Toggle cursorUpToggle, cursorDownToggle, cursorLeftToggle, cursorRightToggle;
    private CustomizableGamepad cursorControls;

    private static final String CURSOR_UP = "CursorUp", CURSOR_DOWN = "CursorDown", CURSOR_LEFT = "CursorLeft", CURSOR_RIGHT = "CursorRight";

    public static final int DEFAULT_TRANSMISSION_INTERVAL_MS = 250;
    public static final int DEFAULT_HAL_TRANSMISSION_INTERVAL_MS = 50;

    /**
     * The private GUI constructor. Initializes the queues, the render timestamp, and the cycle toggle.
     */
    private HALGUI() {}

    /**
     * Gets the static instance of the gui.
     *
     * @return The static instance of the gui.
     */
    @Contract(pure = true)
    public static HALGUI getInstance() {
        return INSTANCE;
    }

    /**
     * Initializes the GUI with a robot and menu-cycling button.
     *
     * @param robot The robot that the gui will use to control the telemetry.
     * @param cycleButton The button used to cycle between menu trees.
     */
    public void setup(Robot robot, Button<Boolean> cycleButton) {
        this.robot = robot;
        cycleControls = new CustomizableGamepad(robot);
        this.cycleButton = cycleButton;
        menuStacks = new PriorityQueue<>();
        cursorControlQueue = new PriorityQueue<>();
        forwardStack = new Stack<>();
        lastRenderTime = 0;
        cycleToggle = new Toggle(Toggle.ToggleTypes.trueOnceToggle, false);
        cursorUpToggle = new Toggle(Toggle.ToggleTypes.trueOnceToggle, false);
        cursorDownToggle = new Toggle(Toggle.ToggleTypes.trueOnceToggle, false);
        cursorLeftToggle = new Toggle(Toggle.ToggleTypes.trueOnceToggle, false);
        cursorRightToggle = new Toggle(Toggle.ToggleTypes.trueOnceToggle, false);
        cursorControls = new CustomizableGamepad(robot);
        robot.telemetry.setMsTransmissionInterval(DEFAULT_HAL_TRANSMISSION_INTERVAL_MS);
    }

    /**
     * Gets the robot the gui is using to control the telemetry.
     *
     * @return The robot that the gui will use to control the telemetry.
     */
    public Robot getRobot() {
        return robot;
    }

    /**
     * Adds a menu at the root of a new menu tree. This creates a new menu tree.
     *
     * @param menu The root menu of the new tree.
     */
    public void addRootMenu(@NotNull HALMenu menu) {
        currentMenu = menu;
        currentStack = new Stack<>();
        forwardStack.clear();
        currentStack.push(currentMenu);
        menuStacks.add(currentStack);
        cursorControlQueue.add(new ArrayList<Button<Boolean>>() {{
            add(new Button<>(1, Button.BooleanInputs.dpad_up));
            add(new Button<>(1, Button.BooleanInputs.dpad_down));
            add(new Button<>(1, Button.BooleanInputs.dpad_left));
            add(new Button<>(1, Button.BooleanInputs.dpad_right));
        }});
        setupCursor();
        currentMenu.init(new Payload());
    }

    /**
     * Adds a new menu to the current tree and displays it. This does not create a new tree, and instead adds on to the existing tree.
     *
     * @param menu The menu to add to the new tree and display.
     */
    public void inflate(@NotNull HALMenu menu) {
        currentStack.push(currentMenu);
        forwardStack.clear();
        currentMenu = menu;
        currentMenu.init(currentMenu.payload);
    }

    /**
     * Renders the currently active menu.
     */
    public void renderCurrentMenu() {
        boolean cursorMoved = false;
        if(!menuStacks.isEmpty()) {
            cursorUpToggle.updateToggle(cursorControls.getInput(CURSOR_UP));
            cursorDownToggle.updateToggle(cursorControls.getInput(CURSOR_DOWN));
            cursorLeftToggle.updateToggle(cursorControls.getInput(CURSOR_LEFT));
            cursorRightToggle.updateToggle(cursorControls.getInput(CURSOR_RIGHT));

            if(cursorUpToggle.getCurrentState()) {
                currentMenu.cursorUp();
                cursorMoved = true;
            }
            else if(cursorDownToggle.getCurrentState()) {
                currentMenu.cursorDown();
                cursorMoved = true;
            }
            else if(cursorLeftToggle.getCurrentState()) {
                currentMenu.cursorLeft();
                cursorMoved = true;
            }
            else if(cursorRightToggle.getCurrentState()) {
                currentMenu.cursorRight();
                cursorMoved = true;
            }
        }

        if(!menuStacks.isEmpty() && System.currentTimeMillis() - lastRenderTime >= currentMenu.getCursorBlinkSpeedMs() || cursorMoved) {
            currentMenu.render();
            robot.telemetry.update();
            lastRenderTime = System.currentTimeMillis();
        }

        cycleToggle.updateToggle(cycleControls.getInput(cycleButton));
        if(cycleToggle.getCurrentState() && !menuStacks.isEmpty()) {
            menuStacks.add(menuStacks.poll());
            currentStack = menuStacks.peek();
            ExceptionChecker.assertNonNull(currentStack, new DumpsterFireException("The stack of menus that you tried to switch to is null. This state should be unreachable, what did you do?!?"));
            currentMenu = currentStack.peek();
            cursorControlQueue.add(cursorControlQueue.poll());
            forwardStack.clear();
        }
    }

    public void setCursorControls(Button<Boolean> upButton, Button<Boolean> downButton, Button<Boolean> leftButton, Button<Boolean> rightButton) {
        ExceptionChecker.assertFalse(upButton.equals(downButton) ||
                upButton.equals(leftButton) ||
                upButton.equals(rightButton) ||
                downButton.equals(leftButton) ||
                downButton.equals(rightButton) ||
                leftButton.equals(rightButton), new DumpsterFireException("All cursor controls must be unique"));

        ArrayList<Button<Boolean>> cursorControlButtons = cursorControlQueue.peek();

        ExceptionChecker.assertNonNull(cursorControlButtons, new NullPointerException("Controls are null."));

        cursorControlButtons.set(0, upButton);
        cursorControlButtons.set(1, downButton);
        cursorControlButtons.set(2, leftButton);
        cursorControlButtons.set(3, rightButton);

        cursorControls.removeButton(CURSOR_UP);
        cursorControls.removeButton(CURSOR_DOWN);
        cursorControls.removeButton(CURSOR_LEFT);
        cursorControls.removeButton(CURSOR_RIGHT);

        cursorControls.addButton(CURSOR_UP, upButton);
        cursorControls.addButton(CURSOR_DOWN, downButton);
        cursorControls.addButton(CURSOR_LEFT, leftButton);
        cursorControls.addButton(CURSOR_RIGHT, rightButton);
    }

    public void back(@NotNull Payload payload) {
        if(currentStack.size() > 1) {
            forwardStack.push(currentStack.pop());
            currentMenu = currentStack.peek();
            currentMenu.init(payload);
        }
    }

    public void back() {
        back(new Payload());
    }

    public void forward(@NotNull Payload payload) {
        if(!forwardStack.isEmpty()) {
            currentStack.push(forwardStack.pop());
            currentMenu = currentStack.peek();
            currentMenu.init(payload);
        }
    }

    public void forward() {
        forward(new Payload());
    }

    public void stop() {
        robot.telemetry.setMsTransmissionInterval(DEFAULT_TRANSMISSION_INTERVAL_MS);
        currentStack = null;
        currentMenu = null;
        robot = null;
        cycleControls = null;
        cycleButton = null;
    }

    private void setupCursor() {
        List<Button<Boolean>> cursorControlButtons = cursorControlQueue.peek();
        ExceptionChecker.assertNonNull(cursorControlButtons, new NullPointerException("Control queue is null."));
        cursorControls.addButton(CURSOR_UP, cursorControlButtons.get(0));
        cursorControls.addButton(CURSOR_DOWN, cursorControlButtons.get(1));
        cursorControls.addButton(CURSOR_LEFT, cursorControlButtons.get(2));
        cursorControls.addButton(CURSOR_RIGHT, cursorControlButtons.get(3));
    }
}