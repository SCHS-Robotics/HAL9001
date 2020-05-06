package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;

public class TextInput implements ViewListener, BlinkingConfigurator {
    private static final char RESERVED_CHAR = '#';
    private static final String NEXT_CHAR = "nextChar", PREVIOUS_CHAR = "previousChar";

    public enum CharSet {
        LETTERS(RESERVED_CHAR+"abcdefghijklmnopqrstuvwxyz"),
        NUMBERS(RESERVED_CHAR+"0123456789"),
        SPECIAL_CHARACTERS(RESERVED_CHAR+"!?@$%^&*"),
        ALPHANUMERIC(LETTERS.charSet+NUMBERS.charSet.substring(1)),
        ALPHANUMERIC_SPECIAL(ALPHANUMERIC.charSet + SPECIAL_CHARACTERS.charSet.substring(1));

        private String charSet;
        CharSet(String charSet) {
            this.charSet = charSet;
        }
    }
    private CharSet charSet;
    private int entryLength;
    private String text;
    private String textEntry;
    private CustomizableGamepad selectionControls;
    public TextInput(String text, CharSet charSet, int entryLength, Button<Boolean> nextCharButton, Button<Boolean> previousCharButton) {
        textEntry = new String(new char[entryLength]).replace('\0', RESERVED_CHAR); //Repeats reserved char entryLength times
        this.text = text;
        this.entryLength = entryLength;
        this.charSet = charSet;
        selectionControls = new CustomizableGamepad(HALGUI.getInstance().getRobot());
        selectionControls.addButton(NEXT_CHAR, nextCharButton);
        selectionControls.addButton(PREVIOUS_CHAR, previousCharButton);
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean requestNoBlinkOnTriggeredUpdate() {
        return true;
    }
}
