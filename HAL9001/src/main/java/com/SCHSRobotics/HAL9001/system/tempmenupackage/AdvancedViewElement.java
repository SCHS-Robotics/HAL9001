package com.SCHSRobotics.HAL9001.system.tempmenupackage;

public interface AdvancedViewElement extends ViewElement {
    void append(char c);
    void remove(int charIdx);
    void setChar(int charIdx, char c);
}
