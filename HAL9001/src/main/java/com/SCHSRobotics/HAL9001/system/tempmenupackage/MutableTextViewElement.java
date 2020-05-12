package com.SCHSRobotics.HAL9001.system.tempmenupackage;

public interface MutableTextViewElement extends ViewElement {
    void setText(String text);
    void append(char c);
    void remove(int charIdx);
    void setChar(int charIdx, char c);
}
