package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import java.util.ArrayList;
import java.util.List;

public class TelemetryMenu extends HALMenu {
    private List<TextElement> elementsToDisplay;
    private char divider;

    public TelemetryMenu() {
        super();
        selectionZone = new SelectionZone(0,0);
        elementsToDisplay = new ArrayList<>();
        divider = ':';
    }

    @Override
    protected void init(Payload payload) {
        for(TextElement element : elementsToDisplay) {
            addItem(element);
        }
    }

    public TelemetryMenu addLine(String line) {
        elementsToDisplay.add(new TextElement(line));
        return this;
    }

    public TelemetryMenu addLine(Object line) {
        return addLine(line.toString());
    }

    public TelemetryMenu addData(String caption, String data) {
        elementsToDisplay.add(new TextElement(caption + divider + ' ' + data));
        return this;
    }

    public TelemetryMenu addData(String caption, Object data) {
        return addData(caption, data.toString());
    }

    public void update() {
        clear();
        init(payload);
    }

    public void clear() {
        elementsToDisplay.clear();
        clearElements();
    }
}
