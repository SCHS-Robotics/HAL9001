/*
 * Filename: GuiLine.java
 * Author: Dylan Zueck
 * Team Name: Crow Force
 * Date: 7/20/19
 */

package com.SCHSRobotics.HAL9001.system.source.GUI;

import com.SCHSRobotics.HAL9001.util.exceptions.InvalidSelectionZoneException;

/**
 * A class representing a line printed on a menu.
 */
public class GuiLine {

    //SelectionZoneText is text for the selection zone, PostSelectionText is text to go after selection zone, divider is text between the two of them.
    public String selectionZoneText, postSelectionText, divider;
    //True if custom divider is used.
    private boolean hasDivider;

    /**
     * Constructor that checks line against the given menu and uses the default divider.
     *
     * @param menu - menu to check size of selection zone text against.
     * @param selectionZoneText - Text for the selection zone.
     * @param postSelectionText - Text for the post selection zone area.
     *                          
     * @throws InvalidSelectionZoneException - Throws this exception when the text within the selection zone exceeds the length of the selection zone.
     */
    public GuiLine(Menu menu, String selectionZoneText, String postSelectionText){
        this.selectionZoneText = selectionZoneText;
        this.postSelectionText= postSelectionText;
        hasDivider = false;
        if(menu.getSelectionZoneHeight() != selectionZoneText.length()){
            throw new InvalidSelectionZoneException("The selection zone does not match the given selection zone text size.");
        }
    }

    /**
     * Constructor that creates a basic GuiLine with a default divider.
     *
     * @param selectionZoneText - Text for the selection zone.
     * @param postSelectionText - Text for the post selection zone area.
     */
    public GuiLine(String selectionZoneText, String postSelectionText){
        this.selectionZoneText = selectionZoneText;
        this.postSelectionText= postSelectionText;
        hasDivider = false;
    }

    /**
     * Constructor that sets the divider.
     *
     * @param selectionZoneText - Text for the selection zone.
     * @param postSelectionText - Text for the post selection zone area.
     * @param divider - Text that will divide the selection and post selection text.
     */
    public GuiLine(String selectionZoneText, String postSelectionText, String divider){
        this.selectionZoneText = selectionZoneText;
        this.postSelectionText= postSelectionText;
        this.divider = divider;
        hasDivider = true;
    }

    /**
     * Constructor that checks line against the given menu and sets the divider.
     *
     * @param menu - menu to check size of selection zone text against.
     * @param selectionZoneText - Text for the selection zone.
     * @param postSelectionText - Text for the post selection zone area.
     * @param divider - Text that will divide the selection and post selection text.
     *                
     * @throws InvalidSelectionZoneException - Throws this exception when the text within the selection zone exceeds the length of the selection zone.
     */
    public GuiLine(Menu menu, String selectionZoneText, String postSelectionText, String divider){
        this.selectionZoneText = selectionZoneText;
        this.postSelectionText= postSelectionText;
        this.divider = divider;
        hasDivider = true;
        if(menu.getSelectionZoneHeight() != selectionZoneText.length()){
            throw new InvalidSelectionZoneException("The selection zone does not match the given selection zone text size.");
        }
    }

    /**
     * Returns the full line text.
     */
    public String getLineText() {

        if (hasDivider) {
            return selectionZoneText + divider + postSelectionText;
        } else {
            return selectionZoneText + "| " + postSelectionText;
        }
    }

    /**
     * Returns the printable line with different selectionZoneText.
     *
     * @param selectionZoneText - Text to replace the selectionZoneText with.
     */
    public String FormatSelectionZoneText(String selectionZoneText){
        if(hasDivider){
            return selectionZoneText + divider + postSelectionText;
        }
        else {
            return selectionZoneText + "| " + postSelectionText;
        }
    }

    /**
     * Checks if selectionZoneText is within the menu's bounds.
     *
     * @param menu - Menu to check the selectionZoneText against.
     */
    public boolean checkSelectionWidthSize(Menu menu){
        return menu.getSelectionZoneWidth() == selectionZoneText.length();
    }

    /**
     * Checks if selectionZoneText is a certain length.
     *
     * @param wantedSize - Wanted length of text.
     */
    public boolean checkSelectionWidthSize(int wantedSize){
        return wantedSize == selectionZoneText.length();
    }

    @Override
    public String toString() {
        return getLineText();
    }
}
