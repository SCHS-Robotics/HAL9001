/*
 * Filename: BaseDisplayMenu.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/5/19
 */

package com.SCHSRobotics.HAL9001.system.source.GUI;

import java.util.ArrayList;
import java.util.List;

/**
 * The base menu class for DisplayMenus to extend.
 */
public abstract class BaseDisplayMenu extends Menu {
    //The current "level" of screen in the menu. If the number of lines in the menu exceeds the maximum number, menunumber will increase by one for every screen the menu takes up.
    private int menuNumber;

    /**
     * Constructor for BaseDisplayMenu.
     *
     * @param gui The GUI being used to render the menu.
     * @param cursor The cursor being used in the menu.
     * @param startingLines The menu's initial set of GuiLines.
     */
    public BaseDisplayMenu(GUI gui, Cursor cursor, GuiLine[] startingLines) {
        super(gui, cursor, startingLines,0,0);
        menuNumber = 0;
    }

    /**
     * Constructor for BaseDisplayMenu.
     *
     * @param gui The GUI being used to render the menu.
     * @param cursor The cursor being used in the menu.
     * @param startingLines The menu's initial set of GuiLines.
     */
    public BaseDisplayMenu(GUI gui, Cursor cursor, ArrayList<GuiLine> startingLines) {
        super(gui,cursor, startingLines,0,0);
        menuNumber = 0;
    }

    @Override
    protected void init() {
        super.cursor.doBlink = false;
    }

    @Override
    protected void open() {
        super.setSelectionZoneHeight(0,new GuiLine[]{});
        super.cursor.doBlink = false;
    }

    @Override
    protected void render() {
        displayCurrentMenu();
        clear();
    }

    @Override
    protected void initLoopRender() {
        displayCurrentMenu();
        clear();
    }

    @Override
    protected void onStart() {}

    @Override
    protected void stop() {}

    @Override
    public void menuUp(){

        menuNumber--;

        if(menuNumber < 0) {
            menuNumber = (int) Math.floor((lines.size() * 1.0) / Menu.MAXLINESPERSCREEN);
            cursor.y = Math.min(lines.size() - 1,(menuNumber*Menu.MAXLINESPERSCREEN)-1);
        }
    }

    @Override
    public void menuDown(){

        menuNumber++;

        if(menuNumber >= (int) Math.ceil((lines.size() * 1.0) / Menu.MAXLINESPERSCREEN)) {
            menuNumber = 0;
            cursor.y = 0;
        }
    }

    /**
     * Displays the current menu.
     */
    private void displayCurrentMenu(){

        List<Integer> lineNums = new ArrayList<>();
        for (int i = menuNumber * Menu.MAXLINESPERSCREEN; i < Math.min(lines.size(),(menuNumber+1)*Menu.MAXLINESPERSCREEN); i++) {
            lineNums.add(i);
        }

        displayLines(lines,lineNums);
    }

    /**
     * Clears the screen.
     */
    public void clear() {
        super.setSelectionZoneHeight(0,new GuiLine[]{});
    }
}