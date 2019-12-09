package com.SCHSRobotics.HAL9001.util.debug;

import android.os.Environment;
import android.util.Log;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.source.GUI.GUI;
import com.SCHSRobotics.HAL9001.system.source.GUI.GuiLine;
import com.SCHSRobotics.HAL9001.system.source.GUI.ScrollingListMenu;
import com.SCHSRobotics.HAL9001.system.subsystems.cursors.ConfigCursor;
import com.SCHSRobotics.HAL9001.util.annotations.AutonomousConfig;
import com.SCHSRobotics.HAL9001.util.annotations.TeleopConfig;
import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.functional_interfaces.BiFunction;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.ConfigParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigDebugMenu extends ScrollingListMenu {

    /**
    Internal state of the menu. Controls how it will react when a button is pressed.
    Menu State Key:

    START: The menu has just started, you are asked to select or delete a robot folder.
    TELEOP_AUTO_SELECT: Select autonomous or teleop.
    ROOT_DIR: The menu is currently in the root directory of the config (either the teleop or autonomous directory). Has options to edit, delete, or make a new config. Can also select a config to run.
    DELETE_CONFIG: Select a config from a list to delete. Will not transition to this state if there are no configs.
    CHOOSE_EDIT_CONFIG: Select a config from a list of configs to edit. Will not transition to this state if there are no configs.
    NEW_CONFIG: Create a new config. Type (as best you can) a name into the upper bar, then press done to submit the name. If name is robot_info or "" it will transition back to ROOT_DIR without doing anything.
    SELECT_SUBSYSTEM: Select a subsystem to configure from a list of subsystems.
    DONE: Configuration is complete, clear menu screen and mark as done so the GUI can close the menu.
    AUTO_RUN: Program being run is not standalone and has a config pre-loaded. Do not allow the user to change the config unless they press the center button to confirm they want to.
     */
    private enum MenuState {
        START, ROOT_DIR, DELETE_CONFIG, CHOOSE_EDIT_CONFIG, SELECT_SUBSYSTEM, NEW_CONFIG, CONFIGURE_SUBSYSTEM, DELETE_ROBOT, TELEOP_AUTO_SELECT
    }
    private MenuState menuState;

    /**
     * An enum representing whether it is being configured for teleop or autonomous.
     */
    private enum ConfigurationState {
        AUTONOMOUS, TELEOP
    }
    private ConfigurationState configState;

    //A String of supported characters for the menu to render.
    private static final String SUPPORTED_CHARS = "#abcdefghijklmnopqrstuvwxyz0123456789";
    //An arraylist version of SUPPORTED_CHARS.
    private final ArrayList<Character> VALID_CHARS = getValidChars();
    //The current filepath of the menu.
    private String currentFilepath;
    //The current config file's path.
    private String selectedConfigPath;
    //The name of the current subsystem being configured.
    private String selectedSubsystemName;
    //An internal storage variable used to buffer changes to the config.
    private Map<String,List<ConfigParam>> config;
    //A boolean value that represents whether the menu is currently creating a new config file.
    private boolean creatingNewConfig = false;
    //The GuiLine use to enter config names. It is saved here so that if the back button is pressed the config name can be edited.
    private GuiLine nameLine;
    //A custom version of the modulo function that allows negative numbers to wrap around.
    private BiFunction<Integer,Integer,Integer> customMod = new BiFunction<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer x, Integer m) {
            return (x % m + m) % m;
        }
    };
    //The path to the robot folder.
    private String robotFilepath;

    /**
     * Constructor for ConfigDebugMenu.
     *
     * @param gui - The GUI being used to render the menu.
     */
    public ConfigDebugMenu(GUI gui) {
        super(gui, new ConfigCursor(gui.robot,500), genInitialLines(Environment.getExternalStorageDirectory().getPath()+"/System64/"),1,genInitialLines(Environment.getExternalStorageDirectory().getPath()+"/System64/").size());
        menuState = MenuState.START;
        currentFilepath = Environment.getExternalStorageDirectory().getPath()+"/System64/";
        config = new HashMap<>();
        configState = ConfigurationState.AUTONOMOUS;
    }

    @Override
    public void onSelect() {

    }

    @Override
    public void onButton(String name, Button button) {

        switch (menuState) {
            //startup, shows all robot directories.
            case START:
                if(name.equals(ConfigCursor.SELECT)) {
                    if(!lines.get(cursor.y).postSelectionText.equals("Delete")) {
                        menuState = MenuState.TELEOP_AUTO_SELECT;
                        currentFilepath += lines.get(cursor.y).postSelectionText;

                        robotFilepath = currentFilepath;

                        initRobotConfig();

                        resetCursorPos();
                        setFolderSelectLines();
                    }
                    else if(new File(currentFilepath).listFiles().length > 0){
                        menuState = MenuState.DELETE_ROBOT;

                        resetCursorPos();
                        setRootDeleteLines();
                    }
                }
                break;

            case DELETE_ROBOT:
                if(name.equals(ConfigCursor.SELECT)) {
                    menuState = MenuState.START;

                    deleteDirectory(currentFilepath+lines.get(cursor.y).postSelectionText);

                    resetCursorPos();
                    setRootDirLines();
                }
                else if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    menuState = MenuState.START;

                    resetCursorPos();
                    setRootDirLines();
                }
                break;

            case TELEOP_AUTO_SELECT:
                if(name.equals(ConfigCursor.SELECT)) {
                    menuState = MenuState.ROOT_DIR;

                    configState = lines.get(cursor.y).postSelectionText.equals("autonomous") ? ConfigurationState.AUTONOMOUS : ConfigurationState.TELEOP;

                    currentFilepath += '/'+lines.get(cursor.y).postSelectionText;

                    resetCursorPos();
                    setRobotDirLines();
                }
                else if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    menuState = MenuState.START;

                    currentFilepath = Environment.getExternalStorageDirectory().getPath()+"/System64/";

                    resetCursorPos();
                    setRootDirLines();
                }
                break;

            //robot directory has been selected, showing all config files in the directory and new/edit/delete options.
            case ROOT_DIR:
                if(name.equals(ConfigCursor.SELECT)) {
                    if ((lines.get(cursor.y).postSelectionText.equals("Delete Config") || lines.get(cursor.y).postSelectionText.equals("Edit Config")) && genLines(currentFilepath,"robot_info.txt").size() > 0) {
                        menuState = lines.get(cursor.y).postSelectionText.equals("Delete Config") ? MenuState.DELETE_CONFIG : MenuState.CHOOSE_EDIT_CONFIG;

                        genDefaultConfigMap();
                        resetCursorPos();
                        setConfigListLines();

                    } else if (lines.get(cursor.y).postSelectionText.equals("New Config")) {
                        menuState = MenuState.NEW_CONFIG;

                        genDefaultConfigMap();
                        resetCursorPos();
                        setConfigNamingLines();
                    }
                }

                else if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    menuState = MenuState.TELEOP_AUTO_SELECT;

                    currentFilepath = robotFilepath;

                    resetCursorPos();
                    setFolderSelectLines();
                }
                break;
            //delete option selected. Selected config will be deleted.
            case DELETE_CONFIG:
                if(name.equals(ConfigCursor.SELECT)) {
                    menuState = MenuState.ROOT_DIR;

                    String configPath = currentFilepath + '/' + lines.get(cursor.y).postSelectionText + ".txt";
                    File configFile = new File(configPath);

                    if (!configFile.delete()) {
                        Log.e("File Issues", "Problem deleting file at " + configPath);
                    }

                    resetCursorPos();
                    setRobotDirLines();
                }

                else if(name.equals(ConfigCursor.BACK_BUTTON)) {

                    menuState = MenuState.ROOT_DIR;

                    resetCursorPos();
                    setRobotDirLines();
                }
                break;

            //new option selected. will prompt user to enter a name for the config file.
            case NEW_CONFIG:

                if(name.equals(ConfigCursor.SELECT)) {
                    if (!lines.get(cursor.y).postSelectionText.equals("Done")) {
                        ((ConfigCursor) cursor).setWriteMode(true);
                        char[] currentNameText = lines.get(0).selectionZoneText.toCharArray();
                        currentNameText[cursor.x] = VALID_CHARS.get((VALID_CHARS.indexOf(currentNameText[cursor.x]) + 1) % VALID_CHARS.size());

                        setConfigNamingLines(new GuiLine(new String(currentNameText), "Config Name"));
                    }
                    else {
                        ((ConfigCursor) cursor).setWriteMode(false);

                        String newConfigName = parseName(lines.get(0).selectionZoneText);

                        if (!newConfigName.equals("") && !newConfigName.equals("robot_info")) {

                            creatingNewConfig = true;
                            nameLine = lines.get(0);

                            selectedConfigPath = currentFilepath + '/' + newConfigName + ".txt";

                            menuState = MenuState.SELECT_SUBSYSTEM;

                            resetCursorPos();
                            setSubsystemSelectionLines();
                        }

                        else {
                            menuState = MenuState.ROOT_DIR;
                            Log.e("Oh No", "Hacker Alert!");
                            resetCursorPos();
                            setRobotDirLines();
                        }
                    }
                }

                else if(name.equals(ConfigCursor.REVERSE_SELECT) && !lines.get(cursor.y).postSelectionText.equals("Done")) {
                    ((ConfigCursor) cursor).setWriteMode(true);
                    char[] currentNameText = lines.get(0).selectionZoneText.toCharArray();
                    currentNameText[cursor.x] = VALID_CHARS.get(customMod.apply((VALID_CHARS.indexOf(currentNameText[cursor.x]) - 1),VALID_CHARS.size()));

                    setConfigNamingLines(new GuiLine(new String(currentNameText), "Config Name"));
                }

                else if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    menuState = MenuState.ROOT_DIR;

                    resetCursorPos();
                    setRobotDirLines();
                }

                break;

            //edit option selected. Selected config will be edited
            case CHOOSE_EDIT_CONFIG:

                if(name.equals(ConfigCursor.SELECT)) {

                    selectedConfigPath = currentFilepath + '/' + lines.get(cursor.y).postSelectionText + ".txt";

                    readConfigFile(selectedConfigPath);

                    menuState = MenuState.SELECT_SUBSYSTEM;

                    resetCursorPos();
                    setSubsystemSelectionLines();
                }

                else if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    menuState = MenuState.ROOT_DIR;

                    resetCursorPos();
                    setRobotDirLines();
                }

                break;
            case SELECT_SUBSYSTEM:
                if(name.equals(ConfigCursor.SELECT)) {
                    if (!lines.get(cursor.y).postSelectionText.equals("Done")) {
                        menuState = MenuState.CONFIGURE_SUBSYSTEM;
                        selectedSubsystemName = lines.get(cursor.y).postSelectionText;

                        resetCursorPos();
                        setSubsystemOptionsLines();
                    } else {
                        menuState = MenuState.ROOT_DIR;
                        creatingNewConfig = false;

                        writeConfigFile();

                        resetCursorPos();
                        setRobotDirLines();
                    }
                }
                if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    if(creatingNewConfig) {
                        menuState = MenuState.NEW_CONFIG;
                        creatingNewConfig = false;

                        genDefaultConfigMap();
                        resetCursorPos();
                        setConfigNamingLines(nameLine);
                    }
                    else {
                        menuState = MenuState.CHOOSE_EDIT_CONFIG;

                        resetCursorPos();
                        setConfigListLines();
                    }
                }
                break;

            case CONFIGURE_SUBSYSTEM:
                if(name.equals(ConfigCursor.SELECT)) {
                    if (!lines.get(cursor.y).postSelectionText.equals("Done")) {

                        String[] data = parseOptionLine(lines.get(cursor.y));

                        List<ConfigParam> subsystemParams = config.get(selectedSubsystemName);
                        ConfigParam currentParam = new ConfigParam("", new String[]{}, "");

                        for (ConfigParam param : subsystemParams) {
                            if (param.name.equals(data[0])) {
                                currentParam = param;
                                break;
                            }
                        }

                        if(currentParam.options.size() == 0) {
                            throw new DumpsterFireException("Couldn't find options for configParam");
                        }

                        lines.set(cursor.y, new GuiLine("#", currentParam.usesGamepad ? data[0] + " | " + currentParam.options.get((currentParam.options.indexOf(data[1]) + 1) % currentParam.options.size()) + " | " + data[2] : data[0] + " | " + currentParam.options.get((currentParam.options.indexOf(data[1]) + 1) % currentParam.options.size())));
                    }
                    else {
                        menuState = MenuState.SELECT_SUBSYSTEM;

                        updateConfigMapSubsystem(lines,selectedSubsystemName);

                        resetCursorPos();
                        setSubsystemSelectionLines();
                    }
                }
                else if(name.equals(ConfigCursor.REVERSE_SELECT) && !lines.get(cursor.y).postSelectionText.equals("Done")) {

                    String[] data = parseOptionLine(lines.get(cursor.y));

                    List<ConfigParam> subsystemParams = config.get(selectedSubsystemName);
                    ConfigParam currentParam = new ConfigParam("", new String[]{}, "");

                    for (ConfigParam param : subsystemParams) {
                        if (param.name.equals(data[0])) {
                            currentParam = param;
                            break;
                        }
                    }

                    if(currentParam.options.size() == 0) {
                        throw new DumpsterFireException("Couldn't find options for configParam");
                    }

                    lines.set(cursor.y, new GuiLine("#", currentParam.usesGamepad ? data[0] + " | " + currentParam.options.get(customMod.apply((currentParam.options.indexOf(data[1])-1), currentParam.options.size())) + " | " + data[2] : data[0] + " | " + currentParam.options.get(customMod.apply((currentParam.options.indexOf(data[1])-1), currentParam.options.size()))));
                }

                else if(name.equals(ConfigCursor.SWITCH_GAMEPAD) && !lines.get(cursor.y).postSelectionText.equals("Done")) {
                    String unparsedLine = lines.get(cursor.y).postSelectionText;
                    String currentOptionName = unparsedLine.substring(0, unparsedLine.indexOf('|')).trim();

                    int tempIdx = unparsedLine.substring(unparsedLine.indexOf('|') + 1).indexOf('|'); //This number is the index of the vertical bar in the substring formed by taking all the text after the first vertical bar.

                    String currentOptionValue;
                    String currentGamepadOptionValue;

                    if (tempIdx != -1) {
                        currentOptionValue = unparsedLine.substring(unparsedLine.indexOf('|') + 1, unparsedLine.indexOf('|') + tempIdx).trim();
                        currentGamepadOptionValue = unparsedLine.substring(unparsedLine.indexOf('|') + tempIdx + 3).trim();
                        List<ConfigParam> subsystemParams = config.get(selectedSubsystemName);
                        ConfigParam currentParam = new ConfigParam("", new String[]{}, "");

                        for (ConfigParam param : subsystemParams) {
                            if (param.name.equals(currentOptionName)) {
                                currentParam = param;
                                break;
                            }
                        }

                        if(currentParam.options.size() == 0) {
                            throw new DumpsterFireException("Couldn't find options for configParam");
                        }

                        lines.set(cursor.y, new GuiLine("#", currentParam.usesGamepad ? currentOptionName + " | " + currentOptionValue + " | " + currentParam.gamepadOptions.get((currentParam.gamepadOptions.indexOf(currentGamepadOptionValue) + 1) % currentParam.gamepadOptions.size()) : currentOptionName + " | " + currentOptionValue));
                    }
                }

                else if(name.equals(ConfigCursor.BACK_BUTTON)) {
                    menuState = MenuState.SELECT_SUBSYSTEM;

                    resetCursorPos();
                    setSubsystemSelectionLines();
                }
                break;
        }
    }

    /**
     * Sets the root directory lines. Shows all robot folders and gives delete option.
     */
    private void setRootDirLines() {
        List<GuiLine> newLines = genLines(currentFilepath);
        newLines.add(new GuiLine("#","Delete"));
        super.setSelectionZoneHeight(newLines.size(), newLines);
    }

    /**
     * Shows all deletable robot folders.
     */
    private void setRootDeleteLines() {
        List<GuiLine> newLines = genLines(currentFilepath);
        super.setSelectionZoneHeight(newLines.size(), newLines);
    }

    /**
     * Set the folder select lines. Run when you select a robot folder, lists teleop and autonomous folders.
     */
    private void setFolderSelectLines() {
        List<GuiLine> newLines = genLines(currentFilepath, "robot_info.txt");
        super.setSelectionZoneHeight(newLines.size(),newLines);
    }

    /**
     * Sets the normal configuration menu lines.
     */
    private void setRobotDirLines() {
        List<GuiLine> newLines = new ArrayList<>();
        newLines.add(new GuiLine("#", "New Config"));
        newLines.add(new GuiLine("#", "Edit Config"));
        newLines.add(new GuiLine("#", "Delete Config"));
        newLines.addAll(genLines(currentFilepath, "robot_info.txt"));

        super.setSelectionZoneWidthAndHeight(1,newLines.size(), newLines);
    }

    /**
     * Set the subsystem options lines. Each line is a different configurable option in the subsystem.
     */
    private void setSubsystemOptionsLines() {

        List<GuiLine> newLines = new ArrayList<>();

        for(ConfigParam param : config.get(selectedSubsystemName)) {
            newLines.add(new GuiLine("#",param.usesGamepad ? param.name+ " | " + param.currentOption + " | " + param.currentGamepadOption : param.name+ " | " + param.currentOption));
        }
        newLines.add(new GuiLine("#","Done"));

        super.setSelectionZoneHeight(newLines.size(), newLines);
    }

    /**
     * Sets config list lines. Lists out all config files.
     */
    private void setConfigListLines() {
        List<GuiLine> newLines = genLines(currentFilepath,"robot_info.txt");
        super.setSelectionZoneHeight(newLines.size(), newLines);
    }

    /**
     * Sets the config naming lines. Used for naming new config files.
     */
    private void setConfigNamingLines() {
        List<GuiLine> newLines = new ArrayList<>();
        newLines.add(new GuiLine("###############", "Config Name"));
        newLines.add(new GuiLine("###############", "Done"));
        super.setSelectionZoneWidthAndHeight(newLines.get(0).selectionZoneText.length(), newLines.size(), newLines);
    }

    /**
     * Sets the config naming lines. Used for naming new config files.
     *
     * @param initName - The nameLine value to initialize the nameline as.
     */
    private void setConfigNamingLines(GuiLine initName) {
        List<GuiLine> newLines = new ArrayList<>();
        newLines.add(initName);
        newLines.add(new GuiLine("###############", "Done"));
        super.setSelectionZoneWidthAndHeight(newLines.get(0).selectionZoneText.length(), newLines.size(), newLines);
    }

    /**
     * Sets the subsystem selection lines. Lists out all configurable subsystems.
     */
    private void setSubsystemSelectionLines() {
        List<GuiLine> newLines = new ArrayList<>();
        for(String subsystem : config.keySet()) {
            newLines.add(new GuiLine("#",subsystem));
        }
        newLines.add(new GuiLine("#", "Done"));
        super.setSelectionZoneWidthAndHeight(1, newLines.size(), newLines);
    }

    /**
     * Resets the cursor's position to (0,0).
     */
    private void resetCursorPos() {
        cursor.setX(0);
        cursor.setY(0);
    }

    /**
     * Deletes the directory at the specified filepath.
     *
     * @param filePath - The filepath to the folder to delete.
     */
    private void deleteDirectory(String filePath) {
        File dir = new File(filePath);
        File[] contents = dir.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDirectory(f.getPath()); //recursion :)
            }
        }
        dir.delete();
    }

    /**
     * Reads a config file from a specified filepath.
     *
     * @param filepath - The path to the config file.
     */
    private void readConfigFile(String filepath) {

        FileInputStream fis;

        try {
            fis = new FileInputStream(filepath);

            FileReader fReader;
            BufferedReader bufferedReader;

            try {
                fReader = new FileReader(fis.getFD());
                bufferedReader = new BufferedReader(fReader);

                int i = 0;
                String line;
                while((line = bufferedReader.readLine()) != null) {
                    String[] data = line.split(":");
                    config.get(data[0]).get(i).name = data[1];
                    config.get(data[0]).get(i).currentOption = data[2];
                    if(config.get(data[0]).get(i).usesGamepad) {
                        config.get(data[0]).get(i).currentGamepadOption = data[3].substring(0,data[3].indexOf('d')+1) + ' ' + data[3].substring(data[3].indexOf('d')+1); //adds a space in between the d in gamepad and the number
                    }
                    i++;
                }

                bufferedReader.close();
                fReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fis.getFD().sync();
                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRobotConfig() {
        FileInputStream fis;

        try {

            fis = new FileInputStream(currentFilepath + "/robot_info.txt");

            FileReader fReader;
            BufferedReader bufferedReader;

            try {
                fReader = new FileReader(fis.getFD());
                bufferedReader = new BufferedReader(fReader);

                String classname;
                while((classname = bufferedReader.readLine()) != null) {
                    Class subsystem = Class.forName(classname);
                    Method[] methods = subsystem.getDeclaredMethods();

                    boolean foundTeleopConfig = false;
                    boolean foundAutonomousConfig = false;

                    for(Method m : methods) {
                        //method must be annotated as TeleopConfig, have no parameters, be public and static, and return an array of config params
                        if(!foundTeleopConfig && m.isAnnotationPresent(TeleopConfig.class) && m.getReturnType() == ConfigParam[].class && m.getParameterTypes().length == 0 && Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                            Robot.teleopConfig.put(subsystem.getSimpleName(), Arrays.asList((ConfigParam[]) m.invoke(null)));
                            foundTeleopConfig = true;
                        }

                        //method must be annotated as AutonomousConfig, have no parameters, be public and static, and return an array of config params
                        if(!foundAutonomousConfig && m.isAnnotationPresent(AutonomousConfig.class) && m.getReturnType() == ConfigParam[].class && m.getParameterTypes().length == 0 && Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                            Robot.autonomousConfig.put(subsystem.getSimpleName(),Arrays.asList((ConfigParam[]) m.invoke(null)));
                            foundAutonomousConfig = true;
                        }

                        if(foundTeleopConfig && foundAutonomousConfig) {
                            break;
                        }
                    }
                }

                bufferedReader.close();
                fReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fis.getFD().sync();
                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates the default configuration map based on the values contained in Robot.
     */
    private void genDefaultConfigMap() {
        config = new HashMap<>();

        if(configState == ConfigurationState.AUTONOMOUS) {
            for (String subsystem : Robot.autonomousConfig.keySet()) {
                List<ConfigParam> params = new ArrayList<>();
                for (ConfigParam param : Robot.autonomousConfig.get(subsystem)) {
                    params.add(param.clone());
                }
                config.put(subsystem, params);
            }
        }
        else  {
            for (String subsystem : Robot.teleopConfig.keySet()) {
                List<ConfigParam> params = new ArrayList<>();
                for (ConfigParam param : Robot.teleopConfig.get(subsystem)) {
                    params.add(param.clone());
                }
                config.put(subsystem, params);
            }
        }
    }

    /**
     * Updates the config for a specific subsystem.
     *
     * @param newConfig - The new configuration settings.
     * @param subsystemName - The subsystem being configured.
     */
    private void updateConfigMapSubsystem(List<GuiLine> newConfig, String subsystemName) {
        removeDone(newConfig); //gets rid of the Done line

        for(int i = 0; i < newConfig.size(); i++) {
            String[] data = parseOptionLine(newConfig.get(i));

            config.get(subsystemName).get(i).name = data[0];
            config.get(subsystemName).get(i).currentOption = data[1];

            if(config.get(subsystemName).get(i).usesGamepad) {
                config.get(subsystemName).get(i).currentGamepadOption = data[2];
            }
        }
    }

    /**
     * Removes the "Done" line often found at the end of many list of GuiLines.
     *
     * @param lines - The lines to search for "Done" in.
     */
    private void removeDone(List<GuiLine> lines) {
        for(GuiLine line : lines) {
            if(line.postSelectionText.equals("Done")) {
                lines.remove(line);
                break;
            }
        }
    }

    /**
     * Writes a config file.
     */
    private void writeConfigFile() {

        File configFile = new File(selectedConfigPath);

        FileOutputStream fos;

        try {
            configFile.delete();
            configFile.createNewFile();

            fos = new FileOutputStream(selectedConfigPath, true);

            FileWriter fWriter;

            StringBuilder sb = new StringBuilder();
            for(String subsystem : config.keySet()) {
                for(ConfigParam param : config.get(subsystem)) {
                    sb.append(subsystem);
                    sb.append(':');
                    sb.append(param.name);
                    sb.append(':');
                    sb.append(param.currentOption);
                    if(param.usesGamepad) {
                        sb.append(':');
                        sb.append(param.currentGamepadOption);
                    }
                    sb.append("\r\n");
                }
            }

            sb.delete(sb.length()-2,sb.length()); //removes trailing \r\n characters so there isn't a blank line at the end of the file
            try {
                fWriter = new FileWriter(fos.getFD());

                fWriter.write(sb.toString());

                fWriter.flush();
                fWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fos.getFD().sync();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses a name created by the new config option.
     *
     * @param input - The unparsed name of the new config file.
     * @return The parsed name of the new config file.
     */
    private String parseName(String input) {

        int startIdx = 0;
        int endIdx = 0;

        //removes '#'s at beginning of string
        for(int i = 0; i < input.length(); i++) {
            if(input.charAt(i) != '#') {
                startIdx = i;
                break;
            }
        }

        //removes '#'s at end of string
        for(int i = input.length()-1; i >= 0; i--) {
            if(input.charAt(i) != '#') {
                endIdx = i;
                break;
            }
        }

        String parsedString = input.substring(startIdx,endIdx+1);
        parsedString = parsedString.equals("#") ? "" : parsedString;

        return parsedString.replace('#','_');
    }

    /**
     * Gets an arraylist version of the valid_chars string.
     *
     * @return An arraylist version of the valid_chars string.
     */
    private ArrayList<Character> getValidChars() {
        ArrayList<Character> outputList = new ArrayList<>();
        for(char c : SUPPORTED_CHARS.toCharArray()) {
            outputList.add(c);
        }
        return outputList;
    }

    /**
     * Generates program's initial lines.
     *
     * @param filePath - The filepath to use to generate the initial lines.
     * @return The program's initial lines.
     */
    private static ArrayList<GuiLine> genInitialLines(String filePath) {
        File rootDirectory = new File(filePath);
        File[] dirs = rootDirectory.listFiles();
        ArrayList<GuiLine> startingLines = new ArrayList<>();
        for(File dir : dirs) {
            startingLines.add(new GuiLine("#",dir.getName()));
        }
        startingLines.add(new GuiLine("#","Delete"));
        return startingLines;
    }

    /**
     * Generates GuiLines for a specific filepath.
     *
     * @param filePath - The path to the folder to generate GuiLines for.
     * @return The list of GuiLines representing all the folders/files in that directory.
     */
    private static ArrayList<GuiLine> genLines(String filePath) {
        File rootDirectory = new File(filePath);
        File[] dirs = rootDirectory.listFiles();
        ArrayList<GuiLine> startingLines = new ArrayList<>();
        for(File dir : dirs) {
            startingLines.add(new GuiLine("#",dir.getName().replace(".txt","")));
        }
        return startingLines;
    }

    /**
     * Generates GuiLines for a specific filepath.
     *
     * @param filePath - The path to the folder to generate GuiLines for.
     * @param exclude - A certain file or folder name to exclude from the list of GuiLines.
     * @return The list of GuiLines representing all the folders/files in that directory.
     */
    private static ArrayList<GuiLine> genLines(String filePath, String exclude) {
        File rootDirectory = new File(filePath);
        File[] dirs = rootDirectory.listFiles();
        ArrayList<GuiLine> startingLines = new ArrayList<>();
        for(File dir : dirs) {
            if (!dir.getName().equals(exclude)) {
                startingLines.add(new GuiLine("#",dir.getName().replace(".txt","")));
            }
        }
        return startingLines;
    }

    /**
     * Parse a GuiLine that represents a ConfigParam.
     *
     * @param line - The GuiLine to be parsed.
     * @return - A string array containing the name of the config param, the config param's current option, and, if applicable, the config param's current gamepad option.
     */
    private static String[] parseOptionLine(GuiLine line) {
        String unparsedLine = line.postSelectionText;
        String currentOptionName = unparsedLine.substring(0, unparsedLine.indexOf('|')).trim();

        int tempIdx = unparsedLine.substring(unparsedLine.indexOf('|') + 1).indexOf('|'); //This number is the index of the vertical bar in the substring formed by taking all the text after the first vertical bar.

        String currentOptionValue;
        String currentGamepadOptionValue;

        if(tempIdx != -1) {
            currentOptionValue = unparsedLine.substring(unparsedLine.indexOf('|') + 1, unparsedLine.indexOf('|') + tempIdx).trim();
            currentGamepadOptionValue = unparsedLine.substring(unparsedLine.indexOf('|') + tempIdx + 3).trim();
        }
        else {
            currentOptionValue = unparsedLine.substring(unparsedLine.indexOf('|') + 1).trim();
            currentGamepadOptionValue = "";
        }
        return new String[] {currentOptionName,currentOptionValue,currentGamepadOptionValue};
    }
}
