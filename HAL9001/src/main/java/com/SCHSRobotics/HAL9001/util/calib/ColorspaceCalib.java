package com.SCHSRobotics.HAL9001.util.calib;

import com.SCHSRobotics.HAL9001.system.menus.DisplayMenu;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.VisionSubSystem;
import com.SCHSRobotics.HAL9001.util.annotations.TeleopConfig;
import com.SCHSRobotics.HAL9001.util.exceptions.ChannelDoesNotExistException;
import com.SCHSRobotics.HAL9001.util.exceptions.GuiNotPresentException;
import com.SCHSRobotics.HAL9001.util.exceptions.NotBooleanInputException;
import com.SCHSRobotics.HAL9001.util.exceptions.ViewportDisabledException;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.ConfigData;
import com.SCHSRobotics.HAL9001.util.misc.ConfigParam;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;
import com.SCHSRobotics.HAL9001.util.misc.Toggle;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * A calibration subsystem used to find good colorspace ranges for color detection algorithms.
 *
 * @author Dylan Zueck, Crow Force
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 7/18/19
 */
@SuppressWarnings("unused")
public class ColorspaceCalib extends VisionSubSystem {

    //The upper and lower limits for the x, y, z values for the color spaces.
    private int x_lower, y_lower, z_lower, x_upper, y_upper, z_upper;
    //Controls the change in x, y, z values.
    private int increment;
    //The collection of user selected inputs.
    private CustomizableGamepad inputs;
    //Toggle class that toggles slow mode and upper limit on and off.
    private Toggle slowModeToggle, upperLimit;
    //Key names used with the CustomizableGamepad.
    private static final String SLOWMODE = "slowMode", X_INCREMENT = "XUp", X_DECREMENT = "XDown", Y_INCREMENT = "YUp", Y_DECREMENT = "YDown", Z_INCREMENT = "ZUp", Z_DECREMENT = "ZDown", CHANGELIMIT = "ChangeLimit";
    //A user-specified function that converts an RGB image to a custom colorspace.
    private Function<Mat, Mat> converter;
    //Selected colorspace that will be used.
    private ColorSpace colorSpace;
    //Used to only allow the increment to change the x,y,z values every deleyMs milliseconds.
    private long lastActivatedTimestamp;
    //ChannelIdx is the index of the channel in the colorspace that will be filtered in single channel mode. DelayMs is time between changes to x, y, z values in milliseconds.
    private int channelIdx, delayMs;
    //The mode of image filtering. Either single chanel images or 3 channel color images.
    private ImageType imageType;
    //The menu used to display the colorspace bounds to the screen.
    private DisplayMenu displayMenu;

    /**
     * List of already supported 3 chanel color spaces.
     */
    public enum ColorSpace {
        HSV, RGB, Lab, YUV, BGR, HLS, HSV_FULL, HLS_FULL, LUV, XYZ, YCrCb, CUSTOM
    }

    /**
     * The type of image filtering mechanism to use for the color space (single channel or 3 channel color).
     */
    public enum ImageType {
        SINGLE_CHANNEL, COLOR
    }

    /**
     * Constructor that uses configuration to set everything.
     *
     * @param robot The robot running the program.
     */
    public ColorspaceCalib(@NotNull Robot robot) {
        super(robot);

        if(!robot.usesGUI()){
            throw new GuiNotPresentException("ColorspaceCalib requires a GUI to correctly run");
        }

        if(!robot.isViewportEnabled()) {
            throw new ViewportDisabledException("The camera viewport must be enabled for this program to run");
        }

        displayMenu = new DisplayMenu(robot.gui);
        robot.gui.addMenu("Colorspace Ranges",displayMenu);

        this.imageType = ImageType.COLOR;
        usesConfig = true;
    }

    /**
     * Constructor that uses default keys.
     *
     * @param robot The robot running the program.
     * @param colorSpace Enum that determine what 3 chanel color space to use.
     */
    public ColorspaceCalib(@NotNull Robot robot, @NotNull ColorSpace colorSpace){
        super(robot);

        if(!robot.usesGUI()){
            throw new GuiNotPresentException("ColorspaceCalib requires a GUI to correctly run");
        }

        if(!robot.isViewportEnabled()) {
            throw new ViewportDisabledException("The camera viewport must be enabled for this program to run");
        }

        displayMenu = new DisplayMenu(robot.gui);
        robot.gui.addMenu("Colorspace Ranges",displayMenu);

        //default keys are all on gamepad1 and are dpad_up for x_increment, dpad_down for x decrement, left_stick_up for y_increment, left_stick_down for y decrement, right_stick_up for z_increment, right_stick_down for z decrement, a for slowMode, and b for changeLimit.
        setInputs(new Button(1, Button.BooleanInputs.dpad_up), new Button(1, Button.BooleanInputs.dpad_down), new Button(1, Button.BooleanInputs.bool_left_stick_y_up), new Button(1, Button.BooleanInputs.bool_left_stick_y_down), new Button(1, Button.BooleanInputs.bool_right_stick_y_up), new Button(1, Button.BooleanInputs.bool_right_stick_y_down), new Button(1, Button.BooleanInputs.a), new Button(1, Button.BooleanInputs.x));

        this.colorSpace = colorSpace;
        this.imageType = ImageType.COLOR;
    }

    /**
     * Constructor that uses default keys and allows the user to specify the type of image being filtered.
     *
     * @param robot The robot running the program.
     * @param colorSpace Enum that determine what 3 chanel color space to use.
     * @param imageType An enum that specifies if a single channel or multi-channel image is being filtered.
     * @param channelIdx The index of the filtered channel within the colorspace.
     */
    public ColorspaceCalib(@NotNull Robot robot, @NotNull ColorSpace colorSpace, @NotNull ImageType imageType, int channelIdx){
        super(robot);

        if(!robot.usesGUI()){
            throw new GuiNotPresentException("ColorspaceCalib requires a GUI to correctly run");
        }

        if(!robot.isViewportEnabled()) {
            throw new ViewportDisabledException("The camera viewport must be enabled for this program to run");
        }

        displayMenu = new DisplayMenu(robot.gui);
        robot.gui.addMenu("Colorspace Ranges",displayMenu);

        //default keys are all on gamepad1 and are dpad_up for x_increment, dpad_down for x decrement, left_stick_up for y_increment, left_stick_down for y decrement, right_stick_up for z_increment, right_stick_down for z decrement, a for slowMode, and b for changeLimit.
        setInputs(new Button(1, Button.BooleanInputs.dpad_up), new Button(1, Button.BooleanInputs.dpad_down), new Button(1, Button.BooleanInputs.bool_left_stick_y_up), new Button(1, Button.BooleanInputs.bool_left_stick_y_down), new Button(1, Button.BooleanInputs.bool_right_stick_y_up), new Button(1, Button.BooleanInputs.bool_right_stick_y_down), new Button(1, Button.BooleanInputs.a), new Button(1, Button.BooleanInputs.x));

        this.colorSpace = colorSpace;
        this.imageType = imageType;
        this.channelIdx = channelIdx;
    }

    /**
     * Constructor that uses default keys and lets the user specify a custom function to convert from the RGB color space to an arbitrary custom colorspace.
     *
     * @param robot The robot running the program.
     * @param converter The custom conversion function.
     */
    public ColorspaceCalib(@NotNull Robot robot, @NotNull Function<Mat, Mat> converter){
        super(robot);

        if(!robot.usesGUI()){
            throw new GuiNotPresentException("ColorspaceCalib requires a GUI to correctly run");
        }

        if(!robot.isViewportEnabled()) {
            throw new ViewportDisabledException("The camera viewport must be enabled for this program to run");
        }

        displayMenu = new DisplayMenu(robot.gui);
        robot.gui.addMenu("Colorspace Ranges",displayMenu);

        setInputs(new Button(1, Button.BooleanInputs.dpad_up), new Button(1, Button.BooleanInputs.dpad_down), new Button(1, Button.BooleanInputs.bool_left_stick_y_up), new Button(1, Button.BooleanInputs.bool_left_stick_y_down), new Button(1, Button.BooleanInputs.bool_right_stick_y_up), new Button(1, Button.BooleanInputs.bool_right_stick_y_down), new Button(1, Button.BooleanInputs.a), new Button(1, Button.BooleanInputs.x));

        this.colorSpace = ColorSpace.CUSTOM;
        this.imageType = ImageType.COLOR;
        this.converter = converter;
    }

    /**
     * Constructor that uses default keys, lets the user input a custom color conversion function (RGB to custom), and allows the user to specify if a single channel or multi-channel image is being filtered.
     *
     * @param robot The robot running the program.
     * @param converter The custom conversion function.
     * @param imageType An enum that specifies if a single channel or multi-channel image is being filtered.
     * @param channelIdx The index of the filtered channel within the colorspace.
     */
    public ColorspaceCalib(@NotNull Robot robot, @NotNull Function<Mat, Mat> converter, @NotNull ImageType imageType, int channelIdx){
        super(robot);

        if(!robot.usesGUI()){
            throw new GuiNotPresentException("ColorspaceCalib requires a GUI to correctly run");
        }

        if(!robot.isViewportEnabled()) {
            throw new ViewportDisabledException("The camera viewport must be enabled for this program to run");
        }

        displayMenu = new DisplayMenu(robot.gui);
        robot.gui.addMenu("Colorspace Ranges",displayMenu);

        setInputs(new Button(1, Button.BooleanInputs.dpad_up), new Button(1, Button.BooleanInputs.dpad_down), new Button(1, Button.BooleanInputs.bool_left_stick_y_up), new Button(1, Button.BooleanInputs.bool_left_stick_y_down), new Button(1, Button.BooleanInputs.bool_right_stick_y_up), new Button(1, Button.BooleanInputs.bool_right_stick_y_down), new Button(1, Button.BooleanInputs.a), new Button(1, Button.BooleanInputs.x));

        this.colorSpace = ColorSpace.CUSTOM;
        this.imageType = imageType;
        this.converter = converter;
        this.channelIdx = channelIdx;
    }

    /**
     * Constructor that lets you set the keys.
     *
     * @param robot The robot running the program.
     * @param XUp Button to increment X values.
     * @param XDown Button to decrement X values.
     * @param YUp Button to increment Y values.
     * @param YDown Button to decrement Y values.
     * @param ZUp Button to increment Z values.
     * @param ZDown Button to decrement Z values.
     * @param slowMode Toggle button for slow mode.
     * @param changeLimit Toggle button for changing between changing upper values and lower values.
     * @param colorSpace Enum that determines what 3 chanel color space to use.
     */
    public ColorspaceCalib(@NotNull Robot robot, @NotNull Button XUp, @NotNull Button XDown, @NotNull Button YUp, @NotNull Button YDown, @NotNull Button ZUp, @NotNull Button ZDown, @NotNull Button slowMode, @NotNull Button changeLimit, @NotNull ColorSpace colorSpace){
        super(robot);

        if(!robot.usesGUI()){
            throw new GuiNotPresentException("ColorspaceCalib requires a GUI to correctly run");
        }

        if(!robot.isViewportEnabled()) {
            throw new ViewportDisabledException("The camera viewport must be enabled for this program to run");
        }

        displayMenu = new DisplayMenu(robot.gui);
        robot.gui.addMenu("Colorspace Ranges",displayMenu);

        setInputs(XUp, XDown, YUp, YDown, ZUp, ZDown, slowMode, changeLimit);

        this.colorSpace = colorSpace;
        this.imageType = ImageType.COLOR;
    }

    /**
     * Constructor that lets you set the keys and allows the user to specify if a single channel or multi-channel image is being filtered.
     *
     * @param robot The robot running the program.
     * @param XUp Button to increment X values.
     * @param XDown Button to decrement X values.
     * @param YUp Button to increment Y values.
     * @param YDown Button to decrement Y values.
     * @param ZUp Button to increment Z values.
     * @param ZDown Button to decrement Z values.
     * @param slowMode Toggle button for slow mode.
     * @param changeLimit Toggle button for changing between changing upper values and lower values.
     * @param colorSpace Enum that determine what 3 chanel color space to use.
     * @param imageType An enum that specifies if a single channel or multi-channel image is being filtered.
     * @param channelIdx The index of the filtered channel within the colorspace.
     */
    public ColorspaceCalib(@NotNull Robot robot, @NotNull Button XUp, @NotNull Button XDown, @NotNull Button YUp, @NotNull Button YDown, @NotNull Button ZUp, @NotNull Button ZDown, @NotNull Button slowMode, @NotNull Button changeLimit, @NotNull ColorSpace colorSpace, @NotNull ImageType imageType, int channelIdx){
        super(robot);

        if(!robot.usesGUI()){
            throw new GuiNotPresentException("ColorspaceCalib requires a GUI to correctly run");
        }

        if(!robot.isViewportEnabled()) {
            throw new ViewportDisabledException("The camera viewport must be enabled for this program to run");
        }

        displayMenu = new DisplayMenu(robot.gui);
        robot.gui.addMenu("Colorspace Ranges",displayMenu);

        setInputs(XUp, XDown, YUp, YDown, ZUp, ZDown, slowMode, changeLimit);

        this.colorSpace = colorSpace;
        this.imageType = imageType;
        this.channelIdx = channelIdx;
    }

    /**
     * Constructor that lets the user set the keys and lets the user input a custom color conversion function (RGB to custom).
     *
     * @param robot The robot running the program.
     * @param XUp Button to increment X values.
     * @param XDown Button to decrement X values.
     * @param YUp Button to increment Y values.
     * @param YDown Button to decrement Y values.
     * @param ZUp Button to increment Z values.
     * @param ZDown Button to decrement Z values.
     * @param slowMode Toggle button for slow mode.
     * @param changeLimit Toggle button for changing between changing upper values and lower values.
     * @param converter The custom conversion function.
     */
    public ColorspaceCalib(@NotNull Robot robot, @NotNull Button XUp, @NotNull Button XDown, @NotNull Button YUp, @NotNull Button YDown, @NotNull Button ZUp, @NotNull Button ZDown, @NotNull Button slowMode, @NotNull Button changeLimit, @NotNull Function<Mat, Mat> converter) {
        super(robot);

        if(!robot.usesGUI()){
            throw new GuiNotPresentException("ColorspaceCalib requires a GUI to correctly run");
        }

        if(!robot.isViewportEnabled()) {
            throw new ViewportDisabledException("The camera viewport must be enabled for this program to run");
        }

        displayMenu = new DisplayMenu(robot.gui);
        robot.gui.addMenu("Colorspace Ranges",displayMenu);

        setInputs(XUp, XDown, YUp, YDown, ZUp, ZDown, slowMode, changeLimit);

        this.colorSpace = ColorSpace.CUSTOM;
        this.imageType = ImageType.COLOR;
        this.converter = converter;
    }

    /**
     * Constructor that lets the user set the keys, lets the user input a custom color conversion function (RGB to custom), and allows the user to specify if a single channel or multi-channel image is being filtered.
     *
     * @param robot The robot running the program.
     * @param XUp Button to increment X values.
     * @param XDown Button to decrement X values.
     * @param YUp Button to increment Y values.
     * @param YDown Button to decrement Y values.
     * @param ZUp Button to increment Z values.
     * @param ZDown Button to decrement Z values.
     * @param slowMode Toggle button for slow mode.
     * @param changeLimit Toggle button for changing between changing upper values and lower values.
     * @param converter The custom conversion function.
     * @param imageType An enum that specifies if a single channel or multi-channel image is being filtered.
     * @param channelIdx The index of the filtered channel within the colorspace.
     */
    public ColorspaceCalib(@NotNull Robot robot, @NotNull Button XUp, @NotNull Button XDown, @NotNull Button YUp, @NotNull Button YDown, @NotNull Button ZUp, @NotNull Button ZDown, @NotNull Button slowMode, @NotNull Button changeLimit, @NotNull Function<Mat, Mat> converter, @NotNull ImageType imageType, int channelIdx){
        super(robot);

        if(!robot.usesGUI()){
            throw new GuiNotPresentException("ColorspaceCalib requires a GUI to correctly run");
        }

        if(!robot.isViewportEnabled()) {
            throw new ViewportDisabledException("The camera viewport must be enabled for this program to run");
        }

        displayMenu = new DisplayMenu(robot.gui);
        robot.gui.addMenu("Colorspace Ranges",displayMenu);

        setInputs(XUp, XDown, YUp, YDown, ZUp, ZDown, slowMode, changeLimit);

        this.colorSpace = ColorSpace.CUSTOM;
        this.imageType = imageType;
        this.converter = converter;
        this.channelIdx = channelIdx;
    }

    @Override
    public void init() {

    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {
        if(usesConfig) {
            inputs = robot.pullControls(this);
            ConfigData data = robot.pullNonGamepad(this);
            colorSpace = data.getData("Colorspace", ColorSpace.class);
        }

        startVision();
    }

    @Override
    public void handle() {

        displayMenu.addData("x_upper",x_upper);
        displayMenu.addData("x_lower",x_lower);
        displayMenu.addData("y_upper",y_upper);
        displayMenu.addData("y_lower",y_lower);
        displayMenu.addData("z_upper",z_upper);
        displayMenu.addData("z_lower",z_lower);

        boolean slowMode = inputs.getInput(SLOWMODE);
        slowModeToggle.updateToggle(slowMode);

        if (slowModeToggle.getCurrentState()) {
            increment = 1;
        }
        else {
            increment = 5;
        }

        boolean changeLimit = inputs.getInput(CHANGELIMIT);
        upperLimit.updateToggle(changeLimit);

        if(System.currentTimeMillis() - lastActivatedTimestamp >= delayMs) {
            if (upperLimit.getCurrentState()) {
                if (inputs.getInput(X_INCREMENT)) {
                    x_upper += increment;
                } else if (inputs.getInput(X_DECREMENT)) {
                    x_upper -= increment;
                }
                if (inputs.getInput(Y_INCREMENT)) {
                    y_upper += increment;
                } else if (inputs.getInput(Y_DECREMENT)) {
                    y_upper -= increment;
                }
                if (inputs.getInput(Z_INCREMENT)) {
                    z_upper += increment;
                } else if (inputs.getInput(Z_DECREMENT)) {
                    z_upper -= increment;
                }
            } else {
                if (inputs.getInput(X_INCREMENT)) {
                    x_lower += increment;
                } else if (inputs.getInput(X_DECREMENT)) {
                    x_lower -= increment;
                }
                if (inputs.getInput(Y_INCREMENT)) {
                    y_lower += increment;
                } else if (inputs.getInput(Y_DECREMENT)) {
                    y_lower -= increment;
                }
                if (inputs.getInput(Z_INCREMENT)) {
                    z_lower += increment;
                } else if (inputs.getInput(Z_DECREMENT)) {
                    z_lower -= increment;
                }
            }
            lastActivatedTimestamp = System.currentTimeMillis();
        }

        x_lower = Range.clip(x_lower,0,x_upper);
        x_upper = Range.clip(x_upper,x_lower,255);

        y_lower = Range.clip(y_lower,0,y_upper);
        y_upper = Range.clip(y_upper,y_lower,255);

        z_lower = Range.clip(z_lower,0,z_upper);
        z_upper = Range.clip(z_upper,z_lower,255);
    }

    @Override
    public void stop() {}

    /**
     * Blacks out pixels outside of the color space range and displays image on phones.
     */
    @Override
    public Mat onCameraFrame(@NotNull Mat inputFrame) {

        Mat converted = new Mat();
        Mat rgb = new Mat();

        Imgproc.cvtColor(inputFrame,rgb, Imgproc.COLOR_RGBA2RGB);

        inputFrame.release();

        if(imageType == ImageType.COLOR){

            convertImage(rgb, converted, colorSpace);

            Mat binaryMask = new Mat();
            Core.inRange(converted,new Scalar(x_lower,y_lower,z_lower),new Scalar(x_upper,y_upper,z_upper),binaryMask);

            converted.release();

            Imgproc.cvtColor(binaryMask,binaryMask, Imgproc.COLOR_GRAY2RGB);
            Mat outputMask = new Mat();
            Core.bitwise_and(binaryMask,rgb,outputMask);

            rgb.release();
            binaryMask.release();

            System.gc();

            return outputMask;
        }
        else {

            convertImage(rgb,converted,colorSpace);
            Mat chan = new Mat();
            try {
                Core.extractChannel(converted,chan,channelIdx);}
            catch(CvException e) {
                if(channelIdx < 0 || channelIdx > (converted.channels()-1)) {
                    throw new ChannelDoesNotExistException("Error: Your channel index does not refer to an actual channel in the image.", e);
                }
                else {
                    throw e;
                }
            }

            converted.release();

            Mat binaryMask = new Mat();
            Imgproc.threshold(chan,binaryMask,z_lower,255, Imgproc.THRESH_BINARY);

            chan.release();

            Mat outputMask = new Mat();
            Imgproc.cvtColor(binaryMask,binaryMask, Imgproc.COLOR_GRAY2RGB);
            Core.bitwise_and(binaryMask,rgb,outputMask);
            binaryMask.release();
            rgb.release();

            return outputMask;
        }
    }

    /**
     * Returns lower bounds for x, y, and z in an array.
     *
     * @return An array containing the lower bounds of the colorspace range.
     */
    public int[] getLowerBound() {
        return new int[] {x_lower,y_lower,z_lower};
    }

    /**
     * Returns upper bounds for x, y, and z in an array.
     *
     * @return An array containing the upper bounds of the colorspace range.
     */
    public int[] getUpperBound() {
        return new int[] {x_upper,y_upper,z_upper};
    }

    /**
     * Converts an image from the RGB color space to a specified color space.
     *
     * @param src Source image that needs converting.
     * @param dst Output of the image conversion.
     */
    private void convertImage(@NotNull Mat src, @NotNull Mat dst, @NotNull ColorSpace colorSpace){
        switch (colorSpace){
            case HSV: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2HSV); break;
            case BGR: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2BGR); break;
            case HLS: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2HLS); break;
            case Lab: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2Lab); break;
            case LUV: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2Luv); break;
            case RGB: src.copyTo(dst); break; //Bruh
            case XYZ: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2XYZ); break;
            case YUV: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2YUV); break;
            case YCrCb: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2YCrCb); break;
            case HLS_FULL: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2HLS_FULL); break;
            case HSV_FULL: Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2HSV_FULL); break;
            case CUSTOM: converter.apply(src).copyTo(dst);
            default: break;
        }
    }

    /**
     * Set delay between each x, y, or z change.
     *
     * @param delayMs delay in milliseconds between x, y, or z changes.
     */
    public void setDelay(int delayMs) {
        this.delayMs = delayMs;
    }

    /**
     * Initializes values used in the rest of the program. Only used in the constructor.
     */
    @Override
    protected void initVars() {
        x_lower = 0;
        y_lower = 0;
        z_lower = 0;
        x_upper = 255;
        y_upper = 255;
        z_upper = 255;
        slowModeToggle = new Toggle(Toggle.ToggleTypes.flipToggle, false);
        increment = 5;
        upperLimit = new Toggle(Toggle.ToggleTypes.flipToggle, false);
        lastActivatedTimestamp = 0;
        delayMs = 200;
        inputs = new CustomizableGamepad(robot);
    }

    /**
     * Sets inputs to be used.
     *
     * @param XUp Button to increment X values.
     * @param XDown Button to decrement X values.
     * @param YUp Button to increment Y values.
     * @param YDown Button to decrement Y values.
     * @param ZUp Button to increment Z values.
     * @param ZDown Button to decrement Z values.
     * @param slowMode Toggle button for slow mode.
     * @param changeLimit Toggle button for changing between changing upper values and lower values.
     *                    
     * @throws NotBooleanInputException Throws an exception if button does not return boolean values.
     */
    private void setInputs(@NotNull Button XUp, @NotNull Button XDown, @NotNull Button YUp, @NotNull Button YDown, @NotNull Button ZUp, @NotNull Button ZDown, @NotNull Button slowMode, @NotNull Button changeLimit){
        if(XUp.isBoolean() && XDown.isBoolean() && YUp.isBoolean() && YDown.isBoolean() && ZUp.isBoolean() && ZDown.isBoolean() && slowMode.isBoolean() && changeLimit.isBoolean()) {
            inputs.addButton(X_INCREMENT, XUp);
            inputs.addButton(X_DECREMENT, XDown);
            inputs.addButton(Y_INCREMENT, YUp);
            inputs.addButton(Y_DECREMENT, YDown);
            inputs.addButton(Z_INCREMENT, ZUp);
            inputs.addButton(Z_DECREMENT, ZDown);
            inputs.addButton(SLOWMODE, slowMode);
            inputs.addButton(CHANGELIMIT, changeLimit);
        }
        else {
            throw new NotBooleanInputException("Error: All inputs must be a boolean input");
        }
    }

    /**
     * Returns a list of all of the settings that need to be configured for this program in teleop.
     *
     * @return The settings this program needs to be configured for teleop.
     */
    @NotNull
    @Contract(" -> new")
    @TeleopConfig
    public static ConfigParam[] teleopConfig() {
        return new ConfigParam[] {
                new ConfigParam(X_INCREMENT, Button.BooleanInputs.dpad_up),
                new ConfigParam(X_DECREMENT, Button.BooleanInputs.dpad_down),
                new ConfigParam(Y_INCREMENT, Button.BooleanInputs.bool_left_stick_y_up),
                new ConfigParam(Y_DECREMENT, Button.BooleanInputs.bool_left_stick_y_down),
                new ConfigParam(Z_INCREMENT, Button.BooleanInputs.bool_right_stick_y_up),
                new ConfigParam(Z_DECREMENT, Button.BooleanInputs.bool_right_stick_y_down),
                new ConfigParam(SLOWMODE,Button.BooleanInputs.x),
                new ConfigParam(CHANGELIMIT, Button.BooleanInputs.a),
                new ConfigParam("Colorspace",ColorSpace.RGB)
        };
    }
}
