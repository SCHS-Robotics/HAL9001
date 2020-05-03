package com.SCHSRobotics.HAL9001.system.tempmenupackage;

public class TextElement implements ViewElement {
    private String text;
    public TextElement(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
