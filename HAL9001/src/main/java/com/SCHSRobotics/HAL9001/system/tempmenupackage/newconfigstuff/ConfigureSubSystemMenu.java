package com.SCHSRobotics.HAL9001.system.tempmenupackage.newconfigstuff;

import com.SCHSRobotics.HAL9001.system.tempmenupackage.DataPacket;
import com.SCHSRobotics.HAL9001.system.tempmenupackage.DynamicSelectionZone;
import com.SCHSRobotics.HAL9001.system.tempmenupackage.EntireViewButton;
import com.SCHSRobotics.HAL9001.system.tempmenupackage.HALMathUtil;
import com.SCHSRobotics.HAL9001.system.tempmenupackage.HALMenu;
import com.SCHSRobotics.HAL9001.system.tempmenupackage.Payload;
import com.SCHSRobotics.HAL9001.system.tempmenupackage.ViewButton;
import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.misc.ConfigParam;
import com.SCHSRobotics.HAL9001.util.misc.HALConfig;

import java.util.Arrays;
import java.util.List;

/**
 * A menu used to select the desired configuration options for a given subsystem.
 *
 * @author Cole Savage, Level Up
 * @since 1.1.0
 * @version 1.0.0
 *
 * Creation Date: 9/11/20
 */
@DynamicSelectionZone(pattern = {true})
public class ConfigureSubSystemMenu extends HALMenu {

    @Override
    protected void init(Payload payload) {
        ExceptionChecker.assertTrue(payload.idPresent(SelectConfigurableSubsystemsMenu.LOCAL_CONFIG_ID), new DumpsterFireException("No local config in payload, this should not be possible."));
        ExceptionChecker.assertTrue(payload.idPresent(SelectConfigurableSubsystemsMenu.SELECTED_SUBSYSTEM_ID), new DumpsterFireException("No subsystem selected, this should not be possible."));
        ExceptionChecker.assertTrue(payload.idPresent(ConfigConstants.SELECTION_MODE_ID), new DumpsterFireException("Selection mode not present, this should not be possible."));

        //Extracting data from the payload.
        HALConfig localConfig = payload.get(SelectConfigurableSubsystemsMenu.LOCAL_CONFIG_ID);
        String subsystemName = payload.get(SelectConfigurableSubsystemsMenu.SELECTED_SUBSYSTEM_ID);
        ConfigSelectionMode selectionMode = payload.get(ConfigConstants.SELECTION_MODE_ID);

        //Forward and back buttons.
        addItem(new EntireViewButton()
                .onClick(payload.get(ConfigConstants.BACK_BUTTON_ID), (DataPacket packet) -> gui.back(payload))
                .onClick(payload.get(ConfigConstants.FORWARD_BUTTON_ID), (DataPacket packet) -> gui.forward(payload)));

        //Lists each config option for the selectable subsystem as a selectable entry in the menu.
        List<ConfigParam> configOptions = localConfig.getConfig(selectionMode.mode, subsystemName);
        if(configOptions != null) {
            for (ConfigParam option : configOptions) {
                if (option.usesGamepad) {
                    addItem(new ViewButton(ConfigConstants.OPTION_PREFIX + option.name + ConfigConstants.OPTION_DIVIDER + option.currentOption + ConfigConstants.OPTION_DIVIDER + option.currentGamepadOption)
                            //Cycles to the next option in line when the select button is clicked.
                            .onClick(payload.get(ConfigConstants.SELECT_BUTTON_ID), (DataPacket packet) -> {
                                ViewButton thisButton = packet.getListener();

                                String[] parsedData = parseOptionLine(thisButton.getText());
                                String optionName = parsedData[0];
                                String optionValue = parsedData[1];

                                int paramIdx = getOptionIdx(configOptions, optionName);
                                ConfigParam currentParam = configOptions.get(paramIdx);
                                nextOption(currentParam, optionValue);

                                thisButton.setText(ConfigConstants.OPTION_PREFIX + optionName + ConfigConstants.OPTION_DIVIDER + currentParam.currentOption + ConfigConstants.OPTION_DIVIDER + currentParam.currentGamepadOption);
                            })
                            //Cycles to the previous option in line when the reverse select button is clicked.
                            .onClick(payload.get(ConfigConstants.REVERSE_SELECT_BUTTON_ID), (DataPacket packet) -> {
                                ViewButton thisButton = packet.getListener();

                                String[] parsedData = parseOptionLine(thisButton.getText());
                                String optionName = parsedData[0];
                                String optionValue = parsedData[1];

                                int paramIdx = getOptionIdx(configOptions, optionName);
                                ConfigParam currentParam = configOptions.get(paramIdx);
                                previousOption(currentParam, optionValue);

                                thisButton.setText(ConfigConstants.OPTION_PREFIX + optionName + ConfigConstants.OPTION_DIVIDER + currentParam.currentOption + ConfigConstants.OPTION_DIVIDER + currentParam.currentGamepadOption);
                            })
                            //Cycles to the next gamepad option when the change gamepad button is clicked.
                            .onClick(payload.get(ConfigConstants.CHANGE_GAMEPAD_BUTTON_ID), (DataPacket packet) -> {
                                ViewButton thisButton = packet.getListener();

                                String[] parsedData = parseOptionLine(thisButton.getText());
                                String optionName = parsedData[0];
                                String gamepadOption = parsedData[2];

                                int paramIdx = getOptionIdx(configOptions, optionName);
                                ConfigParam currentParam = configOptions.get(paramIdx);

                                int currentGamepadOptionIdx = Arrays.asList(ConfigParam.GAMEPAD_OPTIONS).indexOf(gamepadOption);
                                int nextGamepadOptionIdx = HALMathUtil.mod(currentGamepadOptionIdx + 1, ConfigParam.GAMEPAD_OPTIONS.length);
                                currentParam.currentGamepadOption = ConfigParam.GAMEPAD_OPTIONS[nextGamepadOptionIdx];

                                thisButton.setText(ConfigConstants.OPTION_PREFIX + optionName + ConfigConstants.OPTION_DIVIDER + currentParam.currentOption + ConfigConstants.OPTION_DIVIDER + currentParam.currentGamepadOption);
                            }));
                }
                else {
                    //Cycles to the next option in line when the select button is clicked.
                    addItem(new ViewButton(ConfigConstants.OPTION_PREFIX + option.name + ConfigConstants.OPTION_DIVIDER + option.currentOption)
                            .onClick(payload.get(ConfigConstants.SELECT_BUTTON_ID), (DataPacket packet) -> {
                                ViewButton thisButton = packet.getListener();

                                String[] parsedData = parseOptionLine(thisButton.getText());
                                String optionName = parsedData[0];
                                String optionValue = parsedData[1];

                                int paramIdx = getOptionIdx(configOptions, optionName);
                                ConfigParam currentParam = configOptions.get(paramIdx);
                                nextOption(currentParam, optionValue);

                                thisButton.setText(ConfigConstants.OPTION_PREFIX + optionName + ConfigConstants.OPTION_DIVIDER + currentParam.currentOption);
                            })
                            //Cycles to the previous option in line when the reverse select button is clicked.
                            .onClick(payload.get(ConfigConstants.REVERSE_SELECT_BUTTON_ID), (DataPacket packet) -> {
                                ViewButton thisButton = packet.getListener();

                                String[] parsedData = parseOptionLine(thisButton.getText());
                                String optionName = parsedData[0];
                                String optionValue = parsedData[1];

                                int paramIdx = getOptionIdx(configOptions, optionName);
                                ConfigParam currentParam = configOptions.get(paramIdx);
                                previousOption(currentParam, optionValue);

                                thisButton.setText(ConfigConstants.OPTION_PREFIX + optionName + ConfigConstants.OPTION_DIVIDER + currentParam.currentOption);
                            }));
                }
            }
        }

        //Adds the "done" button to the bottom of the menu.
        addItem(new ViewButton(ConfigConstants.OPTION_PREFIX + "Done")
            .onClick(payload.get(ConfigConstants.SELECT_BUTTON_ID), (DataPacket packet) -> {
                localConfig.setConfig(selectionMode.mode, subsystemName, configOptions);
                payload.add(SelectConfigurableSubsystemsMenu.LOCAL_CONFIG_ID, localConfig);
                gui.back(payload);
            }));
    }

    /**
     * Parses a raw text line into information about the specific config option that the line is associated with.
     * This includes information like the name of the option and the option's current value.
     *
     * @param line The raw line to be parsed.
     * @return An output array of parsed data. The format of the array is {option name, option value, gamepad option}. If there is no gamepad option, that section of the output is null.
     */
    private static String[] parseOptionLine(String line) {
        String[] parsedLine = new String[3];

        //Removes OPTION_PREFIX from entered line
        String unparsedLine = line.substring(line.indexOf(ConfigConstants.OPTION_PREFIX) + ConfigConstants.OPTION_PREFIX.length());

        int firstDividerIdx = unparsedLine.indexOf(ConfigConstants.OPTION_DIVIDER);
        String optionName = unparsedLine.substring(0, firstDividerIdx);
        parsedLine[0] = optionName;

        int secondDividerIdx = unparsedLine.indexOf(ConfigConstants.OPTION_DIVIDER, firstDividerIdx + 1);
        String optionValue = secondDividerIdx == -1 ? unparsedLine.substring(firstDividerIdx + 1) : unparsedLine.substring(firstDividerIdx + 1, secondDividerIdx);
        parsedLine[1] = optionValue;

        if (secondDividerIdx != -1) {
            String gamepadOption = unparsedLine.substring(secondDividerIdx + 1);
            parsedLine[2] = gamepadOption;
        }

        return parsedLine;
    }

    /**
     * Gets the index of a specific config option in a given list of config options. Searches by name.
     *
     * @param params The list of config options to search through.
     * @param optionName The name of the option that is being searched for.
     * @return Either the index of the desired option in the list, or -1 if the index is not found.
     */
    private static int getOptionIdx(List<ConfigParam> params, String optionName) {
        for(int i = 0; i < params.size(); i++) {
            if(params.get(i).name.equals(optionName)) {
                return i;
            }
        }
        return  -1;
    }

    /**
     * Moves the config option to its next possible value.
     *
     * @param param The config option that is being moved to its next possible value.
     * @param currentOptionValue The current value of the config option.
     */
    private static void nextOption(ConfigParam param, String currentOptionValue) {
        int currentOptionIdx = param.options.indexOf(currentOptionValue);
        //The mathutil mod IS different than the % operator, but this is only evident for negative numbers. The % operator would have worked here, but was not used for the sake of simplicity.
        int nextOptionIdx = HALMathUtil.mod(currentOptionIdx + 1, param.options.size());

        String newOptionValue = param.options.get(nextOptionIdx);
        ExceptionChecker.assertNonNull(newOptionValue, new DumpsterFireException("Previous option for config parameter " + param.name + " was null, this should not be possible."));
        param.currentOption = newOptionValue;
    }

    /**
     * Moves the config option to its previous possible value.
     *
     * @param param The config option that is being moved ot its previous possible value.
     * @param currentOptionValue The current value of the config option.
     */
    private static void previousOption(ConfigParam param, String currentOptionValue) {
        int currentOptionIdx =param.options.indexOf(currentOptionValue);
        //The mathutil mod IS different than the % operator, but this is only evident for negative numbers.
        int nextOptionIdx = HALMathUtil.mod(currentOptionIdx - 1, param.options.size());

        String newOptionValue = param.options.get(nextOptionIdx);
        ExceptionChecker.assertNonNull(newOptionValue, new DumpsterFireException("Previous option for config parameter " + param.name + " was null, this should not be possible."));
        param.currentOption = newOptionValue;
    }
}
