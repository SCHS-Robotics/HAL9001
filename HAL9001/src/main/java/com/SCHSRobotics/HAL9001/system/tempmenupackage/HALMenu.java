package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.qualcomm.robotcore.util.Range;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private List<ViewElement> elements, displayableElements;
    private Timer blinkTimer;
    private Map<Class<? extends Event>, List<EventListener>> listenerElementLookup;
    private boolean doForceUpdateCursor;
    private boolean dynamicSelectionZone;
    private DynamicSelectionZone dynamicSelectionZoneAnnotation;

    private Set<Button<?>> validButtons;

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
        cursorBlinkState = BlinkState.ON;
        doBlink = true;
        enforceMaxLines = true;
        elements = new ArrayList<>();
        displayableElements = new ArrayList<>();
        listenerElementLookup = new HashMap<>();
        doForceUpdateCursor = false;

        validButtons = new HashSet<>();

        blinkTimer = new Timer();
        blinkTimer.start(cursorBlinkSpeedMs, TimeUnit.MILLISECONDS);

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
            blinkTimer.reset();
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
        if(element instanceof EventListener && element.getClass().isAnnotationPresent(HandlesEvents.class)) {
            HandlesEvents eventAnnotation = element.getClass().getAnnotation(HandlesEvents.class);
            ExceptionChecker.assertNonNull(eventAnnotation, new NullPointerException("Event annotation returned null. This should not be possible."));
            Class<? extends Event>[] eventClasses = eventAnnotation.events();
            for(Class<? extends Event> eventClass : eventClasses) {
                if(listenerElementLookup.containsKey(eventClass)) {
                    List<EventListener> listeners = listenerElementLookup.get(eventClass);
                    ExceptionChecker.assertNonNull(listeners, new NullPointerException("Event key mapped to null value. This should not be possible."));
                    listeners.add((EventListener) element);
                }
                else {
                    List<EventListener> listeners = new ArrayList<>();
                    listeners.add((EventListener) element);
                    listenerElementLookup.put(eventClass, listeners);
                }
            }
            if(element instanceof AdvancedListener) {
                AdvancedListener advancedListener = (AdvancedListener) element;
                CriteriaPacket eventCriteria = advancedListener.getCriteria();
                for(EventCriteria<?> criteria : eventCriteria) {
                    if(criteria instanceof GamepadEventCriteria) {
                        GamepadEventCriteria<ClickEvent<Button<?>>, Button<?>> gamepadCriteria = (GamepadEventCriteria<ClickEvent<Button<?>>, Button<?>>) criteria;
                        Set<Button<?>> buttons = gamepadCriteria.getValidButtons();
                        validButtons.addAll(buttons);
                    }
                }
            }
        }
        if(dynamicSelectionZone && displayableElements.size() > selectionZone.getHeight()) {
            selectionZone.addRow(dynamicSelectionZoneAnnotation.pattern());
        }
    }

    protected final boolean updateListeners() {
        GamepadEventGenerator eventGenerator = GamepadEventGenerator.getInstance();

        //Generate gamepad events.
        Iterator<Button<?>> validButtonIterator = this.validButtons.iterator();
        Button[] validButtons = new Button[this.validButtons.size()];
        for (int i = 0; i < this.validButtons.size(); i++) {
            validButtons[i] = validButtonIterator.next();
        }
        eventGenerator.generateEvents(validButtons);

        //Generate blink event.
        if(blinkTimer.requiredTimeElapsed() || doForceUpdateCursor) {
            Event.injectEvent(new BlinkEvent(-1, cursorBlinkState.nextState()));
        }

        //Pass events to event listeners.
        boolean anythingUpdatesCursor = false;
        boolean anythingRequestsNoBlink = false;

        Event currentEvent = Event.getNextEvent();
        while (currentEvent != null) {

            List<EventListener> registeredListeners = listenerElementLookup.get(currentEvent.getClass());
            registeredListeners = registeredListeners == null ? new ArrayList<>() : registeredListeners;

            for (EventListener listener : registeredListeners) {
                boolean doCursorUpdate = false;


                boolean satisfiesCriteria = false;
                if(listener instanceof AdvancedListener) {
                    AdvancedListener advancedListener = (AdvancedListener) listener;
                    CriteriaPacket eventCriteria = advancedListener.getCriteria();
                    for(EventCriteria<?> criteria : eventCriteria) {
                        satisfiesCriteria |= criteria.satisfiesCriteria(currentEvent);
                    }
                }
                else {
                    satisfiesCriteria = true;
                }

                /*
                Short circuit evaluation, DO NOT CHANGE THE ORDER OF THINGS IN THE IF STATEMENT
                if element is not displayable (i.e. entire-screen-related), skip second check
                if element is displayable, check if element is on the same line as the cursor
                IF THE ORDER OF THESE CHECKS IS REVERSED THERE WILL BE ERRORS, AS FULL-VIEW BUTTONS ARE NOT DISPLAYABLE
                 */
                if(satisfiesCriteria && (!displayableElements.contains(listener) || displayableElements.indexOf(listener) == cursorY)) {
                    doCursorUpdate = listener.onEvent(currentEvent);
                }

                //Handles no blink requested checking.
                if (listener instanceof CursorConfigurable) {
                    anythingRequestsNoBlink |= ((CursorConfigurable) listener).requestNoBlinkOnTriggeredUpdate() && doCursorUpdate;
                }

                anythingUpdatesCursor |= doCursorUpdate;
            }

            currentEvent = Event.getNextEvent();
        }

        doBlink = !anythingRequestsNoBlink && !selectionZone.isZero();
        return anythingUpdatesCursor && !selectionZone.isZero();
    }

    protected abstract void init(Payload payload);

    protected final void displayLines(int startAt, int endAt){
        ExceptionChecker.assertTrue(startAt >= 0, new DumpsterFireException("startAt must be greater than 0"));
        ExceptionChecker.assertTrue(startAt < endAt, new DumpsterFireException("startAt must be less than endAt"));
        ExceptionChecker.assertTrue(endAt <= displayableElements.size(), new DumpsterFireException("endAt must be less than or equal to the number of displayable view elements"));
        for(int i = startAt; i < endAt; i++) {
            ViewElement displayableElement = displayableElements.get(i);
            String line = displayableElement.getText();
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

        if(blinkTimer.requiredTimeElapsed()) {
            cursorBlinkState = cursorBlinkState.nextState();
            blinkTimer.reset();
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
