package com.SCHSRobotics.HAL9001.system.robot;

import android.os.Environment;

import com.SCHSRobotics.HAL9001.system.config.ConfigData;
import com.SCHSRobotics.HAL9001.system.config.ConfigLabel;
import com.SCHSRobotics.HAL9001.system.config.ConfigParam;
import com.SCHSRobotics.HAL9001.system.config.DisableSubSystem;
import com.SCHSRobotics.HAL9001.system.config.HALConfig;
import com.SCHSRobotics.HAL9001.system.config.ProgramOptions;
import com.SCHSRobotics.HAL9001.system.config.StandAlone;
import com.SCHSRobotics.HAL9001.system.gui.HALGUI;
import com.SCHSRobotics.HAL9001.system.gui.Payload;
import com.SCHSRobotics.HAL9001.system.gui.menus.configmenu.ConfigConstants;
import com.SCHSRobotics.HAL9001.system.gui.menus.configmenu.ConfigSelectionMode;
import com.SCHSRobotics.HAL9001.system.gui.menus.configmenu.ConfigStartingMenu;
import com.SCHSRobotics.HAL9001.util.control.Button;
import com.SCHSRobotics.HAL9001.util.control.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.exceptions.NothingToSeeHereException;
import com.SCHSRobotics.HAL9001.util.misc.HALFileUtil;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.opencv.core.Size;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvInternalCamera2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

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
@SuppressWarnings({"WeakerAccess"})
public abstract class Robot {
    private static final String HAL_FILESYSTEM_ROOT = Environment.getExternalStorageDirectory().getPath() + "/System64";

    public static final String INTERNAL_CAMERA_ID = "Internal Camera", ALL_CAMERAS_ID = "All Cameras";

    private HALConfig globalConfig;

    //A hashmap mapping the name of a subsystem to the actual subsystem object.
    private final Queue<SubSystem> subSystems;
    //The opmode the robot is running.
    private OpMode opMode;
    //The GUI the robot uses to render the menus.
    public HALGUI gui;
    //A boolean value specifying whether or not to use the config system.
    private boolean useConfig = false;
    //The gamepads used to control the robot.
    public volatile Gamepad gamepad1, gamepad2;
    //The telemetry used to print lines to the driver station.
    public final Telemetry telemetry;
    //The hardwaremap used to map software representations of hardware to the actual hardware.
    public final HardwareMap hardwareMap;
    //A list of Vision-Based subsystems.
    private List<VisionSubSystem> visionSubSystems;

    private int internalCameraViewId;
    private InternalCamera internalCameraData;
    private OpenCvInternalCamera.CameraDirection internalCameraCurrentDirection;
    private Field internalCameraField;

    /**
     * Constructor for robot.
     *
     * @param opMode The opmode the robot is currently running.
     */
    public Robot(@NotNull OpMode opMode) {
        this.opMode = opMode;
        telemetry = opMode.telemetry;
        hardwareMap = opMode.hardwareMap;

        subSystems = new LinkedBlockingQueue<>();
        visionSubSystems = new ArrayList<>();

        globalConfig = HALConfig.getGlobalInstance();

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        gui = HALGUI.getInstance();

        int numCamerasUsingViewport = 0;
        List<Field> externalCameraFields = new ArrayList<>();

        Field[] fields = this.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (OpenCvCamera.class.isAssignableFrom(f.getType())) {
                if (f.isAnnotationPresent(InternalCamera.class)) {
                    ExceptionChecker.assertNull(internalCameraField, new DumpsterFireException("Must have a maximum of one defined internal camera. If you want to switch which internal camera you're using, use the appropriate method."));
                    internalCameraData = f.getAnnotation(InternalCamera.class);
                    internalCameraField = f;
                    internalCameraCurrentDirection = internalCameraData.direction();
                    numCamerasUsingViewport = internalCameraData.usesViewport() ? numCamerasUsingViewport + 1 : numCamerasUsingViewport;
                } else if (f.isAnnotationPresent(ExternalCamera.class)) {
                    ExternalCamera cameraData = f.getAnnotation(ExternalCamera.class);
                    externalCameraFields.add(f);
                    numCamerasUsingViewport = cameraData.usesViewport() ? numCamerasUsingViewport + 1 : numCamerasUsingViewport;
                }
            }
        }

        int cameraMonitorViewIdIdx = 0;
        int[] cameraMonitorViewIds;
        if (numCamerasUsingViewport > 1) {
            cameraMonitorViewIds = OpenCvCameraFactory.getInstance().splitLayoutForMultipleViewports(cameraMonitorViewId, numCamerasUsingViewport, OpenCvCameraFactory.ViewportSplitMethod.HORIZONTALLY);
        } else {
            cameraMonitorViewIds = new int[]{cameraMonitorViewId};
        }

        if (internalCameraField != null) {
            InternalCamera cameraData = internalCameraField.getAnnotation(InternalCamera.class);
            Size resolution = new Size(cameraData.resWidth(), cameraData.resHeight());
            internalCameraViewId = cameraMonitorViewIds[cameraMonitorViewIdIdx];
            CameraManager.addCamera(INTERNAL_CAMERA_ID, createCamera(internalCameraField, cameraData.usesViewport(), CameraType.INTERNAL, cameraData.direction(), INTERNAL_CAMERA_ID, internalCameraViewId), CameraType.INTERNAL, resolution);
            if (cameraData.usesViewport()) {
                cameraMonitorViewIdIdx++;
            }
        }

        for (Field f : externalCameraFields) {
            ExternalCamera cameraData = f.getAnnotation(ExternalCamera.class);
            Size resolution = new Size(cameraData.resWidth(), cameraData.resHeight());

            String id = cameraData.uniqueId().equals("") ? cameraData.configName() : cameraData.uniqueId();
            ExceptionChecker.assertFalse(id.equals(INTERNAL_CAMERA_ID) || id.equals(ALL_CAMERAS_ID), new DumpsterFireException("Id for external webcam cannot match id of internal camera or the all cameras id. Those are reserved values."));

            CameraManager.addCamera(id, createCamera(f, cameraData.usesViewport(), CameraType.EXTERNAL, null, cameraData.configName(), cameraMonitorViewIds[cameraMonitorViewIdIdx]), CameraType.EXTERNAL, resolution);
            if (cameraData.usesViewport()) {
                cameraMonitorViewIdIdx++;
            }
        }

        if (opMode.getClass().isAnnotationPresent(ProgramOptions.class)) {
            globalConfig.addOpmode(opMode);
        }
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
            useConfig |= success;

            if (success && !gui.isInitialized()) {
                startGui(Button.noButtonBoolean);
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
    public final void startGui(Button<Boolean> cycleButton) {
        if (gui.isInitialized()) {
            gui.setCycleButton(cycleButton);
        } else {
            gui.setup(this, cycleButton);
        }
    }

    /**
     * Returns whether the robot has already been set up to use the GUI.
     *
     * @return Whether the GUI has been instantiated.
     */
    @Contract(pure = true)
    public final boolean usesGUI() {
        return gui.isInitialized();
    }

    /**
     * Runs all the initialization methods of every subsystem and the GUI. Also starts the config and creates the config file tree if needed.
     */
    public final void init()
    {
        //Collects subsystems from robot class and adds them to internal subsystem list.
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

        HALFileUtil.createDirectory(HAL_FILESYSTEM_ROOT);

        if(useConfig) {
            String configMetadataFullName = '/' + ConfigConstants.CONFIG_METADATA_FILENAME + ConfigConstants.CONFIG_FILE_EXTENSION;

            //create overall robot folder
            String robotRootConfigPath = HAL_FILESYSTEM_ROOT + "/robot_" + this.getClass().getSimpleName();
            HALFileUtil.createDirectory(robotRootConfigPath);
            HALFileUtil.createFile(robotRootConfigPath + configMetadataFullName);

            //create autonomous directory in robot folder
            String autonomousFolder = robotRootConfigPath + ConfigSelectionMode.AUTONOMOUS.filepathExtension;
            HALFileUtil.createDirectory(autonomousFolder);
            HALFileUtil.createFile(autonomousFolder + configMetadataFullName);

            //create teleop directory in robot folder
            String teleopFolder = robotRootConfigPath + ConfigSelectionMode.TELEOP.filepathExtension;
            HALFileUtil.createDirectory(teleopFolder);
            HALFileUtil.createFile(teleopFolder + configMetadataFullName);

            //Get all names of subsystem objects being used in this robot and write it to the outer robot_info.txt file
            //These names will be used in the config debugger
            StringBuilder sb = new StringBuilder();
            for (SubSystem subSystem : subSystems) {
                if (subSystem.usesConfig) {
                    sb.append(subSystem.getClass().getName());
                    sb.append("\r\n");
                }
            }
            if (sb.length() > 2) {
                sb.delete(sb.length() - 2, sb.length()); //removes trailing \r\n characters so there isn't a blank line at the end of the file
            }
            HALFileUtil.save(robotRootConfigPath + configMetadataFullName, sb.toString());

            Payload payload = new Payload()
                    .add(ConfigConstants.ROBOT_FILEPATH_ID, robotRootConfigPath)
                    .add(ConfigConstants.SELECTION_MODE_ID, isAutonomous() ? ConfigSelectionMode.AUTONOMOUS : ConfigSelectionMode.TELEOP);

            if (opMode.getClass().isAnnotationPresent(StandAlone.class)) {
                payload.add(ConfigConstants.STANDALONE_MODE_ID, true);
            }
            gui.addRootMenu(new ConfigStartingMenu(payload));
        }

        for (int i = 0; i < subSystems.size(); i++) {
            SubSystem subSystem = subSystems.poll();
            subSystem.init();
            subSystems.add(subSystem);
        }

        for (VisionSubSystem visionSubSystem : visionSubSystems) {
            HALPipeline[] pipelines = visionSubSystem.getPipelines();
            for (HALPipeline pipeline : pipelines) {
                if (pipeline.getClass().isAnnotationPresent(Camera.class)) {
                    Camera linkedCameraIdAnnotation = pipeline.getClass().getAnnotation(Camera.class);
                    ExceptionChecker.assertNonNull(linkedCameraIdAnnotation, new DumpsterFireException("Linked camera id annotation for pipeline " + pipeline.getClass().getSimpleName() + " was null, this should be impossible."));
                    String linkedCameraId = linkedCameraIdAnnotation.id();
                    if (CameraManager.cameraExists(linkedCameraId)) {
                        CameraManager.addPipeline(linkedCameraId, pipeline);
                    } else if (linkedCameraId.equals(ALL_CAMERAS_ID)) {
                        CameraManager.addPipelineToAll(pipeline);
                    }
                }
            }
        }

        CameraManager.runPipelines();
    }

    /**
     * Runs methods in a loop during init. Runs all subsystem init_loop() methods and draws the configuration menu.
     */
    public final void init_loop() {
        this.gamepad1 = opMode.gamepad1;
        this.gamepad2 = opMode.gamepad2;

        gui.renderCurrentMenu();

        for (int i = 0; i < subSystems.size(); i++) {
            SubSystem subSystem = subSystems.poll();
            subSystem.init_loop();
            subSystems.add(subSystem);
        }
    }

    /**
     * Runs this method when the user presses the start button.
     */
    public final void onStart() {
        this.gamepad1 = opMode.gamepad1;
        this.gamepad2 = opMode.gamepad2;

        for (int i = 0; i < subSystems.size(); i++) {
            SubSystem subSystem = subSystems.poll();
            subSystem.start();
            subSystems.add(subSystem);
        }
    }

    /**
     * Runs subsystem handle() methods and GUI drawCurrentMenu() every frame in driver controlled programs.
     */
    public final void driverControlledUpdate() {
        this.gamepad1 = opMode.gamepad1;
        this.gamepad2 = opMode.gamepad2;

        gui.renderCurrentMenu();

        for (int i = 0; i < subSystems.size(); i++) {
            SubSystem subSystem = subSystems.poll();
            subSystem.handle();
            subSystems.add(subSystem);
        }
    }

    /**
     * Runs the stop functions for all subsystems and the GUI.
     */
    public final void stopAllComponents() {

        gui.stop();

        for (int i = 0; i < subSystems.size(); i++) {
            SubSystem subSystem = subSystems.poll();
            subSystem.stop();
            subSystems.add(subSystem);
        }

        globalConfig.clearConfig();

        CameraManager.resetManager();
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

    public ConfigData pullOpModeSettings() {
        List<ConfigParam> data = globalConfig.getConfig(opMode);
        ExceptionChecker.assertNonNull(data, new NothingToSeeHereException(HALConfig.getOpModeName(opMode.getClass()) + " settings are not part of the config."));

        Map<String, Object> dataMap = new HashMap<>();
        for (ConfigParam param : data) {
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
        return opMode.getClass().isAnnotationPresent(TeleOp.class);
    }

    /**
     * Gets if the program the robot is running is an autonomous program.
     *
     * @return Whether the program being run is an autonomous program.
     */
    @Contract(pure = true)
    public final boolean isAutonomous() {
        return opMode.getClass().isAnnotationPresent(Autonomous.class);
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

    public final void reverseInternalCameraDirection() {
        internalCameraCurrentDirection = internalCameraCurrentDirection == OpenCvInternalCamera.CameraDirection.FRONT ? OpenCvInternalCamera.CameraDirection.BACK : OpenCvInternalCamera.CameraDirection.FRONT;
        CameraManager.stopInternalCamera();
        OpenCvCamera newCamera = createCamera(internalCameraField, internalCameraData.usesViewport(), CameraType.INTERNAL, internalCameraCurrentDirection, INTERNAL_CAMERA_ID, internalCameraViewId);
        CameraManager.overrideInternalCamera(newCamera);
    }

    public final OpenCvCamera getCamera(String cameraId) {
        return CameraManager.getCamera(cameraId);
    }

    private OpenCvCamera createCamera(Field cameraField, boolean usesViewport, CameraType cameraType, OpenCvInternalCamera.CameraDirection direction, String cameraName, int cameraMonitorViewId) {
        OpenCvCamera camera;
        switch (cameraType) {
            default:
            case INTERNAL:
                if (OpenCvInternalCamera2.class.isAssignableFrom(cameraField.getType())) {
                    OpenCvInternalCamera2.CameraDirection direction2 = direction == OpenCvInternalCamera.CameraDirection.FRONT ? OpenCvInternalCamera2.CameraDirection.FRONT : OpenCvInternalCamera2.CameraDirection.BACK;
                    if (usesViewport) {
                        camera = OpenCvCameraFactory.getInstance().createInternalCamera2(direction2, cameraMonitorViewId);
                    } else {
                        camera = OpenCvCameraFactory.getInstance().createInternalCamera2(direction2);
                    }
                } else {
                    if (usesViewport) {
                        camera = OpenCvCameraFactory.getInstance().createInternalCamera(direction, cameraMonitorViewId);
                    } else {
                        camera = OpenCvCameraFactory.getInstance().createInternalCamera(direction);
                    }
                }
                break;
            case EXTERNAL:
                if (usesViewport) {
                    camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, cameraName), cameraMonitorViewId);
                } else {
                    camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, cameraName));
                }
                break;
        }

        return camera;
    }
}