/*
 * Filename: ScrollingListTestMenu.java
 * Author: Dylan Zueck and Cole Savage
 * Team Name: Crow Force, Level Up
 * Date: 7/20/19
 */

package com.SCHSRobotics.HAL9001.system.menus;

import com.SCHSRobotics.HAL9001.system.source.GUI.Cursor;
import com.SCHSRobotics.HAL9001.system.source.GUI.GUI;
import com.SCHSRobotics.HAL9001.system.source.GUI.GuiLine;
import com.SCHSRobotics.HAL9001.system.source.GUI.ScrollingListMenu;
import com.SCHSRobotics.HAL9001.util.misc.Button;

import java.util.List;

/**
 * A scrolling list menu created for testing purposes.
 */
public class ScrollingListTestMenu extends ScrollingListMenu {

    //The initial lines to be displayed on the menu.
    private static final GuiLine[] STARTING_LINES = new GuiLine[] {
                new GuiLine("X", "Lines: "),
                new GuiLine("X", "Lines: "),
                new GuiLine("X", "Lines: "),
                new GuiLine("X", "Lines: "),
                new GuiLine("X", "Lines: "),
                new GuiLine("X", "Lines: ")
    };

    /**
     * Ctor for ScrollingListTestMenu.
     *
     * @param gui - The gui used to render the menu.
     */
    public ScrollingListTestMenu(GUI gui, Cursor cursor) {
        super(gui, cursor, STARTING_LINES, 1, 6);
    }
    
    @Override
    public void onSelect() {
        if(cursor.y == 0){
            addLine();
        }
        else if(cursor.y == 1 && super.getSelectionZoneHeight() > 2){
            removeLine();
        }
    }

    @Override
    public void onButton(String name, Button button) {}

    /**
     * Adds a single line to the end of the list of lines in the menu.
     */
    private void addLine(){
        List<GuiLine> newLines = lines;
        lines.add(new GuiLine("Y", "Lines: "));
        super.setSelectionZoneHeight(super.getSelectionZoneHeight() + 1, newLines);
    }

    /**
     * Removes a single line to the end of the list of lines in the menu.
     */
    private void removeLine(){
        List<GuiLine> newLines = lines;
        lines.remove(cursor.getY());
        super.setSelectionZoneHeight(super.getSelectionZoneHeight() - 1, newLines);
    }
}
