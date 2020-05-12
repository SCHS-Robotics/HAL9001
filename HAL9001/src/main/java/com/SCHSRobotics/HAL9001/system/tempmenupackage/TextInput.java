package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;

import static com.SCHSRobotics.HAL9001.system.tempmenupackage.HALMathUtil.mod;
import static com.SCHSRobotics.HAL9001.system.tempmenupackage.StringUtils.bilateralStrip;
import static com.SCHSRobotics.HAL9001.system.tempmenupackage.StringUtils.repeatCharacter;
import static com.SCHSRobotics.HAL9001.system.tempmenupackage.StringUtils.setChar;

public class TextInput implements ButtonListener, CursorConfigurable {
    private static final char RESERVED_CHAR = '#';
    private static final String NEXT_CHAR = "nextChar", PREVIOUS_CHAR = "previousChar";

    public enum CharSet {
        LETTERS("abcdefghijklmnopqrstuvwxyz"), //26
        NUMBERS("0123456789"), //10
        SPECIAL_CHARACTERS("!?@$%^&*"), //8
        ALPHANUMERIC(LETTERS.charSet+NUMBERS.charSet),
        ALPHANUMERIC_SPECIAL(ALPHANUMERIC.charSet + SPECIAL_CHARACTERS.charSet);

        private String charSet;
        CharSet(String charSet) {
            this.charSet = charSet;
        }

        public String getString() {
            return charSet;
        }
    }

    private String supportedCharacters;
    //todo make cursor position gui-accessable
    private HALMenu menu;
    private String text;
    private CustomizableGamepad selectionControls;
    private Timer disabledTimer;
    public TextInput(HALMenu menu, CharSet charSet, int entryLength, Button<Boolean> nextCharButton, Button<Boolean> previousCharButton) {
        this.menu = menu;

        text = repeatCharacter(RESERVED_CHAR, entryLength);
        supportedCharacters = charSet.getString() + RESERVED_CHAR;
        disabledTimer = new Timer();

        selectionControls = new CustomizableGamepad(HALGUI.getInstance().getRobot());
        selectionControls.addButton(NEXT_CHAR, nextCharButton);
        selectionControls.addButton(PREVIOUS_CHAR, previousCharButton);
    }

    @Override
    public boolean update() {
        if(!disabledTimer.requiredTimeElapsed()) {
            return false;
        }

        boolean triggerUpdate = false;
        if(selectionControls.getInput(NEXT_CHAR)) {
            char currentChar = supportedCharacters.charAt(menu.getCursorX());
            int nextCharIdx = mod(supportedCharacters.indexOf(currentChar) + 1, supportedCharacters.length());
            text = setChar(text, menu.getCursorX(), supportedCharacters.charAt(nextCharIdx));
            triggerUpdate = true;
        }
        else if(selectionControls.getInput(PREVIOUS_CHAR)) {
            char currentChar = supportedCharacters.charAt(menu.getCursorX());
            int nextCharIdx = mod(supportedCharacters.indexOf(currentChar) - 1, supportedCharacters.length());
            text = setChar(text, menu.getCursorX(), supportedCharacters.charAt(nextCharIdx));
            triggerUpdate = true;
        }
        return triggerUpdate;
    }

    public String getEnteredText() {
        return bilateralStrip(text, RESERVED_CHAR).replace(RESERVED_CHAR, ' ');
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean requestNoBlinkOnTriggeredUpdate() {
        return true;
    }

    @Override
    public void disable(long timeDisabledMs) {
        disabledTimer.start((double) timeDisabledMs, TimeUnit.MILLISECONDS);
    }
}
