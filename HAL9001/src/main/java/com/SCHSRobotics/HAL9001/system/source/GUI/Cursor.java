/*
 * Filename: Cursor.java
 * Author: Dylan Zueck and Cole Savage
 * Team Name: Crow Force, Level Up
 * Date: 7/20/19
 */

package com.SCHSRobotics.HAL9001.system.source.GUI;

/**
 * An abstract class meant to represent the user's cursor.
 */
public abstract class Cursor {

    //The cursor's x and y coordinate and blink speed.
    public int x, y, blinkSpeedMs;
    //The menu the cursor belongs to.
    protected Menu menu;
    //The cursor's display icon.
    private char cursorIcon;
    //A boolean specifying if the cursor has been updated.
    protected boolean cursorUpdated;
    //A boolean specifying whether the cursor should allow blinking.
    protected boolean doBlink;
    //A boolean specifying if the cursor should force its display icon to appear when updated. This is usually set internally by menus.
    protected boolean forceCursorChar;

    /**
     * Constructor for cursor class.
     *
     * @param x            The cursor's initial x coordinate.
     * @param y            The cursor's initial y coordinate.
     * @param blinkSpeedMs The cursor blink speed in milliseconds.
     * @param cursorIcon   The char that will represent the cursor when the menu is drawn.
     */
    public Cursor(int x, int y, int blinkSpeedMs, char cursorIcon) {
        this.blinkSpeedMs = blinkSpeedMs;
        this.x = x;
        this.y = y;
        this.cursorIcon = cursorIcon;
        forceCursorChar = true;
    }

    /**
     * Constructor for cursor class.
     *
     * @param blinkSpeedMs The cursor blink speed in milliseconds.
     * @param cursorIcon   The char that will represent the cursor when the menu is drawn.
     */
    public Cursor(int blinkSpeedMs, char cursorIcon) {
        this(0,0,blinkSpeedMs,cursorIcon);
    }

    /**
     * Constructor for cursor class.
     *
     * @param blinkSpeedMs The cursor blink speed in milliseconds.
     */
    public Cursor(int blinkSpeedMs) {
        this(0,0,blinkSpeedMs,'█');
    }

    /**
     * Constructor for cursor class.
     *
     * @param x            Cursor starting x position.
     * @param y            Cursor starting y position.
     * @param blinkSpeedMs Cursor blink speed in milliseconds.
     */
    public Cursor(int x, int y, int blinkSpeedMs) {
        this(x,y,blinkSpeedMs,'█');
    }

    /**
     * Returns the cursor icon.
     *
     * @return The cursor icon.
     */
    public char getCursorIcon() {
        return cursorIcon;
    }

    /**
     * Sets the cursor icon.
     *
     * @param cursorIcon The new cursor icon.
     */
    public void setCursorIcon(char cursorIcon) {
        this.cursorIcon = cursorIcon;
    }

    /**
     * Sets the menu that the cursor belongs to.
     *
     * @param menu The menu to be assigned to the cursor.
     */
    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    /**
     * Gets the cursor x coordinate.
     *
     * @return The cursor x coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the cursor y coordinate.
     *
     * @return The cursor y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the cursor blink speed.
     *
     * @return The cursor blink speed.
     */
    public int getBlinkSpeedMs() {
        return blinkSpeedMs;
    }

    /**
     * Sets the cursor's x coordinate.
     *
     * @param x The cursor's desired x coordinate.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the cursor's y coordinate.
     *
     * @param y The cursor's desired y coordinate.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Abstract method that runs when the cursor is updated every frame. Methods for controlling the cursor go here.
     */
    public abstract void update();

    /**
     * Sets if the cursor should blink or not.
     *
     * @param doBlink A boolean specifying whether the cursor should be set to blink (true) or not (false).
     */
    public void setDoBlink(boolean doBlink) {
        this.doBlink = doBlink;
    }
}
