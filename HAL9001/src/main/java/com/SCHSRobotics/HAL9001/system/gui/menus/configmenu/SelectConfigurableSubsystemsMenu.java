package com.SCHSRobotics.HAL9001.system.gui.menus.configmenu;

import com.SCHSRobotics.HAL9001.system.config.HALConfig;
import com.SCHSRobotics.HAL9001.system.gui.DynamicSelectionZone;
import com.SCHSRobotics.HAL9001.system.gui.HALMenu;
import com.SCHSRobotics.HAL9001.system.gui.Payload;
import com.SCHSRobotics.HAL9001.system.gui.UniqueID;
import com.SCHSRobotics.HAL9001.system.gui.event.DataPacket;
import com.SCHSRobotics.HAL9001.system.gui.viewelement.eventlistener.EntireViewButton;
import com.SCHSRobotics.HAL9001.system.gui.viewelement.eventlistener.ViewButton;
import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.misc.HALFileUtil;

import java.util.Set;

/**
 * A menu used to select a specific subsystem to configure out a list of possible configurable subsystems.
 */
@DynamicSelectionZone(pattern = {true})
public class SelectConfigurableSubsystemsMenu extends HALMenu {
    protected static final UniqueID SELECTED_SUBSYSTEM_ID = new UniqueID("selected subsystem"), LOCAL_CONFIG_ID = new UniqueID("local config");

    private final String robotFilepath;
    private String configFilename;
    private ConfigSelectionMode selectionMode;
    private HALConfig localConfig;

    public SelectConfigurableSubsystemsMenu(Payload payload) {
        super(payload);

        ExceptionChecker.assertTrue(payload.idPresent(ConfigConstants.ROBOT_FILEPATH_ID), new DumpsterFireException("Must provide robot filepath."));
        ExceptionChecker.assertTrue(payload.idPresent(ConfigConstants.CONFIG_FILE_NAME_ID), new DumpsterFireException("Must provide config filename."));

        robotFilepath = payload.get(ConfigConstants.ROBOT_FILEPATH_ID);
        selectionMode = payload.idPresent(ConfigConstants.SELECTION_MODE_ID) ? payload.get(ConfigConstants.SELECTION_MODE_ID) : ConfigSelectionMode.TELEOP;
        configFilename = payload.get(ConfigConstants.CONFIG_FILE_NAME_ID);

        String configFilepath = robotFilepath+selectionMode.filepathExtension+'/'+configFilename.replace(' ','_')+ConfigConstants.CONFIG_FILE_EXTENSION;

        if(!configFilename.replace(' ','_').equals(ConfigConstants.CONFIG_METADATA_FILENAME) && HALFileUtil.fileExists(configFilepath)) {
            localConfig = HALConfig.readConfig(selectionMode.mode, configFilepath);
        }
        else {
            localConfig = HALConfig.getDefaultConfig();
        }

        payload.add(LOCAL_CONFIG_ID, localConfig);
    }

    @Override
    protected void init(Payload payload) {
        ExceptionChecker.assertTrue(payload.idPresent(LOCAL_CONFIG_ID), new DumpsterFireException("Local config not present in payload, this should not be possible."));
        ExceptionChecker.assertTrue(payload.idPresent(ConfigConstants.CONFIG_FILE_NAME_ID), new DumpsterFireException("Must provide config filename."));

        localConfig = payload.get(LOCAL_CONFIG_ID);
        configFilename = payload.get(ConfigConstants.CONFIG_FILE_NAME_ID);

        if(configFilename.replace(' ','_').equals(ConfigConstants.CONFIG_METADATA_FILENAME)) {
            payload.remove(ConfigConstants.CONFIG_FILE_NAME_ID);
            gui.back(payload);
        }

        String configFilepath = robotFilepath+selectionMode.filepathExtension+'/'+configFilename.replace(' ','_')+ConfigConstants.CONFIG_FILE_EXTENSION;

        addItem(new EntireViewButton()
            .onClick(payload.get(ConfigConstants.BACK_BUTTON_ID), (DataPacket packet) -> {
                payload.remove(ConfigConstants.CONFIG_FILE_NAME_ID);
                gui.back(payload);
            })
            .onClick(payload.get(ConfigConstants.FORWARD_BUTTON_ID), (DataPacket packet) -> gui.forward(payload)));

        Set<String> subsystemNames = localConfig.getSubsystemNames();
        for(String subsystemName : subsystemNames) {
            addItem(new ViewButton(ConfigConstants.OPTION_PREFIX+subsystemName)
                .onClick(payload.get(ConfigConstants.SELECT_BUTTON_ID), (DataPacket packet) -> {
                    payload.add(SELECTED_SUBSYSTEM_ID, subsystemName);
                    gui.inflate(new ConfigureSubSystemMenu(), payload);
                }));
        }
        addItem(new ViewButton(ConfigConstants.OPTION_PREFIX+"Save")
            .onClick(payload.get(ConfigConstants.SELECT_BUTTON_ID), (DataPacket packet) -> {
                HALConfig.saveConfig(selectionMode.mode, localConfig, configFilepath);
                payload.remove(ConfigConstants.CONFIG_FILE_NAME_ID);
                gui.inflate(new ConfigStartingMenu(payload));
            }));
    }
}
