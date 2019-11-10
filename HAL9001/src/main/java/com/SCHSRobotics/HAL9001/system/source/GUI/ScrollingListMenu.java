/*
 * Filename: ScrollingListMenu.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/20/19
 */

package com.SCHSRobotics.HAL9001.system.source.GUI;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class extending Menu.java that represents a common typ of menu.
 */
public abstract class ScrollingListMenu extends Menu {

    //The current "level" of screen in the menu. If the number of lines in the menu exceeds the maximum number, menunumber will increase by one for every screen the menu takes up.
    protected int menuNumber;

    /**
     * Constructor for ScrollingListMenu class.
     *
     * @param gui - The GUI that will be used to render the menu.
     * @param cursor - The cursor assigned to this menu.
     * @param startingLines - The list of lines that will be displayed when the menu is first rendered.
     * @param selectionZoneWidth - The maximum x value that the cursor will be able to travel to inside the selection zone.
     *                             Note: This is not the actual width of the zone itself, but a boundary for the index.
     * @param selectionZoneHeight - The maximum y value that the cursor will be able to travel to inside the selection zone.
     *                              Note: This is not the actual height of the zone itself, but a boundary for the index.
     */
    public ScrollingListMenu(GUI gui, Cursor cursor, ArrayList<GuiLine> startingLines, int selectionZoneWidth, int selectionZoneHeight){
        super(gui, cursor,  startingLines, selectionZoneWidth, selectionZoneHeight);
        cursor.setDoBlink(true);
        menuNumber = 0;
    }

    /**
     * Constructor for ScrollingListMenu class.
     *
     * @param gui - The GUI that will be used to render the menu.
     * @param cursor - The cursor assigned to this menu.
     * @param startingLines - The list of lines that will be displayed when the menu is first rendered.
     * @param selectionZoneWidth - The maximum x value that the cursor will be able to travel to inside the selection zone.
     *                             Note: This is not the actual width of the zone itself, but a boundary for the index.
     * @param selectionZoneHeight - The maximum y value that the cursor will be able to travel to inside the selection zone.
     *                              Note: This is not the actual height of the zone itself, but a boundary for the index.
     */
    public ScrollingListMenu(GUI gui, Cursor cursor, GuiLine[] startingLines, int selectionZoneWidth, int selectionZoneHeight){
        super(gui, cursor, startingLines, selectionZoneWidth, selectionZoneHeight);
        cursor.setDoBlink(true);
        menuNumber = 0;
    }

    @Override
    protected void init() { }

    @Override
    protected void open() {
        menuNumber = 0;
        cursor.y = 0;
    }

    @Override
    protected void render() {
        displayCurrentMenu();
    }

    @Override
    protected void initLoopRender() {}

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

    @Override
    public void setSelectionZoneHeight(int selectionZoneHeight, List<GuiLine> newLines) {
        super.setSelectionZoneHeight(selectionZoneHeight, newLines);
        menuNumber = (int) Math.floor((cursor.y * 1.0)/Menu.MAXLINESPERSCREEN);
    }

    @Override
    public void setSelectionZoneHeight(int selectionZoneHeight, GuiLine[] newLines) {
        super.setSelectionZoneHeight(selectionZoneHeight, newLines);
        menuNumber = (int) Math.floor((cursor.y * 1.0)/Menu.MAXLINESPERSCREEN);
    }

    @Override
    public void setSelectionZoneWidthAndHeight(int selectionZoneWidth, int selectionZoneHeight, GuiLine[] newLines) {
        super.setSelectionZoneWidthAndHeight(selectionZoneWidth, selectionZoneHeight, newLines);
        menuNumber = (int) Math.floor((cursor.y * 1.0)/Menu.MAXLINESPERSCREEN);
    }

    @Override
    public void setSelectionZoneWidthAndHeight(int selectionZoneWidth, int selectionZoneHeight, List<GuiLine> newLines) {
        super.setSelectionZoneWidthAndHeight(selectionZoneWidth, selectionZoneHeight, newLines);
        menuNumber = (int) Math.floor((cursor.y * 1.0)/Menu.MAXLINESPERSCREEN);
    }

    /**
     * Displays the current menu.
     */
    protected void displayCurrentMenu(){
        List<Integer> lineNums = new ArrayList<>();
        for (int i = menuNumber * Menu.MAXLINESPERSCREEN; i < Math.min(lines.size(),(menuNumber+1)*Menu.MAXLINESPERSCREEN); i++) {
            lineNums.add(i);
        }

        displayLines(lines,lineNums);
    }
}
