package com.SCHSRobotics.HAL9001.system.source.GUI;

import android.util.Log;

import com.SCHSRobotics.HAL9001.util.exceptions.InvalidSelectionZoneException;
import com.SCHSRobotics.HAL9001.util.exceptions.SkyscraperTooTallException;
import com.SCHSRobotics.HAL9001.util.exceptions.WrongSkyscraperBlueprintException;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.qualcomm.robotcore.util.Range;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class representing a menu that can be displayed on the driver station.
 *
 * @author Cole Savage, Level Up
 * @author Dylan Zueck, Crow Force
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/20/19
 */
public abstract class Menu {

    //The GUI being used to render the menu.
    public GUI gui;
    //The selection zone width and height.
    private int selectionZoneWidth, selectionZoneHeight;
    //The cursor being used in the menu.
    public Cursor cursor;
    //The list of all lines in the menu.
    protected List<GuiLine> lines;
    //The maximum number of lines that can fit on the FTC driver station. This is a global constant.
    public static final int MAXLINESPERSCREEN = 8;

    /**
     * Constructor for menu class.
     *
     * @param gui The GUI that will be used to render the menu.
     * @param cursor The cursor assigned to the menu.
     * @param startingLines The list of lines that will be displayed when the menu is first rendered.
     * @param selectionZoneWidth The maximum x value that the cursor will be able to travel to inside the selection zone.
     *                           Note: This is not the actual width of the zone itself, but a boundary for the index.
     * @param selectionZoneHeight The maximum y value that the cursor will be able to travel to inside the selection zone.
     *                            Note: This is not the actual height of the zone itself, but a boundary for the index.
     *
     * @throws InvalidSelectionZoneException - Throws this exception if the provided selection zone is impossible.
     */
    public Menu(GUI gui, Cursor cursor, GuiLine[] startingLines, int selectionZoneWidth, int selectionZoneHeight) {
        this.gui = gui;
        this.cursor = cursor;
        this.cursor.setMenu(this);
        if(selectionZoneWidth < 0 || selectionZoneHeight < 0) {
            throw new InvalidSelectionZoneException("Error: Invalid selection zone");
        }
        this.selectionZoneWidth = selectionZoneWidth;
        this.selectionZoneHeight = selectionZoneHeight;

        setLines(startingLines);
    }

    /**
     * Constructor for menu class.
     *
     * @param gui The GUI that will be used to render the menu.
     * @param cursor The cursor assigned to the menu.
     * @param startingLines The list of lines that will be displayed when the menu is first rendered.
     * @param selectionZoneWidth The maximum x value that the cursor will be able to travel to inside the selection zone.
     *                           Note: This is not the actual width of the zone itself, but a boundary for the index.
     * @param selectionZoneHeight The maximum y value that the cursor will be able to travel to inside the selection zone.
     *                            Note: This is not the actual height of the zone itself, but a boundary for the index.
     *
     * @throws InvalidSelectionZoneException Throws this exception if the provided selection zone is impossible.
     */
    public Menu(GUI gui, Cursor cursor, ArrayList<GuiLine> startingLines, int selectionZoneWidth, int selectionZoneHeight) {
        this.gui = gui;
        this.cursor = cursor;
        this.cursor.setMenu(this);
        if(selectionZoneWidth < 0 || selectionZoneHeight < 0) {
            throw new InvalidSelectionZoneException("Error: Invalid selection zone");
        }
        this.selectionZoneWidth = selectionZoneWidth;
        this.selectionZoneHeight = selectionZoneHeight;

        setLines(startingLines);
    }

    /**
     * Abstract method that is called whenever a menu is initialized.
     */
    protected abstract void init();

    /**
     * Abstract method that is called whenever the menu is opened.
     */
    protected abstract void open();

    /**
     * Abstract method that is called whenever the cursor select button is pressed.
     */
    public abstract void onSelect();

    /**
     * Abstract method that is called whenever the a button is pressed.
     *
     * @param button - The button currently being pressed;
     */
    public abstract void onButton(String name, Button button);

    /**
     * Abstract method that is called every frame to render the menu.
     */
    protected abstract void render();

    /**
     * Abstract method that is called every frame on init_loop to render the menu.
     */
    protected abstract void initLoopRender();

    /**
     * Abstract method that is called on start.
     */
    protected abstract void onStart();

    /**
     * Abstract method that is called when the gui is stopped.
     */
    protected abstract void stop();

    /**
     * Displays a line to the screen. Calls an identically-named method in GUI.
     *
     * @param line The line to display.
     * @param lineNumber The index of the line on the screen.
     */
    protected void displayLine(GuiLine line, int lineNumber){
        gui.displayLine(line, lineNumber);
    }

    /**
     * Displays multiple lines to the screen.
     *
     * @param lines The lines to display.
     *
     * @throws SkyscraperTooTallException Throws this exception when the number of lines to draw is greater than the number that can fit on the screen.
     */
    protected void displayLines(GuiLine[] lines){
        if(lines.length > MAXLINESPERSCREEN) {
            throw new SkyscraperTooTallException("Lines passed to displayLines were more than "+MAXLINESPERSCREEN+" lines");
        }
            if (lines.length != 0) {
                for (int i = 0; i < lines.length; i++) {
                    displayLine(lines[i], i);
                }
            } else {
                displayNothing();
            }

    }

    /**
     * Displays multiple lines to the screen.
     *
     * @param lines The lines to display.
     * @param lineNumbers The indexes of the lines to display.
     *
     * @throws SkyscraperTooTallException Throws this exception when the number of lines to draw is greater than the number that can fit on the screen.
     */
    protected void displayLines(GuiLine[] lines, List<Integer> lineNumbers){
        if(lineNumbers.size() > MAXLINESPERSCREEN) {
            throw new SkyscraperTooTallException("More than "+MAXLINESPERSCREEN+" lines were selected for display");
        }
        if(lines.length != 0 && lineNumbers.size() != 0) {
            for (int i = 0; i < lineNumbers.size(); i++) {
                displayLine(lines[lineNumbers.get(i)], lineNumbers.get(i));
            }
        }
        else {
            displayNothing();
        }
    }

    /**
     * Displays multiple lines to the screen.
     *
     * @param lines The lines to display.
     *
     * @throws SkyscraperTooTallException Throws this exception when the number of lines to draw is greater than the number that can fit on the screen.
     */
    protected void displayLines(List<GuiLine> lines){
        if(lines.size() > MAXLINESPERSCREEN) {
            throw new SkyscraperTooTallException("Lines passed to displayLines were more than 8 lines");
        }
        if(lines.size() != 0) {
            for (int i = 0; i < lines.size(); i++) {
                displayLine(lines.get(i), i);
            }
        }
        else {
            displayNothing();
        }
    }

    /**
     * Displays multiple lines to the screen.
     *
     * @param lines The lines to display.
     * @param lineNumbers The indexes of the lines to display.
     *
     * @throws SkyscraperTooTallException Throws this exception when the number of lines to draw is greater than the number that can fit on the screen.
     */
    protected void displayLines(List<GuiLine> lines, List<Integer> lineNumbers){
        if(lineNumbers.size() > MAXLINESPERSCREEN) {
            throw new SkyscraperTooTallException("More than 8 lines were selected for display");
        }
        if(lines.size() != 0 && lineNumbers.size() != 0) {
            for (int i = 0; i < lineNumbers.size(); i++) {
                displayLine(lines.get(lineNumbers.get(i)), lineNumbers.get(i));
            }
        }
        else {
            displayNothing();
        }
    }

    /**
     * Empties the screen display.
     */
    protected final void displayNothing(){
        displayLine(new GuiLine("", "", ""), 0);
    }

    /**
     * Gets the selection zone width.
     *
     * @return The selection zone width.
     */
    public int getSelectionZoneWidth() {
        return selectionZoneWidth;
    }

    /**
     * Gets the selection zone height.
     *
     * @return The selection zone height.
     */
    public int getSelectionZoneHeight() {
        return selectionZoneHeight;
    }

    /**
     * Sets the selection zone width.
     *
     * @param selectionZoneWidth The desired selection zone width.
     * @param newLines The list of lines to display on the menu with the new format.
     */
    public void setSelectionZoneWidth(int selectionZoneWidth, List<GuiLine> newLines){
        this.selectionZoneWidth = selectionZoneWidth;

        cursor.x = Range.clip(cursor.x,0,selectionZoneWidth);

        setLines(newLines);
    }

    /**
     * Sets the selection zone width.
     *
     * @param selectionZoneWidth The desired selection zone width.
     * @param newLines The list of lines to display on the menu with the new format.
     */
    public void setSelectionZoneWidth(int selectionZoneWidth, GuiLine[] newLines){
        this.selectionZoneWidth = selectionZoneWidth;

        cursor.x = Range.clip(cursor.x,0,selectionZoneWidth);

        setLines(newLines);
    }


    /**
     * Sets the selection zone height.
     *
     * @param selectionZoneHeight The desired selection zone height.
     * @param newLines The list of lines to display on the menu with the new format.
     */
    public void setSelectionZoneHeight(int selectionZoneHeight, List<GuiLine> newLines) {
        this.selectionZoneHeight = selectionZoneHeight;

        cursor.y = Range.clip(cursor.y,0,selectionZoneHeight);

        setLines(newLines);
    }

    /**
     * Sets the selection zone height.
     *
     * @param selectionZoneHeight The desired selection zone height.
     * @param newLines The list of lines to display on the menu with the new format.
     */
    public void setSelectionZoneHeight(int selectionZoneHeight, GuiLine[] newLines) {
        this.selectionZoneHeight = selectionZoneHeight;

        cursor.y = Range.clip(cursor.y,0,selectionZoneHeight);

        setLines(newLines);
    }

    /**
     * Sets the selection zone width and height.
     *
     * @param selectionZoneWidth The desired selection zone width.
     * @param selectionZoneHeight The desired selection zone height.
     * @param newLines The list of lines to display on the menu with the new format.
     */
    public void setSelectionZoneWidthAndHeight(int selectionZoneWidth, int selectionZoneHeight, GuiLine[] newLines){
        this.selectionZoneWidth = selectionZoneWidth;
        this.selectionZoneHeight = selectionZoneHeight;

        cursor.x = Range.clip(cursor.x,0,selectionZoneWidth);
        cursor.y = Range.clip(cursor.y,0,selectionZoneHeight);

        setLines(newLines);
    }

    /**
     * Sets the selection zone width and height.
     *
     * @param selectionZoneWidth The desired selection zone width.
     * @param selectionZoneHeight The desired selection zone height.
     * @param newLines The list of lines to display on the menu with the new format.
     */
    public void setSelectionZoneWidthAndHeight(int selectionZoneWidth, int selectionZoneHeight, List<GuiLine> newLines){
        this.selectionZoneWidth = selectionZoneWidth;
        this.selectionZoneHeight = selectionZoneHeight;

        cursor.x = Range.clip(cursor.x,0,selectionZoneWidth);
        cursor.y = Range.clip(cursor.y,0,selectionZoneHeight);

        setLines(newLines);
    }

    /**
     * Updates the menu's lines with new values.
     *
     * @param lines The list of lines that the menu will display.
     *
     * @throws InvalidSelectionZoneException Throws this exception if the selection zone width is not equal to the length of the
     *                                       text to display in the selection zone.
     * @throws WrongSkyscraperBlueprintException Throws this exception if there are not enough lines in newLines to fill the selection zone.
     */
    public void setLines(GuiLine[] lines){
        if(lines.length == selectionZoneHeight) {
            this.lines = new ArrayList<>();
            for (GuiLine line : lines) {
                if (!line.checkSelectionWidthSize(this.getSelectionZoneWidth())) {
                    throw new InvalidSelectionZoneException("Selection zone text width must match menu selection zone width");
                }
                this.lines.add(line);
            }
        }
        else {
            throw new WrongSkyscraperBlueprintException("New lines do not match the height of selection zone");
        }
    }

    /**
     * Updates the menu's lines with new values.
     *
     * @param lines The list of lines that the menu will display.
     *
     * @throws InvalidSelectionZoneException Throws this exception if the selection zone width is not equal to the length of the
     *                                       text to display in the selection zone.
     * @throws WrongSkyscraperBlueprintException Throws this exception if there are not enough or too many lines in newLines to fill the selection zone.
     */
    public void setLines(List<GuiLine> lines){
        if(lines.size() == selectionZoneHeight) {
            for(GuiLine line: lines) {
                if (!line.checkSelectionWidthSize(this.getSelectionZoneWidth())) {
                    throw new InvalidSelectionZoneException("Selection zone text width must match menu selection zone width");
                }
            }

            if(lines.size() == 0) {
                selectionZoneHeight = 1;
                lines.add(new GuiLine("","",""));
                Log.w("Menu Warning", "Warning: setLines() was passed an empty list. Side effects may include: headache, sad times, and a skyscraper collapse (Wrong skyscraper blueprint exception)");
            }

            this.lines = lines;
        }
        else {
            throw new WrongSkyscraperBlueprintException("New lines do not match the height of selection zone");
        }
    }

    /**
     * Cycles to the next upward part of the menu.
     */
    public void menuUp() {}

    /**
     * Cycles to the next downward part of the menu.
     */
    public void menuDown() {}
}