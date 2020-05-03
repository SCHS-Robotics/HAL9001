package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.qualcomm.robotcore.util.Range;

public class SelectionZone {
    private int width, height;
    public SelectionZone(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    //todo dylan mode
    public void setHeight(int height) {
        this.height = Range.clip(height, 0, HALMenu.MAX_LINES_PER_SCREEN);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isZero() {
        return width == 0 && height == 0;
    }
}
