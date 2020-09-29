package com.SCHSRobotics.HAL9001.system.gui;

import com.SCHSRobotics.HAL9001.system.gui.event.BlinkEvent;
import com.SCHSRobotics.HAL9001.system.gui.event.ClickEvent;
import com.SCHSRobotics.HAL9001.system.gui.event.Event;
import com.SCHSRobotics.HAL9001.system.gui.event.GamepadEventGenerator;
import com.SCHSRobotics.HAL9001.system.gui.event.LoopEvent;
import com.SCHSRobotics.HAL9001.system.gui.event.criteria.CriteriaPacket;
import com.SCHSRobotics.HAL9001.system.gui.event.criteria.EventCriteria;
import com.SCHSRobotics.HAL9001.system.gui.event.criteria.GamepadEventCriteria;
import com.SCHSRobotics.HAL9001.system.gui.viewelement.CursorConfigurable;
import com.SCHSRobotics.HAL9001.system.gui.viewelement.UniversalUpdater;
import com.SCHSRobotics.HAL9001.system.gui.viewelement.ViewElement;
import com.SCHSRobotics.HAL9001.system.gui.viewelement.eventlistener.AdvancedListener;
import com.SCHSRobotics.HAL9001.system.gui.viewelement.eventlistener.EntireViewButton;
import com.SCHSRobotics.HAL9001.system.gui.viewelement.eventlistener.EventListener;
import com.SCHSRobotics.HAL9001.system.gui.viewelement.eventlistener.HandlesEvents;
import com.SCHSRobotics.HAL9001.util.control.Button;
import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.math.datastructures.MinHeap;
import com.SCHSRobotics.HAL9001.util.math.datastructures.MultiElementMap;
import com.SCHSRobotics.HAL9001.util.math.units.HALTimeUnit;
import com.SCHSRobotics.HAL9001.util.misc.Timer;
import com.qualcomm.robotcore.util.Range;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
    public MultiElementMap<Class<? extends Event>, EventListener> listenerElementLookup;
    private boolean doForceUpdateCursor;
    private boolean dynamicSelectionZone;
    private DynamicSelectionZone dynamicSelectionZoneAnnotation;

    private Set<Button<?>> validButtons;

    public enum BlinkState {
        ON, OFF;
        public final BlinkState nextState() {
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
        listenerElementLookup = new MultiElementMap<>();
        doForceUpdateCursor = false;

        validButtons = new HashSet<>();

        blinkTimer = new Timer();
        blinkTimer.start(cursorBlinkSpeedMs, HALTimeUnit.MILLISECONDS);

        selectionZone = initialSelectionZone();
        Class<? extends HALMenu> thisClass = getClass();
        dynamicSelectionZone = thisClass.isAnnotationPresent(DynamicSelectionZone.class);
        if(dynamicSelectionZone) {
            dynamicSelectionZoneAnnotation = thisClass.getAnnotation(DynamicSelectionZone.class);
        }

    }

    public HALMenu() {
        this(new Payload());
    }

    protected final void render() {
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

    protected final void addItem(ViewElement element) {
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
                listenerElementLookup.put(eventClass, (EventListener) element);
            }
            listenerElementLookup.put(LoopEvent.class, (EventListener) element);

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
        Event.injectEvent(new LoopEvent());

        GamepadEventGenerator eventGenerator = GamepadEventGenerator.getInstance();

        //Generate gamepad events.
        Iterator<Button<?>> validButtonIterator = this.validButtons.iterator();
        Button<?>[] validButtons = new Button[this.validButtons.size()];
        for (int i = 0; i < this.validButtons.size(); i++) {
            validButtons[i] = validButtonIterator.next();
        }
        eventGenerator.generateEvents(validButtons);

        //Generate blink event.
        Event.injectEvent(new BlinkEvent(1, cursorBlinkState.nextState()));

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
                IF THE ORDER OF THESE CHECKS IS REVERSED THERE WILL BE ERRORS
                 */
                boolean updatesUniversally = listener instanceof UniversalUpdater && ((UniversalUpdater) listener).updatesUniversally();
                if ((satisfiesCriteria && (!displayableElements.contains(listener) || displayableElements.indexOf(listener) == cursorY || updatesUniversally)) || currentEvent instanceof LoopEvent) {
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
    protected SelectionZone initialSelectionZone() {
        return new SelectionZone(0,0);
    }

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

    protected final void clearElements() {
        elements.clear();
        displayableElements.clear();
        listenerElementLookup.clear();
    }

    protected final void cursorUp() {
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

    protected final void cursorDown() {
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

    protected final void cursorLeft() {
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

    protected final void cursorRight() {
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

    protected final void setCursor(EntireViewButton cursor) {
        elements.set(0, cursor);
    }

    protected final void notifyForceCursorUpdate(boolean doForceUpdateCursor) {
        this.doForceUpdateCursor = doForceUpdateCursor;
    }

    public final SelectionZone getSelectionZone() {
        return selectionZone;
    }

    public final int getCursorX() {
        return cursorX;
    }

    public final int getCursorY() {
        return cursorY;
    }

    protected final void setCursorPos(int x, int y) {
        cursorY = Range.clip(y, 0, displayableElements.size() - 1);
        if(enforceMaxLines) {
            menuLevel = cursorY / MAX_LINES_PER_SCREEN;
        }
        cursorX = Range.clip(x, 0, displayableElements.get(y).getText().length() - 1);
    }

    protected final void setCursorX(int x) {
        setCursorPos(x, cursorY);
    }

    protected final void setCursorY(int y) {
        setCursorPos(cursorX, y);
    }

    public final char getCursorChar() {
        return cursorChar;
    }

    public final long getCursorBlinkSpeedMs() {
        return cursorBlinkSpeedMs;
    }

    private final class CursorLoc implements Comparable<CursorLoc> {

        private final int x, y;
        private CursorLoc(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public final int getX() {
            return x;
        }

        public final int getY() {
            return y;
        }

        private int taxicabDistance(CursorLoc a, CursorLoc b) {
            return abs(a.x-b.x) + abs(a.y - b.y);
        }

        @Override
        public final int compareTo(CursorLoc loc) {
            return taxicabDistance(this, new CursorLoc(cursorX, cursorY)) - taxicabDistance(loc, new CursorLoc(cursorX, cursorY));
        }

        @Override
        @NotNull
        public final String toString() {
            return "("+x+", "+y+")";
        }

        @Override
        public final boolean equals(Object obj) {
            if(!(obj instanceof CursorLoc)) {
                return false;
            }
            CursorLoc loc = (CursorLoc) obj;
            return loc.x == x && loc.y == y;
        }
    }
}
