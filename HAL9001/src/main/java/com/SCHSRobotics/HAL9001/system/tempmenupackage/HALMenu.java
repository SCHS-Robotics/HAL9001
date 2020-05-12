package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.qualcomm.robotcore.util.Range;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
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

    //The current "level" of screen in the menu. If the number of lines in the menu exceeds the maximum number, menuLevel will increase by one for every screen the menu takes up.
    private int menuLevel;
    private int cursorX, cursorY;
    private long lastBlinkTimeMs;
    private List<ViewElement> elements, displayableElements;
    private boolean doForceUpdateCursor;
    private boolean dynamicSelectionZone;
    private DynamicSelectionZone dynamicSelectionZoneAnnotation;

    public enum BlinkState {
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
        cursorChar = '█';
        cursorBlinkSpeedMs = 500;
        lastBlinkTimeMs = System.currentTimeMillis();
        cursorBlinkState = BlinkState.ON;
        doBlink = true;
        enforceMaxLines = true;
        elements = new ArrayList<>();
        displayableElements = new ArrayList<>();
        doForceUpdateCursor = false;

        Class<? extends HALMenu> thisClass = getClass();
        dynamicSelectionZone = thisClass.isAnnotationPresent(DynamicSelectionZone.class);
        if(dynamicSelectionZone) {
            dynamicSelectionZoneAnnotation = thisClass.getAnnotation(DynamicSelectionZone.class);
        }
    }

    public HALMenu() {
        this(new Payload());
    }

    protected void render() {
        if(doForceUpdateCursor) {
            cursorBlinkState = BlinkState.ON;
            lastBlinkTimeMs = System.currentTimeMillis();
        }

        if(enforceMaxLines) {
            displayLines(menuLevel*MAX_LINES_PER_SCREEN, min(displayableElements.size(), (menuLevel+1)*MAX_LINES_PER_SCREEN));
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
        }
        if(dynamicSelectionZone && displayableElements.size() > selectionZone.getHeight()) {
            selectionZone.addRow(dynamicSelectionZoneAnnotation.pattern());
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
                IF THE ORDER OF THESE CHECKS IS REVERSED THERE WILL BE ERRORS, AS FULL-VIEW BUTTONS ARE NOT DISPLAYABLE
                 */
                if(!displayableElements.contains(element) || displayableElements.indexOf(element) == cursorY) {
                    if(element instanceof Blinkable) {
                        ((Blinkable) element).notifyCurrentBlinkState(cursorBlinkState);
                    }
                    forceCursorUpdate = ((ViewListener) element).update();
                }
                if(element instanceof CursorConfigurable) {
                    forceCursorUpdate &= !((CursorConfigurable) element).requestNoBlinkOnTriggeredUpdate();
                }

                anythingUpdatesCursor |= forceCursorUpdate;
            }
        }

        return anythingUpdatesCursor && !selectionZone.isZero();
    }

    protected final void disableListeners() {
        for(ViewElement element : elements) {
            if(element instanceof ButtonListener) {
                ((ButtonListener) element).disable(HALGUI.POST_LOAD_LISTENER_DISABLE_DURATION_MS);
            }
        }
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

    protected void clear() {
        elements.clear();
        displayableElements.clear();
    }

    protected void cursorUp() {
        if(cursorY > 0) {
            MinHeap<CursorLoc> distanceHeap = new MinHeap<>();
            for (int virtualCursorY = cursorY - 1; virtualCursorY >= 0; virtualCursorY--) {
                boolean validSpaceFound = false;
                for (int virtualCursorX = 0; virtualCursorX < min(selectionZone.getWidth(), displayableElements.get(virtualCursorY).getText().length()); virtualCursorX++) {
                    validSpaceFound |= selectionZone.isValidLocation(virtualCursorX, virtualCursorY);
                    if (selectionZone.isValidLocation(virtualCursorX, virtualCursorY)) {
                        distanceHeap.add(new CursorLoc(virtualCursorX, virtualCursorY));
                    }
                }
                if (validSpaceFound) {
                    break;
                }
            }
            CursorLoc newPoint = distanceHeap.poll();
            if(newPoint != null) {
                cursorX = newPoint.getX();
                cursorY = newPoint.getY();
            }
        }
        if(enforceMaxLines) {
            //Floor Division
            menuLevel = cursorY / MAX_LINES_PER_SCREEN;
        }
    }

    protected void cursorDown() {
        if(cursorY < min(displayableElements.size(), selectionZone.getHeight()) - 1) {
            MinHeap<CursorLoc> distanceHeap = new MinHeap<>();
            for (int virtualCursorY = cursorY + 1; virtualCursorY < min(selectionZone.getHeight(), displayableElements.size()); virtualCursorY++) {
                boolean validSpaceFound = false;
                for (int virtualCursorX = 0; virtualCursorX < min(selectionZone.getWidth(), displayableElements.get(virtualCursorY).getText().length()); virtualCursorX++) {
                    validSpaceFound |= selectionZone.isValidLocation(virtualCursorX, virtualCursorY);
                    if (selectionZone.isValidLocation(virtualCursorX, virtualCursorY)) {
                        distanceHeap.add(new CursorLoc(virtualCursorX, virtualCursorY));
                    }
                }
                if (validSpaceFound) {
                    break;
                }
            }
            CursorLoc newLoc = distanceHeap.poll();
            if(newLoc != null) {
                cursorX = newLoc.getX();
                cursorY = newLoc.getY();
            }
        }

        if(enforceMaxLines) {
            //Floor Division
            menuLevel = cursorY / MAX_LINES_PER_SCREEN;
        }
    }

    protected void cursorLeft() {
        if(cursorX > 0) {
            int virtualCursorX = cursorX - 1;
            while (!selectionZone.isValidLocation(virtualCursorX, cursorY)) {
                virtualCursorX--;
                if(virtualCursorX == -1) {
                    return;
                }
            }
            cursorX = virtualCursorX;
        }
    }

    protected void cursorRight() {
        int lineLength = displayableElements.get(cursorY).getText().length();
        if(cursorX < min(selectionZone.getWidth(), lineLength) - 1) {
            int virtualCursorX = cursorX + 1;
            while (!selectionZone.isValidLocation(virtualCursorX, cursorY)) {
                virtualCursorX++;
                if(virtualCursorX == min(selectionZone.getWidth(), lineLength)) {
                    return;
                }
            }
            cursorX = virtualCursorX;
        }
    }

    protected void setCursor(EntireViewButton cursor) {
        elements.set(0, cursor);
    }

    protected void notifyForceCursorUpdate(boolean doForceUpdateCursor) {
        this.doForceUpdateCursor = doForceUpdateCursor;
    }

    public SelectionZone getSelectionZone() {
        return selectionZone;
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
            menuLevel = cursorY / MAX_LINES_PER_SCREEN;
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

    private class CursorLoc implements Comparable<CursorLoc> {

        private int x, y;
        private CursorLoc(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        private int taxicabDistance(CursorLoc a, CursorLoc b) {
            return abs(a.x-b.x) + abs(a.y - b.y);
        }

        @Override
        public int compareTo(CursorLoc loc) {
            return taxicabDistance(this, new CursorLoc(cursorX, cursorY)) - taxicabDistance(loc, new CursorLoc(cursorX, cursorY));
        }

        @Override
        @NotNull
        public String toString() {
            return "("+x+", "+y+")";
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof CursorLoc)) {
                return false;
            }
            CursorLoc loc = (CursorLoc) obj;
            return loc.x == x && loc.y == y;
        }
    }
}
