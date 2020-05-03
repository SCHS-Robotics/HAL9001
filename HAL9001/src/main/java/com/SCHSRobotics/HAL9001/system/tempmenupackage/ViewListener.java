package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.misc.Button;

public interface ViewListener extends ViewElement {
    ViewListener onClick(Button<Boolean> button, Program program);
    boolean update();
}
