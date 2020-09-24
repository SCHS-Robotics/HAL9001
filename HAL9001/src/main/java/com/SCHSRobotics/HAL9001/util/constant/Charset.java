package com.SCHSRobotics.HAL9001.util.constant;

public enum Charset {
    LETTERS("abcdefghijklmnopqrstuvwxyz"), //26
    NUMBERS("0123456789"), //10
    SPECIAL_CHARACTERS("!?@$%^&"), //8
    ALPHANUMERIC(LETTERS.chars+NUMBERS.chars),
    ALPHANUMERIC_SPECIAL(ALPHANUMERIC.chars + SPECIAL_CHARACTERS.chars),
    NUMBERS_DECIMAL(NUMBERS.chars + '.');

    private String chars;
    Charset(String chars) {
        this.chars = chars;
    }

    public String getChars() {
        return chars;
    }
}
