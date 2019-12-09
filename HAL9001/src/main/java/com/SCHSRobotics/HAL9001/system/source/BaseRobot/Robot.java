/*
 * Filename: Robot.java
 * Author: Andrew Liang, Dylan Zueck, Cole Savage
 * Team Name: Level Up
 * Date: 2017
 */

package com.SCHSRobotics.HAL9001.system.source.BaseRobot;

import android.os.Environment;
import android.util.Log;

import com.SCHSRobotics.HAL9001.system.menus.ConfigMenu;
import com.SCHSRobotics.HAL9001.system.source.GUI.GUI;
import com.SCHSRobotics.HAL9001.util.annotations.AutonomousConfig;
import com.SCHSRobotics.HAL9001.util.annotations.StandAlone;
import com.SCHSRobotics.HAL9001.util.annotations.TeleopConfig;
import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.NotBooleanInputException;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.ConfigData;
import com.SCHSRobotics.HAL9001.util.misc.ConfigParam;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An abstract class representing the physical robot.
 */
public abstract class Robot {

    private static final String VISION_CYCLE = "VisionCycler";

    private static Size cameraSize;

    //A map relating the name of each subsystem in the robot to that subsystem's corresponding autonomous config
    public static Map<String, List<ConfigParam>> autonomousConfig = new HashMap<>();
    //A map relating the name of each subsystem in the robot to that subsystem's corresponding teleop config
    public static Map<String, List<ConfigParam>> teleopConfig = new HashMap<>();
    //A hashmap mapping the name of a subsystem to the actual subsystem object.
    private final Map<String, SubSystem> subSystems;
    //The opmode the robot is running.
    private OpMode opMode;
    //A boolean value specifying whether or not to use a GUI, whether or not to use a config, and whether or not to close the current config GUI.
    private boolean useGui;
    //A boolean value specifying whether or not to use the config system.
    private boolean useConfig;
    //The GUI the robot uses to render the menus.
    public GUI gui;
    //The gamepads used to control the robot.
    public volatile Gamepad gamepad1, gamepad2;
    //The telemetry used to print lines to the driver station.
    public final Telemetry telemetry;
    //The hardwaremap used to map software representations of hardware to the actual hardware.
    public final HardwareMap hardwareMap;

    private OpenCvCamera camera;
    private final CustomizableGamepad visionCycler;
    private boolean useViewport, pipelineSet, cameraStarted;
    private final int cameraMonitorViewId;

    private List<VisionSubSystem> visionSubSystems;

    private static boolean errorThrown = false;
    private static Exception thrownException;

    /**
     * Constructor for robot.
     *
     * @param opMode - The opmode the robot is currently running.
     */
    public Robot(OpMode opMode)
    {
        this.opMode = opMode;
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        subSystems = new HashMap<>();
        visionSubSystems = new ArrayList<>();

        useGui = false;
        useConfig = false;
        useViewport = false;
        pipelineSet = false;
        cameraStarted = false;

        visionCycler = new CustomizableGamepad(this);

        cameraSize = new Size(320, 240);
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId","id", hardwareMap.appContext.getPackageName());
    }

    /**
     * Adds a subsystem to the robot's hashmap of subsystems and, if the subsystem uses config, load the default config.
     *
     * @param name - The name of the subsystem.
     * @param subSystem - The subsystem object.
     */
    protected final void putSubSystem(String name,  SubSystem subSystem)
    {
        subSystems.put(name, subSystem);

        if(subSystem instanceof VisionSubSystem) {
            visionSubSystems.add((VisionSubSystem) subSystem);
        }

        if(subSystem.usesConfig) {

            boolean foundTeleopConfig = false;
            boolean foundAutonomousConfig = false;

            try {
                Method[] methods = subSystem.getClass().getDeclaredMethods();
                for(Method m : methods) {

                    //method must be annotated as TeleopConfig, have no parameters, be public and static, and return an array of config params
                    if(!foundTeleopConfig && m.isAnnotationPresent(TeleopConfig.class) && m.getReturnType() == ConfigParam[].class && m.getParameterTypes().length == 0 && Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                        teleopConfig.put(subSystem.getClass().getSimpleName(),Arrays.asList((ConfigParam[]) m.invoke(null)));
                        if(!useGui) {
                            gui = new GUI(this, new Button(1, Button.BooleanInputs.noButton));
                            useGui = true;
                        }
                        useConfig = true;
                        foundTeleopConfig = true;
                    }

                    //method must be annotated as AutonomousConfig, have no parameters, be public and static, and return an array of config params
                    if(!foundAutonomousConfig && m.isAnnotationPresent(AutonomousConfig.class) && m.getReturnType() == ConfigParam[].class && m.getParameterTypes().length == 0 && Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                        autonomousConfig.put(subSystem.getClass().getSimpleName(),Arrays.asList((ConfigParam[]) m.invoke(null)));
                        if(!useGui) {
                            gui = new GUI(this, new Button(1, Button.BooleanInputs.noButton));
                            useGui = true;
                        }
                        useConfig = true;
                        foundAutonomousConfig = true;
                    }

                    if(foundTeleopConfig && foundAutonomousConfig) {
                        break;
                    }
                }
            }
            catch (Exception e) {
                Log.e("Error","Problem loading config for subsystem "+name,e);
            }
        }
    }

    /**
     * Instantiates the GUI and allows the robot to use a GUI.
     *
     * @param cycleButton - The button used to cycle through multiple menus in GUI.
     */
    protected final void startGui(Button cycleButton) {
        if(!useGui) {
            gui = new GUI(this, cycleButton);
            useGui = true;
        }
        else {
            gui.overrideCycleButton(cycleButton);
        }
    }

    protected final void enableViewport(Button cycleButton) {
        if(!cycleButton.isBoolean) {
            throw new NotBooleanInputException("Vision cycle button must be a boolean input");
        }
        if(!useViewport) {
            visionCycler.addButton(VISION_CYCLE, cycleButton);
            useViewport = true;
        }
        else {
            visionCycler.removeButton(VISION_CYCLE);
            visionCycler.addButton(VISION_CYCLE, cycleButton);
        }
    }

    public final boolean isViewportEnabled() {
        return useViewport;
    }

    /**
     * Returns whether the robot has already been set up to use the GUI.
     *
     * @return - Whether the GUI has been instantiated.
     */
    public final boolean usesGUI() {
        return useGui;
    }

    /**
     * Runs all the initialization methods of every subsystem and the GUI. Also starts the config and creates the config file tree if needed.
     */
    public final void init()
    {

        this.gamepad1 = opMode.gamepad1;
        this.gamepad2 = opMode.gamepad2;

        File root = new File(Environment.getExternalStorageDirectory().getPath()+"/System64");
        if(!root.exists()) {
            Log.i("File Creation",root.mkdir() ? "Directory created!" : "File error, couldn't create directory");
        }

        if(useConfig) {

            //create overall robot folder
            File robotConfigDirectory = new File(Environment.getExternalStorageDirectory().getPath()+"/System64/robot_"+this.getClass().getSimpleName());
            if(!robotConfigDirectory.exists()) {
                Log.i("File Creation",robotConfigDirectory.mkdir() ? "Directory created!" : "File error, couldn't create directory");
                writeFile(robotConfigDirectory.getPath() + "/robot_info.txt", "");
            }

            //create autonomous directory in robot folder
            File autoDir = new File(robotConfigDirectory.getPath() + "/autonomous");
            if(!autoDir.exists()) {
                Log.i("File Creation",autoDir.mkdir() ? "Directory created!" : "File error, couldn't create directory");
                writeFile(autoDir.getPath() + "/robot_info.txt", "");
            }

            //create teleop directory in robot folder
            File teleopDir = new File(robotConfigDirectory.getPath() + "/teleop");
            if(!teleopDir.exists()) {
                Log.i("File Creation",teleopDir.mkdir() ? "Directory created!" : "File error, couldn't create directory");
                writeFile(teleopDir.getPath() + "/robot_info.txt", "");
            }

            //Get all names of subsystem objects being used in this robot and write it to the outer robot_info.txt file
            //These names will be used in the config debugger
            StringBuilder sb = new StringBuilder();
            for(SubSystem subSystem : subSystems.values()) {
                if(subSystem.usesConfig) {
                    sb.append(subSystem.getClass().getName());
                    sb.append("\r\n");
                }
            }
            if(sb.length() > 2) {
                sb.delete(sb.length() - 2, sb.length()); //removes trailing \r\n characters so there isn't a blank line at the end of the file
            }

            writeFile(robotConfigDirectory.getPath() + "/robot_info.txt", sb.toString());

            //If the opmode is annotated as StandAlone, add the config menu in standalone mode.
            if(opMode.getClass().isAnnotationPresent(StandAlone.class)) {
                if(opMode instanceof BaseAutonomous) {
                    gui.addMenu("config",new ConfigMenu(gui,autoDir.getPath(),true));
                }
                else if(opMode instanceof BaseTeleop) {
                    gui.addMenu("config",new ConfigMenu(gui,teleopDir.getPath(),true));
                }
            }
            //Otherwise, add the config menu in non-standalone mode.
            else {
                gui.addMenu("config",new ConfigMenu(gui,robotConfigDirectory.getPath(),false));
            }

            gui.setActiveMenu("config");
        }

        if(useGui) {
            gui.start();
        }

        for (SubSystem subSystem : subSystems.values()){
            try
            {
                subSystem.init();
            }
            catch (Exception ex)
            {
                telemetry.clearAll();
                telemetry.addData("ERROR!!!", ex.getMessage());
                telemetry.update();
                if(!errorThrown) {
                    Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
                    thrownException = ex;
                    errorThrown = true;
                }
            }
        }

        if(useViewport) {
            camera = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
        }
        else {
            camera = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK);
        }

        if(enableVisionRequested()) {
            camera.openCameraDevice();
            camera.setPipeline(new Pipeline());
            camera.startStreaming((int) Math.round(cameraSize.width),(int) Math.round(cameraSize.height), OpenCvCameraRotation.UPRIGHT);
            pipelineSet = true;
            cameraStarted = true;
        }
    }

    /**
     * Runs methods in a loop during init. Runs all subsystem init_loop() methods and draws the configuration menu.
     */
    public final void init_loop() {

        if(!errorThrown) {
            this.gamepad1 = opMode.gamepad1;
            this.gamepad2 = opMode.gamepad2;

            if (useGui) {
                gui.drawCurrentMenuInit();

                if (useConfig) {
                    if (((ConfigMenu) gui.getMenu("config")).isDone) {
                        gui.removeMenu("config");
                        useConfig = false;
                    }
                }
            }

            for (SubSystem subSystem : subSystems.values()) {

                try {
                    subSystem.init_loop();
                } catch (Exception ex) {
                    telemetry.clearAll();
                    telemetry.addData("ERROR!!!", ex.getMessage());
                    telemetry.update();
                    if (!errorThrown) {
                        Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
                        thrownException = ex;
                        errorThrown = true;
                    }
                }
            }
            boolean enableVision = enableVisionRequested();
            if (enableVision && !pipelineSet) {
                camera.openCameraDevice();
                camera.setPipeline(new Pipeline());
                camera.startStreaming((int) Math.round(cameraSize.width), (int) Math.round(cameraSize.height), OpenCvCameraRotation.UPRIGHT);
                pipelineSet = true;
                cameraStarted = true;
            } else if (enableVision && !cameraStarted) {
                camera.openCameraDevice();
                camera.startStreaming((int) Math.round(cameraSize.width), (int) Math.round(cameraSize.height), OpenCvCameraRotation.UPRIGHT);
                cameraStarted = true;
            } else if (!enableVision && pipelineSet) {
                camera.stopStreaming();
                camera.closeCameraDevice();
                cameraStarted = false;
            }
        }
        else {
            telemetry.clearAll();
            telemetry.addData("ERROR!!!", thrownException.getMessage());
            telemetry.update();
        }
    }

    /**
     * Runs this method when the user presses the start button.
     */
    public final void onStart() {

        if(!errorThrown) {
            this.gamepad1 = opMode.gamepad1;
            this.gamepad2 = opMode.gamepad2;

            if (useGui) {
                if (gui.isMenuPresent("config")) {
                    gui.removeMenu("config");
                }
                gui.onStart();
            }

            for (SubSystem subSystem : subSystems.values()) {
                try {
                    subSystem.start();
                } catch (Exception ex) {
                    telemetry.clearAll();
                    telemetry.addData("ERROR!!!", ex.getMessage());
                    telemetry.update();
                    if(!errorThrown) {
                        Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
                        thrownException = ex;
                        errorThrown = true;
                    }
                }
            }
            boolean enableVision = enableVisionRequested();
            if (enableVision && !pipelineSet) {
                camera.openCameraDevice();
                camera.setPipeline(new Pipeline());
                camera.startStreaming((int) Math.round(cameraSize.width), (int) Math.round(cameraSize.height), OpenCvCameraRotation.UPRIGHT);
                pipelineSet = true;
                cameraStarted = true;
            } else if (enableVision && !cameraStarted) {
                camera.openCameraDevice();
                camera.startStreaming((int) Math.round(cameraSize.width), (int) Math.round(cameraSize.height), OpenCvCameraRotation.UPRIGHT);
                cameraStarted = true;
            } else if (!enableVision && pipelineSet) {
                camera.stopStreaming();
                camera.closeCameraDevice();
                cameraStarted = false;
            }
        }
        else {
            telemetry.clearAll();
            telemetry.addData("ERROR!!!", thrownException.getMessage());
            telemetry.update();
        }
    }

    /**
     * Runs subsystem handle() methods and GUI drawCurrentMenu() every frame in driver controlled programs.
     */
    public final void driverControlledUpdate()
    {
        if(!errorThrown) {
            this.gamepad1 = opMode.gamepad1;
            this.gamepad2 = opMode.gamepad2;

            if (useGui) {
                gui.drawCurrentMenu();
            }

            for (SubSystem subSystem : subSystems.values()) {
                try {
                    subSystem.handle();
                } catch (Exception ex) {
                    telemetry.clearAll();
                    telemetry.addData("ERROR!!!", ex.getMessage());
                    telemetry.update();
                    if(!errorThrown) {
                        Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
                        thrownException = ex;
                        errorThrown = true;
                    }
                }
            }
            boolean enableVision = enableVisionRequested();
            if (enableVision && !pipelineSet) {
                camera.openCameraDevice();
                camera.setPipeline(new Pipeline());
                camera.startStreaming((int) Math.round(cameraSize.width), (int) Math.round(cameraSize.height), OpenCvCameraRotation.UPRIGHT);
                pipelineSet = true;
                cameraStarted = true;
            } else if (enableVision && !cameraStarted) {
                camera.openCameraDevice();
                camera.startStreaming((int) Math.round(cameraSize.width), (int) Math.round(cameraSize.height), OpenCvCameraRotation.UPRIGHT);
                cameraStarted = true;
            } else if (!enableVision && pipelineSet) {
                camera.stopStreaming();
                camera.closeCameraDevice();
                cameraStarted = false;
            }
        }
        else {
            telemetry.clearAll();
            telemetry.addData("ERROR!!!", thrownException.getMessage());
            telemetry.update();
        }
    }

    /**
     * Runs the stop functions for all subsystems and the GUI.
     */
    public final void stopAllComponents(){

        if(!errorThrown) {
            if (useGui) {
                gui.stop();
            }

            for (SubSystem subSystem : subSystems.values()) {
                try {
                    subSystem.stop();
                } catch (Exception ex) {
                    telemetry.clearAll();
                    telemetry.addData("ERROR!!!", ex.getMessage());
                    telemetry.update();
                    if (!errorThrown) {
                        Log.e(this.getClass().getSimpleName(), ex.getMessage(), ex);
                        thrownException = ex;
                        errorThrown = true;
                    }
                }
            }

            if (cameraStarted) {
                camera.stopStreaming();
                camera.closeCameraDevice();
            }
        }
        else {
            telemetry.clearAll();
            telemetry.addData("ERROR!!!", thrownException.getMessage());
            telemetry.update();
        }
    }

    /**
     * Gets a specific subsystem from the hashmap.
     *
     * @param name - The name of the subsystem.
     * @return - The subsystem with the given identifier in the hashmap.
     */
    public final SubSystem getSubSystem(String name)
    {
        return subSystems.get(name);
    }

    /**
     * Replaces a subsystem already in the hashmap with another subsystem.
     *
     * @param name - The name of the subsystem to be replaced.
     * @param subSystem - The new subsystem.
     * @return - The new subsystem that was passed in as a parameter.
     */
    public final SubSystem eOverrideSubSystem(String name,  SubSystem subSystem)
    {
        subSystems.put(name, subSystem);
        return subSystem;
    }

    /**
     * Gets the opmode the robot is currently running.
     *
     * @return - The opmode the robot is running.
     */
    public final OpMode getOpMode() {
        return opMode;
    }

    /**
     * Pulls a customizable gamepad object from the teleop config map. Allows for easily getting gamepad data from the configuration.
     *
     * @param subsystem - The subsystem to pull the gamepad controls for.
     * @return - A customizable gamepad containing the configured controls for that subsystem.
     */
    public final CustomizableGamepad pullControls(SubSystem subsystem) {
        return pullControls(subsystem.getClass().getSimpleName());
    }

    /**
     * Pulls a customizable gamepad object from the teleop config map. Allows for easily getting gamepad data from the configuration.
     *
     * @param subsystem - The name of the subsystem to pull the gamepad controls for.
     * @return - A customizable gamepad containing the configured controls for that subsystem.
     */
    public final CustomizableGamepad pullControls(String subsystem) {
        List<ConfigParam> configParams = teleopConfig.get(subsystem);
        CustomizableGamepad gamepad = new CustomizableGamepad(this);
        for(ConfigParam param : configParams) {
            if(param.usesGamepad) {
                gamepad.addButton(param.name,param.toButton());
            }
        }
        return gamepad;
    }

    public final ConfigData pullNonGamepad2(SubSystem subsystem) {
        return pullNonGamepad2(subsystem.getClass().getSimpleName());
    }

    public final ConfigData pullNonGamepad2(String subsystem) {
        List<ConfigParam> configParamsTeleop = new ArrayList<>();
        List<ConfigParam> configParamsAuto = new ArrayList<>();

        if(teleopConfig.keySet().contains(subsystem)) {
            configParamsTeleop = teleopConfig.get(subsystem);
        }
        if(autonomousConfig.keySet().contains(subsystem)) {
            configParamsAuto = autonomousConfig.get(subsystem);
        }

        if(configParamsAuto == null) {
            configParamsAuto = new ArrayList<>();
        }
        if(configParamsTeleop == null) {
            configParamsTeleop = new ArrayList<>();
        }

        Map<String,Object> output = new HashMap<>();

        for (ConfigParam param : configParamsAuto) {
            if (!param.usesGamepad) {
                output.put(param.name, param.vals.get(param.options.indexOf(param.currentOption)));
            }
        }

        for (ConfigParam param : configParamsTeleop) {
            if (!param.usesGamepad) {
                output.put(param.name, param.vals.get(param.options.indexOf(param.currentOption)));
            }
        }

        return new ConfigData(output);
    }

    /**
     * Pulls a map of all non-gamepad-related config settings from the global config. The map format is (option name) -> (option value)
     *
     * @param subsystem - The subsystem to pull the gamepad controls for.
     * @return - A map relating the name of each non-gamepad option to that option's value.
     */
    @Deprecated
    public final Map<String,Object> pullNonGamepad(SubSystem subsystem) {
        return pullNonGamepad(subsystem.getClass().getSimpleName());
    }

    /**
     * Pulls a map of all non-gamepad-related config settings from the global config. The map format is (option name) -> (option value)
     *
     * @param subsystem - The name of the subsystem to pull the gamepad controls for.
     * @return - A map relating the name of each non-gamepad option to that option's value.
     */
    @Deprecated
    public final Map<String,Object> pullNonGamepad(String subsystem) {

        List<ConfigParam> configParamsTeleop = new ArrayList<>();
        List<ConfigParam> configParamsAuto = new ArrayList<>();

        if(teleopConfig.keySet().contains(subsystem)) {
            configParamsTeleop = teleopConfig.get(subsystem);
        }
        if(autonomousConfig.keySet().contains(subsystem)) {
            configParamsAuto = autonomousConfig.get(subsystem);
        }

        Map<String,Object> output = new HashMap<>();

        for (ConfigParam param : configParamsAuto) {
            if (!param.usesGamepad) {
                output.put(param.name, param.vals.get(param.options.indexOf(param.currentOption)));
            }
        }

        for (ConfigParam param : configParamsTeleop) {
            if (!param.usesGamepad) {
                output.put(param.name, param.vals.get(param.options.indexOf(param.currentOption)));
            }
        }

        return output;
    }

    /**
     * Gets if the program the robot is running is a teleop program.
     *
     * @return Whether the program being run is a teleop program.
     */
    public final boolean isTeleop() {
        return opMode instanceof BaseTeleop;
    }

    /**
     * Gets if the program the robot is running is an autonomous program.
     *
     * @return Whether the program being run is an autonomous program.
     */
    public final boolean isAutonomous() {
        return opMode instanceof BaseAutonomous;
    }

    /**
     * Gets whether the robot's current program is running.
     *
     * @return Whether the robot's current program is running.
     */
    public final boolean opModeIsActive() {
        if(!isTeleop() && !isAutonomous()) {
            throw new DumpsterFireException("Program is not an instance of BaseAutonomous or BaseTeleop, cannot tell if its running. A lot of other things are probably broken too if you're seeing this.");
        }
        return ((LinearOpMode) opMode).opModeIsActive();
    }

    /**
     * Gets whether the robot's current program has requested to be stopped.
     *
     * @return Whether the robot's current program has requested to be stopped.
     */
    public final boolean isStopRequested() {
        if(!isTeleop() && !isAutonomous()) {
            throw new DumpsterFireException("Program is not an instance of BaseAutonomous or BaseTeleop, cannot tell if its running. A lot of other things are probably broken too if you're seeing this.");
        }
        return ((LinearOpMode) opMode).isStopRequested();
    }

    /**
     * Gets whether the robot's current program has been started.
     *
     * @return Whether the robot's current program has been started.
     */
    public final boolean isStarted() {
        if(!isTeleop() && !isAutonomous()) {
            throw new DumpsterFireException("Program is not an instance of BaseAutonomous or BaseTeleop, cannot tell if its running. A lot of other things are probably broken too if you're seeing this.");
        }
        return ((LinearOpMode) opMode).isStarted();
    }

    /**
     * Writes data to a specified filepath. Creates the file if it doesn't exist, overwrites it if it does.
     *
     * @param filePath - The path of the file to write to.
     * @param data - The data to write to the file/
     */
    private void writeFile(String filePath,  String data) {

        FileOutputStream fos;

        try {
            File file = new File(filePath);
            if(file.exists()) {
                boolean fileDeleted = file.delete();
                if(!fileDeleted) {
                    Log.e("File Error", "Could not delete file at "+filePath);
                }

                boolean fileCreated = file.createNewFile();
                if(!fileCreated) {
                    Log.e("File Error","Could not create file at "+filePath);
                }
            }

            fos = new FileOutputStream(filePath, true);

            FileWriter fWriter;

            try {
                fWriter = new FileWriter(fos.getFD());

                fWriter.write(data);

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

    private boolean enableVisionRequested() {
        boolean anythingEnabled = false;
        for(VisionSubSystem visionSubsystem : visionSubSystems) {
            anythingEnabled |= visionSubsystem.isEnabled();
        }
        return anythingEnabled;
    }

    protected void setCameraSize(int width, int height) {
        cameraSize = new Size(width, height);
    }

    private class Pipeline extends OpenCvPipeline {

        @Override
        public Mat processFrame(Mat input) {
            Mat returnMat = new Mat();
            int activeMatIdx = -1;
            int minPriority = Integer.MAX_VALUE;
            for(VisionSubSystem visionSubsystem : visionSubSystems) {
                if(visionSubsystem.getPriority() < minPriority && visionSubsystem.isEnabled()) {
                    activeMatIdx = visionSubSystems.indexOf(visionSubsystem);
                    minPriority = visionSubsystem.getPriority();
                }
            }

            if(activeMatIdx == -1) {
                returnMat = Mat.zeros((int) Math.round(cameraSize.width),(int) Math.round(cameraSize.height), CvType.CV_8UC3);
            }
            else {
                for(VisionSubSystem visionSubsystem : visionSubSystems) {
                    if(visionSubsystem.isEnabled()) {
                        if(visionSubSystems.indexOf(visionSubsystem) == activeMatIdx) {
                            returnMat = visionSubsystem.onCameraFrame(input.clone());
                        }
                        else {
                            visionSubsystem.onCameraFrame(input.clone()).release();
                        }
                    }
                }
            }

            return returnMat;
        }

        @Override
        public void onViewportTapped() {
            for(VisionSubSystem visionSubsystem : visionSubSystems) {
                visionSubsystem.onViewportTapped();
            }
        }
    }
}
