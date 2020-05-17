package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

import static com.SCHSRobotics.HAL9001.system.tempmenupackage.HALMathUtil.mod;
import static com.SCHSRobotics.HAL9001.system.tempmenupackage.StringUtils.bilateralStrip;
import static com.SCHSRobotics.HAL9001.system.tempmenupackage.StringUtils.repeatCharacter;
import static com.SCHSRobotics.HAL9001.system.tempmenupackage.StringUtils.setChar;

@HandlesEvents(events = WhileClickEvent.class)
public class TextInput implements AdvancedListener, CursorConfigurable {
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

    private String text;
    private String supportedCharacters;
    private HALGUI gui;
    private Button<Boolean> nextCharButton, previousCharButton;

    public TextInput(CharSet charSet, int entryLength, Button<Boolean> nextCharButton, Button<Boolean> previousCharButton) {
        text = repeatCharacter(RESERVED_CHAR, entryLength);
        supportedCharacters = charSet.getString() + RESERVED_CHAR;

        gui = HALGUI.getInstance();

        this.nextCharButton = nextCharButton;
        this.previousCharButton = previousCharButton;
    }

    public String getEnteredText() {
        return bilateralStrip(text, RESERVED_CHAR).replace(RESERVED_CHAR, ' ');
    }

    @Override
    public CriteriaPacket getCriteria() {
        GamepadEventCriteria<OnClickEvent, Button<Boolean>> buttonCriteria = new GamepadEventCriteria<>(nextCharButton, previousCharButton);

        CriteriaPacket criteriaPacket = new CriteriaPacket();
        criteriaPacket.add(buttonCriteria);

        return criteriaPacket;
    }

    @Override
    public boolean requestNoBlinkOnTriggeredUpdate() {
        return true;
    }

    @Override
    public boolean onEvent(Event eventIn) {
        if(eventIn instanceof WhileClickEvent) {
            WhileClickEvent event = (WhileClickEvent) eventIn;
            Button<Boolean> eventButton = event.getButton();

            char currentChar = supportedCharacters.charAt(gui.getCursorX());
            int nextCharIdx;

            if(nextCharButton.equals(eventButton)) {
                nextCharIdx = mod(supportedCharacters.indexOf(currentChar) + 1, supportedCharacters.length());
            }
            else {
                nextCharIdx = mod(supportedCharacters.indexOf(currentChar) - 1, supportedCharacters.length());
            }

            text = setChar(text, gui.getCursorX(), supportedCharacters.charAt(nextCharIdx));

            return true;
        }
        return false;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {}
}
