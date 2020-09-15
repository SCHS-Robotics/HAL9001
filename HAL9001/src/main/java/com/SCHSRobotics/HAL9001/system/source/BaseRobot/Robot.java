package com.SCHSRobotics.HAL9001.system.source.BaseRobot;

import android.os.Environment;
import android.util.Log;

import com.SCHSRobotics.HAL9001.system.menus.ConfigMenu;
import com.SCHSRobotics.HAL9001.system.source.GUI.GUI;
import com.SCHSRobotics.HAL9001.util.annotations.ConfigLabel;
import com.SCHSRobotics.HAL9001.util.annotations.DisableSubSystem;
import com.SCHSRobotics.HAL9001.util.annotations.StandAlone;
import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.exceptions.NotBooleanInputException;
import com.SCHSRobotics.HAL9001.util.exceptions.NothingToSeeHereException;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.ConfigData;
import com.SCHSRobotics.HAL9001.util.misc.ConfigParam;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.misc.HALConfig;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * An abstract class representing the physical robot.
 *
 * @author Andrew Liang, Level Up
 * @author Dylan Zueck, Crow Force
 * @author Cole Savage, Level Up
 * @since 0.0.0
 * @version 1.0.0
 *
 * Creation Date: 2017
 */
@SuppressWarnings({"WeakerAccess","unused"})
public abstract class Robot {

    //The name of the button used to cycle between vision pipelines.
    private static final String VISION_CYCLE = "VisionCycler";
    //The resolution of each camera frame.
    private static Size cameraSize;

    private HALConfig globalConfig;

    //A hashmap mapping the name of a subsystem to the actual subsystem object.
    private final Queue<SubSystem> subSystems;
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
    //The camera used to get frames for computer vision.
    private OpenCvCamera camera;
    //The gamepad used to cycle between vision pipelines.
    private final CustomizableGamepad visionCycler;
    //Boolean values determining different camera states and parameters.
    private boolean useViewport, pipelineSet, cameraStarted;
    //The cameraMonitorViewId for displaying frames on the phone.
    private int cameraMonitorViewId;
    //A list of Vision-Based subsystems.
    private List<VisionSubSystem> visionSubSystems;

    /**
     * Constructor for robot.
     *
     * @param opMode The opmode the robot is currently running.
     */
    public Robot(@NotNull OpMode opMode)
    {
        this.opMode = opMode;
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        subSystems = new PriorityQueue<>();
        visionSubSystems = new ArrayList<>();

        useGui = false;
        useConfig = false;
        useViewport = false;
        pipelineSet = false;
        cameraStarted = false;

        visionCycler = new CustomizableGamepad(this);

        cameraSize = new Size(320, 240);
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId","id", hardwareMap.appContext.getPackageName());

        globalConfig = HALConfig.getGlobalInstance();
    }

    /**
     * Adds a subsystem to the robot's hashmap of subsystems and, if the subsystem uses config, load the default config.
     *
     * @param subSystem The subsystem object.
     */
    private void addSubSystem(String name, SubSystem subSystem) {
        subSystems.add(subSystem);

        if(subSystem instanceof VisionSubSystem) {
            visionSubSystems.add((VisionSubSystem) subSystem);
        }

        if(subSystem.usesConfig) {

            boolean success = globalConfig.addSubSystem(name, subSystem);
            //useConfig |= success;

            if (success && !useGui) {
                //GUI.setup(this, new Button<Boolean>(1, Button.BooleanInputs.noButton));
                //gui = GUI.getInstance();
                //useGui = true;
            }
        }
    }

    /**
     * Adds a subsystem to the robot's hashmap of subsystems and, if the subsystem uses config, load the default config.
     *
     * @param name The name of the subsystem.
     * @param subSystem The subsystem object.
     * @deprecated Renamed to addSubSystem
     */
    @Deprecated
    protected final void putSubSystem(String name, SubSystem subSystem) {
        addSubSystem(name, subSystem);
    }

    /**
     * Instantiates the GUI and allows the robot to use a GUI.
     *
     * @param cycleButton The button used to cycle through multiple menus in GUI.
     */
    protected final void startGui(Button<Boolean> cycleButton) {
        if(!useGui) {
            GUI.setup(this, cycleButton);
            gui = GUI.getInstance();
            useGui = true;
        }
        else {
            gui.overrideCycleButton(cycleButton);
        }
    }

    /**
     * Enables the viewport for displaying frames for computer vision.
     *
     * @param cycleButton The button used to cycle between vision pipelines.
     *
     * @throws NotBooleanInputException Throws this exception if the button is not a boolean input.
     */
    protected final void enableViewport(@NotNull Button<Boolean> cycleButton) {
        if(!useViewport) {
            visionCycler.addButton(VISION_CYCLE, cycleButton);
            useViewport = true;
        }
        else {
            visionCycler.removeButton(VISION_CYCLE);
            visionCycler.addButton(VISION_CYCLE, cycleButton);
        }
    }

    /**
     * Returns whether the viewport is enabled.
     *
     * @return Whether the viewport is enabled.
     */
    @Contract(pure = true)
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public final boolean isViewportEnabled() {
        return useViewport;
    }

    /**
     * Returns whether the robot has already been set up to use the GUI.
     *
     * @return Whether the GUI has been instantiated.
     */
    @Contract(pure = true)
    public final boolean usesGUI() {
        return useGui;
    }

    /**
     * Runs all the initialization methods of every subsystem and the GUI. Also starts the config and creates the config file tree if needed.
     */
    public final void init()
    {
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field f : fields) {
            if(SubSystem.class.isAssignableFrom(f.getType()) && !f.isAnnotationPresent(DisableSubSystem.class)) {
                Object obj;
                try {
                    obj = f.get(this);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new DumpsterFireException("Tried to access your subsystem, but you made it protected or private. SHARE!!!");
                }
                if(obj != null) {
                    SubSystem subSystem = (SubSystem) obj;
                    String name = subSystem.getClass().getSimpleName();
                    if(f.isAnnotationPresent(ConfigLabel.class)) {
                        ConfigLabel configLabelAnnotation = f.getAnnotation(ConfigLabel.class);
                        name = configLabelAnnotation.label();
                    }
                    addSubSystem(name, subSystem);
                }
            }
        }

        HALConfig.setGlobalConfigAsDefault();

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
            for(SubSystem subSystem : subSystems) {
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
                if(isAutonomous()) {
                    gui.addMenu("config",new ConfigMenu(gui,autoDir.getPath(),true));
                }
                else if(isTeleop()) {
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

        for (int i = 0; i < subSystems.size(); i++) {
            SubSystem subSystem = subSystems.poll();
            subSystem.init();
            subSystems.add(subSystem);
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

        for (int i = 0; i < subSystems.size(); i++) {
            SubSystem subSystem = subSystems.poll();
            subSystem.init_loop();
            subSystems.add(subSystem);
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

    /**
     * Runs this method when the user presses the start button.
     */
    public final void onStart() {
        this.gamepad1 = opMode.gamepad1;
        this.gamepad2 = opMode.gamepad2;

        if (useGui) {
            if (gui.isMenuPresent("config")) {
                gui.removeMenu("config");
            }
            gui.onStart();
        }

        for (int i = 0; i < subSystems.size(); i++) {
            SubSystem subSystem = subSystems.poll();
            subSystem.start();
            subSystems.add(subSystem);
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

    /**
     * Runs subsystem handle() methods and GUI drawCurrentMenu() every frame in driver controlled programs.
     */
    public final void driverControlledUpdate() {
        this.gamepad1 = opMode.gamepad1;
        this.gamepad2 = opMode.gamepad2;

        if (useGui) {
            gui.drawCurrentMenu();
        }

        for (int i = 0; i < subSystems.size(); i++) {
            SubSystem subSystem = subSystems.poll();
            subSystem.handle();
            subSystems.add(subSystem);
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

    /**
     * Runs the stop functions for all subsystems and the GUI.
     */
    public final void stopAllComponents(){
        if (useGui) {
            gui.stop();
        }

        for (int i = 0; i < subSystems.size(); i++) {
            SubSystem subSystem = subSystems.poll();
            subSystem.stop();
            subSystems.add(subSystem);
        }

        if (cameraStarted) {
            camera.stopStreaming();
            camera.closeCameraDevice();
        }

        globalConfig.clearConfig();
    }

    /**
     * Gets a specific subsystem from the hashmap.
     *
     * @param name The name of the subsystem.
     * @return The subsystem with the given identifier in the hashmap.
     */
    /*public final SubSystem getSubSystem(String name)
    {
        return subSystems.g(name);
    }*/

    /**
     * Replaces a subsystem already in the hashmap with another subsystem.
     *
   //  * @param name The name of the subsystem to be replaced.
    // * @param subSystem The new subsystem.
     */
    /*public final void overrideSubSystem(String name, SubSystem subSystem)
    {
        subSystems.put(name, subSystem);
    }*/

    @Contract(pure = true)
    public final boolean getUsingConfig() {
        return useConfig;
    }

    /**
     * Gets the opmode the robot is currently running.
     *
     * @return The opmode the robot is running.
     */
    @Contract(pure = true)
    public final OpMode getOpMode() {
        return opMode;
    }

    /**
     * Pulls a customizable gamepad object from the teleop config map. Allows for easily getting gamepad data from the configuration.
     *
     * @param subsystem The name of the subsystem to pull the gamepad controls for.
     * @return A customizable gamepad containing the configured controls for that subsystem.
     */
    public final CustomizableGamepad pullControls(SubSystem subsystem) {
        List<ConfigParam> configParams = globalConfig.getConfig(HALConfig.Mode.TELEOP, subsystem);
        ExceptionChecker.assertNonNull(configParams, new NullPointerException(subsystem+" is not present in teleopConfig"));
        CustomizableGamepad gamepad = new CustomizableGamepad(this);
        for (ConfigParam param : configParams) {
            if (param.usesGamepad) {
                gamepad.addButton(param.name, param.toButton());
            }
        }
        return gamepad;
    }

    /**
     * Pulls the data of non-gamepad-related config settings from the global config. The map format is (option name) -> (option value)
     *
     * @param subsystem The name of the subsystem to get config from.
     * @return The non-gamepad configuration data for that subsystem.
     */
    @NotNull
    @Contract("_ -> new")
    public final ConfigData pullNonGamepad(SubSystem subsystem) {
        List<ConfigParam> configParamsTeleop = globalConfig.getConfig(HALConfig.Mode.TELEOP, subsystem);
        List<ConfigParam> configParamsAuto = globalConfig.getConfig(HALConfig.Mode.AUTONOMOUS, subsystem);

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

    public ConfigData pullProgramSettings() {
        List<ConfigParam> data = globalConfig.getConfig(opMode);
        ExceptionChecker.assertNonNull(data, new NothingToSeeHereException(HALConfig.getOpModeName(opMode.getClass())+" settings are not part of the config."));

        Map<String, Object> dataMap = new HashMap<>();
        for(ConfigParam param : data) {
            dataMap.put(param.name, param.vals.get(param.options.indexOf(param.currentOption)));
        }
        return new ConfigData(dataMap);
    }

    /**
     * Gets if the program the robot is running is a teleop program.
     *
     * @return Whether the program being run is a teleop program.
     */
    @Contract(pure = true)
    public final boolean isTeleop() {
        return opMode instanceof BaseTeleop;
    }

    /**
     * Gets if the program the robot is running is an autonomous program.
     *
     * @return Whether the program being run is an autonomous program.
     */
    @Contract(pure = true)
    public final boolean isAutonomous() {
        return opMode instanceof BaseAutonomous;
    }

    /**
     * Gets whether the robot's current program is running.
     *
     * @return Whether the robot's current program is running.
     */
    public final boolean opModeIsActive() {
        ExceptionChecker.assertTrue(isTeleop() || isAutonomous(), new DumpsterFireException("Program is not an instance of BaseAutonomous or BaseTeleop, cannot tell if its running. A lot of other things are probably broken too if you're seeing this."));
        return ((LinearOpMode) opMode).opModeIsActive();
    }

    /**
     * Gets whether the robot's current program has requested to be stopped.
     *
     * @return Whether the robot's current program has requested to be stopped.
     */
    public final boolean isStopRequested() {
        ExceptionChecker.assertTrue(isTeleop() || isAutonomous(), new DumpsterFireException("Program is not an instance of BaseAutonomous or BaseTeleop, cannot tell if its running. A lot of other things are probably broken too if you're seeing this."));
        return ((LinearOpMode) opMode).isStopRequested();
    }

    /**
     * Gets whether the robot's current program has been started.
     *
     * @return Whether the robot's current program has been started.
     */
    public final boolean isStarted() {
        ExceptionChecker.assertTrue(isTeleop() || isAutonomous(), new DumpsterFireException("Program is not an instance of BaseAutonomous or BaseTeleop, cannot tell if its running. A lot of other things are probably broken too if you're seeing this."));
        return ((LinearOpMode) opMode).isStarted();
    }

    /**
     * Writes data to a specified filepath. Creates the file if it doesn't exist, overwrites it if it does.
     *
     * @param filePath The path of the file to write to.
     * @param data The data to write to the file
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

    /**
     * Returns whether any vision subsystem has requested to enable their pipelines.
     *
     * @return Whether any vision subsystem has requested to enable their pipelines.
     */
    @Contract(pure = true)
    private boolean enableVisionRequested() {
        boolean anythingEnabled = false;
        for(VisionSubSystem visionSubsystem : visionSubSystems) {
            anythingEnabled |= visionSubsystem.isEnabled();
        }
        return anythingEnabled;
    }

    /**
     * Sets the camera resolution. Only valid for specific values.
     *
     * @param width The frame width in pixels.
     * @param height The frame height in pixels.
     */
    protected void setCameraSize(int width, int height) {
        cameraSize = new Size(width, height);
    }

    /**
     * A global vision pipeline that runs all the vision subsystem pipelines
     * @TODO Make different cameras for pipelines that request it instead of all running them one after the other.
     */
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