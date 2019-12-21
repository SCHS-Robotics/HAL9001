package com.SCHSRobotics.HAL9001.system.source.BaseRobot;

import android.os.Environment;
import android.util.Log;

import com.SCHSRobotics.HAL9001.system.menus.ConfigMenu;
import com.SCHSRobotics.HAL9001.system.source.GUI.GUI;
import com.SCHSRobotics.HAL9001.util.annotations.AutonomousConfig;
import com.SCHSRobotics.HAL9001.util.annotations.DisableSubSystem;
import com.SCHSRobotics.HAL9001.util.annotations.LinkTo;
import com.SCHSRobotics.HAL9001.util.annotations.ProgramOptions;
import com.SCHSRobotics.HAL9001.util.annotations.StandAlone;
import com.SCHSRobotics.HAL9001.util.annotations.TeleopConfig;
import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;
import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.exceptions.InvalidLinkException;
import com.SCHSRobotics.HAL9001.util.exceptions.NotAnAlchemistException;
import com.SCHSRobotics.HAL9001.util.exceptions.NotBooleanInputException;
import com.SCHSRobotics.HAL9001.util.exceptions.NothingToSeeHereException;
import com.SCHSRobotics.HAL9001.util.math.ArrayMath;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.ConfigData;
import com.SCHSRobotics.HAL9001.util.misc.ConfigParam;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.opmode.RegisteredOpModes;
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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
public abstract class Robot {

    //The name of the button used to cycle between vision pipelines.
    private static final String VISION_CYCLE = "VisionCycler";
    //The resolution of each camera frame.
    private static Size cameraSize;
    //A map relating the name of each subsystem in the robot to that subsystem's corresponding autonomous config
    public static Map<String, List<ConfigParam>> autonomousConfig = new LinkedHashMap<>();
    //A map relating the name of each subsystem in the robot to that subsystem's corresponding teleop config
    public static Map<String, List<ConfigParam>> teleopConfig = new LinkedHashMap<>();
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
    //Whether or not a program has thrown an error.
    private boolean errorThrown;
    //The exception that was thrown (if an exception was thrown).
    private Throwable thrownException;
    //The name of the opmode as put in the class annotation.
    private String opmodeName;

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

        thrownException = new Throwable();
        errorThrown = false;

        if (opMode.getClass().isAnnotationPresent(TeleOp.class)) {
            opmodeName = opMode.getClass().getAnnotation(TeleOp.class).name();
        } else if (opMode.getClass().isAnnotationPresent(Autonomous.class)) {
            opmodeName = opMode.getClass().getAnnotation(Autonomous.class).name();
        }
        else {
            throw new DumpsterFireException("You are running this program without @Teleop or @Autonomous ... how did this even happen???");
        }

        addSettings(opMode);
    }

    private void addSettings(OpMode opMode) {
        addSettings(opMode,new ArrayList<String>());
    }

    private void addSettings(@NotNull OpMode opMode, List<String> visitedOpmodes) {

        ExceptionChecker.assertTrue(opMode.getClass().isAnnotationPresent(TeleOp.class) || opMode.getClass().isAnnotationPresent(Autonomous.class), new NothingToSeeHereException("You forgot to add @" + (isTeleop() ? "Teleop" : isAutonomous() ? "Autonomous" : "... oh wait this isn't a HAL opmode... how did this even happen???")));
        ExceptionChecker.assertTrue(opMode instanceof BaseAutonomous || opMode instanceof BaseTeleop, new NotAnAlchemistException("Hey, one of your programs or linked programs is not a HAL program"));

        String name;
        if (opMode.getClass().isAnnotationPresent(TeleOp.class)) {
            name = opMode.getClass().getAnnotation(TeleOp.class).name();
        } else {
            name = opMode.getClass().getAnnotation(Autonomous.class).name();
        }

        if(opMode.getClass().isAnnotationPresent(LinkTo.class) && !visitedOpmodes.contains(name)) {
            LinkTo link = opMode.getClass().getAnnotation(LinkTo.class);
            visitedOpmodes.add(name);
            OpMode linkedOpmode = RegisteredOpModes.getInstance().getOpMode(link.destination());
            ExceptionChecker.assertNonNull(linkedOpmode, new InvalidLinkException("Link to nonexistent opmode. '"+link.destination()+"' does not exist"));
            addSettings(linkedOpmode, visitedOpmodes);
        }
        if(opMode.getClass().isAnnotationPresent(ProgramOptions.class)) {
            ProgramOptions settings = opMode.getClass().getAnnotation(ProgramOptions.class);
            String[] nonDuplicatedSettings = ArrayMath.removeDuplicates(settings.options());

            if (nonDuplicatedSettings.length > 0) {
                if (opMode instanceof BaseTeleop) {
                    teleopConfig.put(name, Arrays.asList((ConfigParam[]) new ConfigParam[]{new ConfigParam("Options", nonDuplicatedSettings, nonDuplicatedSettings[0])}));
                } else {
                    autonomousConfig.put(name, Arrays.asList((ConfigParam[]) new ConfigParam[]{new ConfigParam("Options", nonDuplicatedSettings, nonDuplicatedSettings[0])}));
                }
            }
        }

    }

    /**
     * Adds a subsystem to the robot's hashmap of subsystems and, if the subsystem uses config, load the default config.
     *
     * @param subSystem The subsystem object.
     */
    protected final void addSubSystem(SubSystem subSystem) {
        subSystems.put(subSystem.getClass().getSimpleName(), subSystem);

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
            catch (Throwable e) {
                Log.e("Error","Problem loading config for subsystem "+subSystem.getClass().getSimpleName(),e);
            }
        }
    }

    /**
     * Adds a subsystem to the robot's hashmap of subsystems and, if the subsystem uses config, load the default config.
     *
     * @param name The name of the subsystem.
     * @param subSystem The subsystem object.
     * @deprecated Renamed to add SubSystem
     */
    @Deprecated
    protected final void putSubSystem(String name, SubSystem subSystem)
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
/*
                Field[] fields = subSystem.getClass().getDeclaredFields();
                for(Field field : fields) {
                    if(field.isAnnotationPresent(ConfigurableButton.class)) {
                        ConfigurableButton button = field.getAnnotation(ConfigurableButton.class);
                        ConfigButtonType buttonType = button.default_value();

                        if(button.program_type() == ConfigProgramType.TELEOP) {
                            teleopConfig.put(subSystem.getClass().getSimpleName(),Arrays.asList((ConfigParam[]) new ConfigParam[] {new ConfigParam(button.name(),)}));
                        }
                        else {

                        }
                    }
                    Log.println(Log.ASSERT,"name",field.getName());
                }
                */
            }
            catch (Throwable e) {
                Log.e("Error","Problem loading config for subsystem "+name,e);
            }
        }
    }

    /**
     * Instantiates the GUI and allows the robot to use a GUI.
     *
     * @param cycleButton The button used to cycle through multiple menus in GUI.
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

    /**
     * Enables the viewport for displaying frames for computer vision.
     *
     * @param cycleButton The button used to cycle between vision pipelines.
     *
     * @throws NotBooleanInputException Throws this exception if the button is not a boolean input.
     */
    protected final void enableViewport(@NotNull Button cycleButton) {
        ExceptionChecker.assertTrue(cycleButton.isBoolean,new NotBooleanInputException("Vision cycle button must be a boolean input"));

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
    public final boolean isViewportEnabled() {
        return useViewport;
    }

    /**
     * Returns whether the robot has already been set up to use the GUI.
     *
     * @return Whether the GUI has been instantiated.
     */
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
            if(SubSystem.class.isAssignableFrom(f.getType())) {
                if(!f.isAnnotationPresent(DisableSubSystem.class)) {
                    Object obj;
                    try {
                        obj = f.get(this);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        throw new DumpsterFireException("Tried to access your subsystem, but you made it protected or private. SHARE!!!");
                    }
                    if(obj != null) {
                        addSubSystem((SubSystem) obj);
                    }
                }
            }
        }

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

        for (SubSystem subSystem : subSystems.values()){
            try
            {
                subSystem.init();
            }
            catch (Throwable ex)
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
                } catch (Throwable ex) {
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
                } catch (Throwable ex) {
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
                } catch (Throwable ex) {
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
                } catch (Throwable ex) {
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

            for(SubSystem subSystem : subSystems.values()) {
                teleopConfig.remove(subSystem.getClass().getSimpleName());
                autonomousConfig.remove(subSystem.getClass().getSimpleName());
            }
            teleopConfig.remove(opmodeName);
            autonomousConfig.remove(opmodeName);
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
     * @param name The name of the subsystem.
     * @return The subsystem with the given identifier in the hashmap.
     */
    public final SubSystem getSubSystem(String name)
    {
        return subSystems.get(name);
    }

    /**
     * Replaces a subsystem already in the hashmap with another subsystem.
     *
     * @param name The name of the subsystem to be replaced.
     * @param subSystem The new subsystem.
     * @return The new subsystem that was passed in as a parameter.
     */
    public final SubSystem eOverrideSubSystem(String name, SubSystem subSystem)
    {
        subSystems.put(name, subSystem);
        return subSystem;
    }

    public final boolean getUsingConfig() {
        return useConfig;
    }

    /**
     * Gets the opmode the robot is currently running.
     *
     * @return The opmode the robot is running.
     */
    public final OpMode getOpMode() {
        return opMode;
    }

    /**
     * Pulls a customizable gamepad object from the teleop config map. Allows for easily getting gamepad data from the configuration.
     *
     * @param subsystem The subsystem to pull the gamepad controls for.
     * @return A customizable gamepad containing the configured controls for that subsystem.
     */
    public final CustomizableGamepad pullControls(SubSystem subsystem) {
        return pullControls(subsystem.getClass().getSimpleName());
    }

    /**
     * Pulls a customizable gamepad object from the teleop config map. Allows for easily getting gamepad data from the configuration.
     *
     * @param subsystem The name of the subsystem to pull the gamepad controls for.
     * @return A customizable gamepad containing the configured controls for that subsystem.
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

    /**
     * Pulls the data of non-gamepad-related config settings from the global config. The map format is (option name) -> (option value)
     *
     * @param subsystem The subsystem to get config from.
     * @return The non-gamepad configuration data for that subsystem.
     */
    public final ConfigData pullNonGamepad(SubSystem subsystem) {
        return pullNonGamepad(subsystem.getClass().getSimpleName());
    }

    /**
     * Pulls the data of non-gamepad-related config settings from the global config. The map format is (option name) -> (option value)
     *
     * @param subsystem The name of the subsystem to get config from.
     * @return The non-gamepad configuration data for that subsystem.
     */
    public final ConfigData pullNonGamepad(String subsystem) {
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

    public String pullProgramSetting() {
        ExceptionChecker.assertTrue(teleopConfig.containsKey(opmodeName) || autonomousConfig.containsKey(opmodeName), new NothingToSeeHereException("You are not using @ProgramOptions, but are trying pull program settings"));
        if(isTeleop()) {
            return teleopConfig.get(opmodeName).get(0).currentOption;
        }
        else {
            return autonomousConfig.get(opmodeName).get(0).currentOption;
        }
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
