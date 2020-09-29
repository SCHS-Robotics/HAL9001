package com.SCHSRobotics.HAL9001.system.config;

import android.util.Log;

import com.SCHSRobotics.HAL9001.system.robot.LinkTo;
import com.SCHSRobotics.HAL9001.system.robot.SubSystem;
import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.exceptions.InvalidLinkException;
import com.SCHSRobotics.HAL9001.util.math.FakeNumpy;
import com.SCHSRobotics.HAL9001.util.math.datastructures.BidirectionalMap;
import com.SCHSRobotics.HAL9001.util.misc.HALFileUtil;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.internal.opmode.RegisteredOpModes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//3/17/20

public class HALConfig {

    private static HALConfig GLOBAL_INSTANCE = new HALConfig();
    private static HALConfig DEFAULT_CONFIG = new HALConfig();

    private Map<String, List<ConfigParam>> autonomousConfig = new HashMap<>(), teleopConfig = new HashMap<>();
    //maps name to id (reverse is id to name)
    private BidirectionalMap<String, String> subsystemIdLookup = new BidirectionalMap<>();
    private BidirectionalMap<SubSystem, String> subSystemNameLookup = new BidirectionalMap<>();

    public enum Mode {
        AUTONOMOUS, TELEOP
    }

    public HALConfig() {
    }

    private HALConfig(HALConfig sourceForClone) {
        for (Map.Entry<String, List<ConfigParam>> configEntry : sourceForClone.autonomousConfig.entrySet()) {
            List<ConfigParam> clonedParams = new ArrayList<>();
            for(ConfigParam param : configEntry.getValue()) {
                clonedParams.add(param.clone());
            }
            autonomousConfig.put(configEntry.getKey(), clonedParams);
        }
        for (Map.Entry<String, List<ConfigParam>> configEntry : sourceForClone.teleopConfig.entrySet()) {
            List<ConfigParam> clonedParams = new ArrayList<>();
            for (ConfigParam param : configEntry.getValue()) {
                clonedParams.add(param.clone());
            }
            teleopConfig.put(configEntry.getKey(), clonedParams);
        }
        for (Map.Entry<SubSystem, String> subSystemEntry : sourceForClone.subSystemNameLookup.entrySet()) {
            subSystemNameLookup.put(subSystemEntry.getKey(), subSystemEntry.getValue());
        }

        subsystemIdLookup.putAll(sourceForClone.subsystemIdLookup);
    }

    public static HALConfig getGlobalInstance() {
        return GLOBAL_INSTANCE;
    }

    public boolean addSubSystem(String name, SubSystem subSystem) {
        int i = 1;
        String tempName = name;
        while (teleopConfig.containsKey(tempName) || autonomousConfig.containsKey(tempName)) {
            tempName = name + i;
            i++;
        }

        Class<? extends SubSystem> subSystemClass = subSystem.getClass();

        i = 1;
        String className = subSystemClass.getSimpleName();
        String id = className;
        while(subsystemIdLookup.containsValue(id)) {
            id = className + i;
            i++;
        }
        subsystemIdLookup.put(tempName, id);
        subSystemNameLookup.put(subSystem, tempName);

        boolean foundTeleopConfig = false;
        boolean foundAutonomousConfig = false;

        try {
            Method[] methods = subSystemClass.getDeclaredMethods();
            for (Method m : methods) {

                //method must be annotated as TeleopConfig, have no parameters, be public and static, and return an array of config params
                if (!foundTeleopConfig && m.isAnnotationPresent(TeleopConfig.class) && m.getReturnType() == ConfigParam[].class && m.getParameterTypes().length == 0 && Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                    teleopConfig.put(tempName, Arrays.asList((ConfigParam[]) m.invoke(null)));
                    foundTeleopConfig = true;
                }

                //method must be annotated as AutonomousConfig, have no parameters, be public and static, and return an array of config params
                if (!foundAutonomousConfig && m.isAnnotationPresent(AutonomousConfig.class) && m.getReturnType() == ConfigParam[].class && m.getParameterTypes().length == 0 && Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                    autonomousConfig.put(tempName, Arrays.asList((ConfigParam[]) m.invoke(null)));
                    foundAutonomousConfig = true;
                }

                if (foundTeleopConfig && foundAutonomousConfig) {
                    break;
                }
            }
        } catch (Throwable e) {
            Log.e("HAL9001", "Problem loading config for subsystem " + subSystem.getClass().getSimpleName(), e);
        }

        return foundAutonomousConfig || foundTeleopConfig;
    }

    public void addOpmode(@NotNull OpMode opMode) {
        addOpmode(opMode, new ArrayList<>());
    }

    private void addOpmode(@NotNull OpMode opMode, @NotNull List<String> visitedOpmodes) {

        Class<? extends OpMode> opModeClass = opMode.getClass();

        Mode programMode = getOpModeType(opModeClass);
        String name = getOpModeName(opModeClass);

        //opModeRegister.add(name);
        visitedOpmodes.add(name);
        if(opModeClass.isAnnotationPresent(ProgramOptions.class)) {
            ProgramOptions options = opModeClass.getAnnotation(ProgramOptions.class);
            ExceptionChecker.assertNonNull(options, new NullPointerException("If you are seeing this, Java broke. Good luck!"));

            List<ConfigParam> settings = extractConfigFromSettings(options);
            if (settings.size() > 0) {
                switch (programMode) {
                    case AUTONOMOUS:
                        if(autonomousConfig.containsKey(name)) {
                            Log.e("HAL9001", "Error! Autonomous config already has an entry for "+name);
                        }
                        autonomousConfig.put(name, settings);
                        break;
                    case TELEOP:
                        if(teleopConfig.containsKey(name)) {
                            Log.e("HAL9001", "Error! Teleop config already has an entry for "+name);
                        }
                        teleopConfig.put(name, settings);
                        break;
                }
            }
        }

        if (opModeClass.isAnnotationPresent(LinkTo.class) && !visitedOpmodes.contains(name)) {
            LinkTo link = opModeClass.getAnnotation(LinkTo.class);
            ExceptionChecker.assertNonNull(link, new NullPointerException("If you are seeing this, Java broke. Good luck!"));

            OpMode linkedOpmode = RegisteredOpModes.getInstance().getOpMode(link.destination());
            ExceptionChecker.assertNonNull(linkedOpmode, new InvalidLinkException("Link to nonexistent opmode. '" + link.destination() + "' does not exist"));

            addOpmode(opMode, visitedOpmodes);
        }
    }

    public void setConfig(Mode mode, String lookup, List<ConfigParam> configParams) {
        if(mode == Mode.TELEOP) {
            teleopConfig.put(lookup, configParams);
        }
        else {
            autonomousConfig.put(lookup, configParams);
        }
    }

    @NotNull
    private List<ConfigParam> extractConfigFromSettings(@NotNull ProgramOptions opts) {
        List<ConfigParam> options = new ArrayList<>();

        //cleanup, removes any duplicated enum classes from options.
        Class<? extends Enum<?>>[] nonDuplicatedSettings = FakeNumpy.removeDuplicates(opts.options());

        if(nonDuplicatedSettings.length > 0) {
            for(Class<? extends Enum<?>> option : nonDuplicatedSettings) {
                Enum<?>[] enums = option.getEnumConstants();
                if(enums.length == 0) {
                    continue;
                }
                options.add(new ConfigParam(option.getSimpleName(), enums[0]));
            }
        }
        return options;
    }

    public static String getOpModeName(@NotNull Class<? extends OpMode> opModeClass) {
        String name;

        if (opModeClass.isAnnotationPresent(TeleOp.class)) {
            TeleOp op = opModeClass.getAnnotation(TeleOp.class);
            ExceptionChecker.assertNonNull(op, new NullPointerException("If you are seeing this, Java broke. Good luck!"));
            name = op.name();
        } else if (opModeClass.isAnnotationPresent(Autonomous.class)) {
            Autonomous op = opModeClass.getAnnotation(Autonomous.class);
            ExceptionChecker.assertNonNull(op, new NullPointerException("If you are seeing this, Java broke. Good luck!"));
            name = op.name();
        }
        else {
            throw new DumpsterFireException("Program "+opModeClass.getSimpleName()+" can't be run without @TeleOp or @Autonomous");
        }
        return name;
    }

    public static Mode getOpModeType(@NotNull Class<? extends OpMode> opModeClass) {
        if(opModeClass.isAnnotationPresent(Autonomous.class)) {
            return Mode.AUTONOMOUS;
        }
        else if(opModeClass.isAnnotationPresent(TeleOp.class)) {
            return Mode.TELEOP;
        }
        else {
            throw new DumpsterFireException("Program "+opModeClass.getSimpleName()+" can't be run without @TeleOp or @Autonomous");
        }
    }

    @Nullable
    public List<ConfigParam> getConfig(Mode mode, SubSystem subSystem) {
        return getConfig(mode, subSystemNameLookup.getForward(subSystem));
    }

    @Nullable
    public List<ConfigParam> getConfig(Mode mode, String name) {
        if ((mode == Mode.AUTONOMOUS && !autonomousConfig.containsKey(name)) || (mode == Mode.TELEOP && !teleopConfig.containsKey(name))) {
            return null;
        }
        return mode == Mode.AUTONOMOUS ? autonomousConfig.get(name) : teleopConfig.get(name);
    }

    @Nullable
    public List<ConfigParam> getConfig(@NotNull OpMode opMode) {
        Class<? extends OpMode> opModeClass = opMode.getClass();
        return getConfig(getOpModeType(opModeClass), getOpModeName(opModeClass));
    }

    public Set<String> getSubsystemNames() {
        Set<String> subsystemNames = new HashSet<>(autonomousConfig.keySet());
        subsystemNames.addAll(teleopConfig.keySet());
        return subsystemNames;
    }

    public void clearConfig() {
        autonomousConfig.clear();
        teleopConfig.clear();

        subsystemIdLookup.clear();
    }

    public static void setAsDefault(HALConfig defaultConfig) {
        DEFAULT_CONFIG.clearConfig();

        DEFAULT_CONFIG.autonomousConfig.putAll(defaultConfig.autonomousConfig);
        DEFAULT_CONFIG.teleopConfig.putAll(defaultConfig.teleopConfig);
        DEFAULT_CONFIG.subsystemIdLookup.putAll(defaultConfig.subsystemIdLookup);
    }

    public static HALConfig getDefaultConfig() {
        return DEFAULT_CONFIG.clone();
    }

    public static void setGlobalConfigAsDefault() {
        setAsDefault(GLOBAL_INSTANCE);
    }

    public static void updateGlobalConfig(HALConfig config) {
        GLOBAL_INSTANCE.autonomousConfig.clear();
        GLOBAL_INSTANCE.teleopConfig.clear();

        GLOBAL_INSTANCE.autonomousConfig.putAll(config.autonomousConfig);
        GLOBAL_INSTANCE.teleopConfig.putAll(config.teleopConfig);
    }

    public static void saveConfig(Mode mode, HALConfig config, String filepath) {
        StringBuilder sb = new StringBuilder();
        for(String subsystem : config.getSubsystemNames()) {
            List<ConfigParam> params = config.getConfig(mode, subsystem);
            params = params == null ? new ArrayList<>() : params;

            for(ConfigParam param : params) {
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

        if(sb.length() > 2) {
            sb.delete(sb.length() - 2, sb.length()); //removes trailing \r\n characters so there isn't a blank line at the end of the file
        }

        HALFileUtil.save(filepath, sb.toString());
    }

    public static HALConfig readConfig(Mode mode, String filepath) {
        FileInputStream fis;

        HALConfig outputConfig = HALConfig.getDefaultConfig();

        try {
            fis = new FileInputStream(filepath);

            FileReader fReader;
            BufferedReader bufferedReader;

            //TODO: Make this a bit less jank. Right now throwing errors is perfectly fine here because that's how it fixes out of date config files.

            try {
                fReader = new FileReader(fis.getFD());
                bufferedReader = new BufferedReader(fReader);

                int i = 0;
                String lastSubsystem = "\n";
                String line;
                while((line = bufferedReader.readLine()) != null) {
                    String[] data = line.split(":");
                    if(!data[0].equals(lastSubsystem)) {
                        i = 0;
                    }
                    List<ConfigParam> params = outputConfig.getConfig(mode, data[0]);
                    if(params != null && params.get(i).name.equals(data[1].trim())) {
                        params.get(i).name = data[1].trim();
                        params.get(i).currentOption = data[2].trim();
                        if (params.get(i).usesGamepad) {
                            params.get(i).currentGamepadOption = data[3].trim();
                        }
                        lastSubsystem = data[0];

                        i++;
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputConfig;
    }

    @Override
    public HALConfig clone() {
        return new HALConfig(this);
    }

    public boolean isEmpty() {
        return autonomousConfig.isEmpty() && teleopConfig.isEmpty();
    }
}