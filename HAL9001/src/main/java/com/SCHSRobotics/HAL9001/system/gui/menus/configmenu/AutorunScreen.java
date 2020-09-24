package com.SCHSRobotics.HAL9001.system.gui.menus.configmenu;

import com.SCHSRobotics.HAL9001.system.gui.HALMenu;
import com.SCHSRobotics.HAL9001.system.gui.Payload;
import com.SCHSRobotics.HAL9001.system.gui.SelectionZone;
import com.SCHSRobotics.HAL9001.system.gui.event.DataPacket;
import com.SCHSRobotics.HAL9001.system.gui.viewelement.TextElement;
import com.SCHSRobotics.HAL9001.system.gui.viewelement.eventlistener.EntireViewButton;
import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;

/**
 * The screen that gets displayed when the config system is automatically running a specific config file
 *
 * @author Cole Savage, Level Up
 * @since 1.1.0
 * @version 1.0.0
 *
 * Creation Date: 9/14/20
 */
public class AutorunScreen extends HALMenu {

    @Override
    protected void init(Payload payload) {
        selectionZone = new SelectionZone(0,0);

        ExceptionChecker.assertTrue(payload.idPresent(ConfigConstants.CONFIG_FILE_NAME_ID), new DumpsterFireException("Could not find autorun config file"));
        String configFilename = payload.get(ConfigConstants.CONFIG_FILE_NAME_ID);

        addItem(new TextElement("Autorunning " + configFilename + ", press the select button to select a different file to run."));
        addItem(new EntireViewButton()
                .onClick(payload.get(ConfigConstants.SELECT_BUTTON_ID), (DataPacket packet) -> {
                    payload.remove(ConfigConstants.CONFIG_FILE_NAME_ID);

                    //runs in standalone mode because you are selecting a file, but also saves selected file to autorun.
                    payload.add(ConfigConstants.STANDALONE_MODE_ID, true);
                    payload.add(ConfigConstants.SAVE_TO_AUTORUN_ID, true);
                    gui.inflate(new ConfigStartingMenu(payload));
                }));

    }
}
