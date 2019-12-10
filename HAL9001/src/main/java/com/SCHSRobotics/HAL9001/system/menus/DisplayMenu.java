/*
 * Filename: DisplayMenu.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/5/19
 */

package com.SCHSRobotics.HAL9001.system.menus;

import com.SCHSRobotics.HAL9001.system.source.GUI.BaseDisplayMenu;
import com.SCHSRobotics.HAL9001.system.source.GUI.GUI;
import com.SCHSRobotics.HAL9001.system.source.GUI.GuiLine;
import com.SCHSRobotics.HAL9001.system.subsystems.cursors.DefaultCursor;
import com.SCHSRobotics.HAL9001.util.misc.Button;

import java.util.List;

//TODO make synchronized/multithreaded
/**
 * A menu class meant for displaying information similarly to telemetry.
 */
public class DisplayMenu extends BaseDisplayMenu {

    /**
     * Constructor for DisplayMenu.
     *
     * @param gui The GUI used to render the menu.
     */
    public DisplayMenu(GUI gui) {
        super(gui, new DefaultCursor(gui.robot,new DefaultCursor.Params().setBlinkSpeedMs(0)), new GuiLine[]{});
    }

    @Override
    public void onSelect() {
        menuDown();
    }

    @Override
    public void onButton(String name, Button button) {}

    /**
     * Adds a line with a caption and a data value to the end of the menu's lines.
     *
     * @param caption The data's caption.
     * @param data The data to print to the screen.
     */
    public void addData(String caption, Object data){
        if(lines.size() == 1 && lines.get(0).postSelectionText.equals("")) {
            clear();
        }
        List<GuiLine> newLines = lines;
        newLines.add(new GuiLine("", caption + ": " + data.toString(), ""));
        super.setSelectionZoneHeight(super.getSelectionZoneHeight() + 1, newLines);
    }

    /**
     * Adds a line of text to the end of the menu's lines.
     *
     * @param text The text to add.
     */
    public void addLine(String text) {

        if(lines.size() == 1 && lines.get(0).postSelectionText.equals("")) {
            clear();
        }
        List<GuiLine> newLines = lines;
        newLines.add(new GuiLine("",text,""));
        super.setSelectionZoneHeight(super.getSelectionZoneHeight() + 1, newLines);
    }
}
