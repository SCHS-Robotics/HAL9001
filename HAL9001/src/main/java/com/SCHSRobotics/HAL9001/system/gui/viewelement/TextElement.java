package com.SCHSRobotics.HAL9001.system.gui.viewelement;

public class TextElement implements ViewElement {
    private String text;
    public TextElement(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }
}
