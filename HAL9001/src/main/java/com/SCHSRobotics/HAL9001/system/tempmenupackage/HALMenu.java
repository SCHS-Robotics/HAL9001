package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.misc.Toggle;
import com.qualcomm.robotcore.util.Range;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.min;

//4/29/20
public abstract class HALMenu {
    //The maximum number of lines that can fit on the FTC driver station. This is a global constant.
    public static final int MAX_LINES_PER_SCREEN = 8;

    protected HALGUI gui;
    protected SelectionZone selectionZone;
    protected Payload payload;

    protected char cursorChar;
    protected long cursorBlinkSpeedMs;
    protected boolean doBlink;
    protected boolean enforceMaxLines;

    private static final String CURSOR_UP = "CursorUp", CURSOR_DOWN = "CursorDown", CURSOR_LEFT = "CursorLeft", CURSOR_RIGHT = "CursorRight";
    //The current "level" of screen in the menu. If the number of lines in the menu exceeds the maximum number, menuLevel will increase by one for every screen the menu takes up.
    private int menuLevel;
    private int cursorX, cursorY;
    private int minLineLength;
    private long lastBlinkTimeMs;
    private List<ViewElement> elements, displayableElements;
    private List<Button<Boolean>> cursorControlButtons;
    private CustomizableGamepad cursorControls;
    private Toggle cursorUpToggle, cursorDownToggle, cursorLeftToggle, cursorRightToggle;

    private enum BlinkState {
        ON, OFF;
        public BlinkState nextState() {
            if(this == ON) {
                return OFF;
            }
            else {
                return ON;
            }
        }
    }
    private BlinkState cursorBlinkState;

    public HALMenu(Payload payload) {
        this.payload = payload;

        gui = HALGUI.getInstance();
        selectionZone = new SelectionZone(0,0);
        cursorX = 0;
        cursorY = 0;
        menuLevel = 0;
        minLineLength = Integer.MAX_VALUE;
        cursorChar = '█';
        cursorBlinkSpeedMs = 250;
        lastBlinkTimeMs = System.currentTimeMillis();
        cursorBlinkState = BlinkState.ON;
        doBlink = true;
        enforceMaxLines = true;
        elements = new ArrayList<>();
        displayableElements = new ArrayList<>();
        cursorUpToggle = new Toggle(Toggle.ToggleTypes.trueOnceToggle, false);
        cursorDownToggle = new Toggle(Toggle.ToggleTypes.trueOnceToggle, false);
        cursorLeftToggle = new Toggle(Toggle.ToggleTypes.trueOnceToggle, false);
        cursorRightToggle = new Toggle(Toggle.ToggleTypes.trueOnceToggle, false);
    }

    public HALMenu() {
        this(new Payload());
    }

    protected void setupCursor() {
        cursorControls = new CustomizableGamepad(gui.getRobot());
        cursorControlButtons = gui.getCursorControls();
        cursorControls.addButton(CURSOR_UP, cursorControlButtons.get(0));
        cursorControls.addButton(CURSOR_DOWN, cursorControlButtons.get(1));
        cursorControls.addButton(CURSOR_LEFT, cursorControlButtons.get(2));
        cursorControls.addButton(CURSOR_RIGHT, cursorControlButtons.get(3));
    }

    protected void render() {
        cursorUpToggle.updateToggle(cursorControls.getInput(CURSOR_UP));
        cursorDownToggle.updateToggle(cursorControls.getInput(CURSOR_DOWN));
        cursorLeftToggle.updateToggle(cursorControls.getInput(CURSOR_LEFT));
        cursorRightToggle.updateToggle(cursorControls.getInput(CURSOR_RIGHT));

        if(cursorUpToggle.getCurrentState()) {
            cursorUp();
        }
        else if(cursorDownToggle.getCurrentState()) {
            cursorDown();
        }
        else if(cursorLeftToggle.getCurrentState()) {
            cursorLeft();
        }
        else if(cursorRightToggle.getCurrentState()) {
            cursorRight();
        }

        boolean cursorUpdated = updateListeners();
        if(cursorUpdated) {
            cursorBlinkState = BlinkState.ON;
            lastBlinkTimeMs = System.currentTimeMillis();
        }

        if(enforceMaxLines) {
            displayLines(menuLevel*MAX_LINES_PER_SCREEN, min(elements.size(),(menuLevel+1)*MAX_LINES_PER_SCREEN));
        }
        else {
            displayLines(0, elements.size());
        }
    }

    protected void addItem(ViewElement element) {
        elements.add(element);
        String text = element.getText();
        if(text != null) {
            displayableElements.add(element);
            minLineLength = min(minLineLength, text.length());
        }
    }

    protected final boolean updateListeners() {
        boolean anythingUpdatesCursor = false;
        for(ViewElement element : elements) {
            if(element instanceof ViewListener) {
                boolean forceCursorUpdate = false;
                /*
                Short circuit evaluation, DO NOT CHANGE THE ORDER OF THINGS IN THE IF STATEMENT
                if element is not displayable (i.e. entire-screen-related), skip second check
                if element is displayable, check if element is on the same line as the cursor
                IF THE ORDER OF THESE CHECKS IS REVERSED THERE WILL BE ERRORS, AS FULL VIEW BUTTONS ARE NOT DISPLAYABLE
                 */
                if(!displayableElements.contains(element) || displayableElements.indexOf(element) == cursorY) {
                    forceCursorUpdate = ((ViewListener) element).update();
                }
                if(element instanceof BlinkingConfigurator) {
                    forceCursorUpdate &= !((BlinkingConfigurator) element).requestNoBlinkOnTriggeredUpdate();
                }
                anythingUpdatesCursor |= forceCursorUpdate;
            }
        }

        return anythingUpdatesCursor && !selectionZone.isZero();
    }

    protected abstract void init(Payload payload);

    protected final void displayLines(int startAt, int endAt){
        ExceptionChecker.assertTrue(startAt >= 0, new DumpsterFireException("startAt must be greater than 0"));
        ExceptionChecker.assertTrue(startAt < endAt, new DumpsterFireException("startAt must be less than endAt"));
        ExceptionChecker.assertTrue(endAt <= displayableElements.size(), new DumpsterFireException("endAt must be less than or equal to the number of displayable view elements"));
        for(int i = startAt; i < endAt; i++) {
            String line = displayableElements.get(i).getText();
            String toDisplay = line;
            if (cursorY == i) {
                toDisplay = blinkCursor(line);
            }
            gui.getRobot().telemetry.addLine(toDisplay);
        }
    }

    /**
     * Causes the cursor to blink on a specified line.
     *
     * @param line - The line object where the cursor is currently located.
     */
    private String blinkCursor(@NotNull String line) {
        char[] chars = line.toCharArray();

        if(System.currentTimeMillis() - lastBlinkTimeMs >= cursorBlinkSpeedMs) {
            cursorBlinkState = cursorBlinkState.nextState();
            lastBlinkTimeMs = System.currentTimeMillis();
        }

        //if the cursor isn't supposed to blink, turn it off.
        if(!doBlink) {
            cursorBlinkState = BlinkState.OFF;
        }

        if(chars.length != 0) {
            char drawChar = cursorBlinkState == BlinkState.ON ? cursorChar : chars[cursorX];
            chars[cursorX] = drawChar;
        }

        return new String(chars);
    }

    protected final void setCursorControls(Button<Boolean> upButton, Button<Boolean> downButton, Button<Boolean> leftButton, Button<Boolean> rightButton) {
        ExceptionChecker.assertFalse(upButton.equals(downButton) ||
                                               upButton.equals(leftButton) ||
                                               upButton.equals(rightButton) ||
                                               downButton.equals(leftButton) ||
                                               downButton.equals(rightButton) ||
                                               leftButton.equals(rightButton), new DumpsterFireException("All cursor controls must be unique"));

        ArrayList<Button<Boolean>> cursorControlButtons = gui.getCursorControls();

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

    protected void cursorUp() {
        if((cursorY-1) % MAX_LINES_PER_SCREEN == 0 && enforceMaxLines && menuLevel > 0) {
            menuLevel--;
        }
        if(cursorY > 0) {
            cursorY--;
        }

        int maxCursorX = displayableElements.get(cursorY).getText().length() - 1;
        if(cursorX > maxCursorX) {
            cursorX = maxCursorX;
        }
    }

    protected void cursorDown() {
        if(cursorY % MAX_LINES_PER_SCREEN == 0 && enforceMaxLines && menuLevel < ceil(((double) cursorY)/MAX_LINES_PER_SCREEN)) {
            menuLevel++;
        }
        if(cursorY < min(displayableElements.size(), selectionZone.getHeight() + 1)) {
            cursorY++;
        }

        int maxCursorX = displayableElements.get(cursorY).getText().length() - 1;
        if(cursorX > maxCursorX) {
            cursorX = maxCursorX;
        }
    }

    protected void cursorLeft() {
        if(cursorX > 0) {
            cursorX--;
        }
    }

    protected void cursorRight() {
        if(cursorX < selectionZone.getWidth() && cursorX < displayableElements.get(cursorY).getText().length()) {
            cursorX++;
        }
    }

    public SelectionZone getSelectionZone() {
        return selectionZone;
    }

    public void setSelectionZone(int width, int height) {
        selectionZone.setWidth(width);
        selectionZone.setHeight(height);
    }

    public void setSelectionZoneWidth(int width) {
        setSelectionZone(width, selectionZone.getHeight());
    }

    public void setSelectionZoneHeight(int height) {
        setSelectionZone(selectionZone.getWidth(), height);
    }

    public int getCursorX() {
        return cursorX;
    }

    public int getCursorY() {
        return cursorY;
    }

    protected void setCursorPos(int x, int y) {
        cursorY = Range.clip(y, 0, displayableElements.size() - 1);
        if(enforceMaxLines) {
            menuLevel = (int) floor(((double) cursorY) / MAX_LINES_PER_SCREEN);
        }
        cursorX = Range.clip(x, 0, displayableElements.get(y).getText().length() - 1);
    }

    protected void setCursorX(int x) {
        setCursorPos(x, cursorY);
    }

    protected void setCursorY(int y) {
        setCursorPos(cursorX, y);
    }

    public char getCursorChar() {
        return cursorChar;
    }

    public long getCursorBlinkSpeedMs() {
        return cursorBlinkSpeedMs;
    }
}
