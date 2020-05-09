package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;

public class TextInput implements ViewListener, BlinkingConfigurator {
    private static final char RESERVED_CHAR = '#';
    private static final String NEXT_CHAR = "nextChar", PREVIOUS_CHAR = "previousChar";

    public enum CharSet {
        LETTERS(RESERVED_CHAR+"abcdefghijklmnopqrstuvwxyz"), //26
        NUMBERS(RESERVED_CHAR+"0123456789"), //10
        SPECIAL_CHARACTERS(RESERVED_CHAR+"!?@$%^&*"), //8
        ALPHANUMERIC(LETTERS.charSet+NUMBERS.charSet.substring(1)),
        ALPHANUMERIC_SPECIAL(ALPHANUMERIC.charSet + SPECIAL_CHARACTERS.charSet.substring(1));

        private String charSet;
        CharSet(String charSet) {
            this.charSet = charSet;
        }

        public String getCharSet() {
            return charSet;
        }
    }

    public enum EntryMode {
        CYCLIC, NEW_MENU
    }
    private CharSet charSet;
    private int entryLength;
    private String text;
    private String textEntry;
    private CustomizableGamepad selectionControls;
    private EntryMode entryMode = EntryMode.CYCLIC;
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
        return entryMode == EntryMode.CYCLIC;
    }

    private class TextSelectionMenu extends HALMenu {
        private TextInput input;
        public TextSelectionMenu(Payload payload, TextInput input) {
            super(payload);
            this.input = input;
        }

        public TextSelectionMenu(TextInput input) {
            this(new Payload(), input);
        }

        @Override
        protected void init(Payload payload) {
        /*
        ####################
        #|abcd #|efgh #|ijkl
        #|mnop #|qrst #|uvwx
        #|yz01 #|2345 #|6789
        #|!?@$ #|%^&* #|#
        # | Done
         */

            int[] firstRow = new int[entryLength];
            for (int i = 0; i < entryLength; i++) {
                firstRow[i] = 1;
            }
            int[][] zoneMatrix = new int[][] {
                    firstRow,
                    {1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0},
                    {1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0},
                    {1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0},
                    {1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0},
                    {1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
            };
            selectionZone = new SelectionZone(zoneMatrix);

            int selectedXPos = 0;
            addItem(input);
        }
    }
}
