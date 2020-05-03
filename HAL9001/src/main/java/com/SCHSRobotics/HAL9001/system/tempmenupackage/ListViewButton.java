package com.SCHSRobotics.HAL9001.system.tempmenupackage;

public class ListViewButton extends ViewButton {

    private String text;

    public ListViewButton(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
