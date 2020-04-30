package com.SCHSRobotics.HAL9001.system.menus;

import com.SCHSRobotics.HAL9001.system.source.GUI.GUI;
import com.SCHSRobotics.HAL9001.system.source.GUI.GuiLine;
import com.SCHSRobotics.HAL9001.system.source.GUI.Menu;
import com.SCHSRobotics.HAL9001.system.subsystems.cursors.DefaultCursor;
import com.SCHSRobotics.HAL9001.util.calib.EncoderDistanceCalib;
import com.SCHSRobotics.HAL9001.util.exceptions.NotBooleanInputException;
import com.SCHSRobotics.HAL9001.util.math.Units;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.CustomizableGamepad;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A Menu used in the EncoderDistanceCalibrator to display and calculate the number of encoder ticks per meter.
 *
 * @author Dylan Zueck, Crow Force
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 8/31/19
 */
public class EncoderDistanceCalibMenu extends Menu {

    //How fast the distance input should be increased.
    private enum SpeedMode{
        FAST(25), MEDIUM(10), SLOW(1), PRECISION(.1);

        public double increment;

        @Contract(pure = true)
        SpeedMode(double increment) {
            this.increment = increment;
        }
    }
    private SpeedMode speedMode;
    //The customizable gamepad that stores all the inputs for the program.
    private CustomizableGamepad inputs;
    //The name of the button used to toggle the speed mode.
    private static final String SPEED_MODE_TOGGLE = "SpeedModeToggle";
    //The EncoderDistanceCalib subsystem that this menu interacts with.
    private EncoderDistanceCalib calib;
    //The distance value that has been entered into the menu.
    private double distance;
    //The distance unit being entered into the menu.
    private Units unit;

    /**
     * Constructor for EncoderDistanceCalibMenu.
     *
     * @param gui The GUI being used to render the menu.
     * @param unit The unit of distance being entered into the menu.
     * @param speedToggleButton The button used to toggle the increment/decrement speed.
     * @param calib The EncoderDistanceCalib subsystem associated with this menu.
     *
     * @throws NotBooleanInputException Throws this exception if the speed toggle button is not a boolean button.
     */
    public EncoderDistanceCalibMenu(@NotNull GUI gui, @NotNull Units unit, @NotNull Button<Boolean> speedToggleButton, @NotNull EncoderDistanceCalib calib){
        super(gui, new DefaultCursor(gui.robot, new DefaultCursor.Params()), new GuiLine[]{new GuiLine("<#>", ""), new GuiLine("###", "Done  " + "Increment: " + "Fast")},3,2);

        speedMode = SpeedMode.FAST;
        distance = 0;

        this.unit = unit;
        this.calib = calib;

        inputs = new CustomizableGamepad(gui.robot);
        inputs.addButton(SPEED_MODE_TOGGLE, speedToggleButton);
        
        GuiLine[] newerLines = {
                new GuiLine("<#>", "I traveled: " + "0" + unit.abbreviation),
                lines.get(1)
        };
        setLines(newerLines);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void open() {

    }

    @Override
    public void onSelect() {
        if(cursor.getY() == 1){
            calib.numberSelected(Units.convert(distance, unit, Units.METERS));
        }
    }

    @Override
    public void onButton(@NotNull String name, @NotNull Button<?> button) {
        if(name.equals(DefaultCursor.LEFT) || name.equals(DefaultCursor.RIGHT)){
            if(cursor.getY() == 0 && cursor.getX() == 0){
                distance -= speedMode.increment;
                cursor.setX(1);
                updateLinesForIncrement();
            }
            else if(cursor.getY() == 0 && cursor.getX() == 2){
                distance += speedMode.increment;
                cursor.setX(1);
                updateLinesForIncrement();
            }
        }
        if(name.equals(DefaultCursor.UP)){
            if(cursor.getX() == 0 || cursor.getX() == 2){
                cursor.setX(1);
            }
        }
    }

    @Override
    protected void render() {
        boolean speedModeEnabled = inputs.getInput(SPEED_MODE_TOGGLE);
        if(speedModeEnabled){
            switch (speedMode){
                case FAST:
                    speedMode = SpeedMode.MEDIUM;
                    setLines(new GuiLine[]{lines.get(0), new GuiLine(lines.get(1).getSelectionZoneText(), lines.get(1).getPostSelectionText().substring(0, 6) + "Medium")});
                    break;
                case MEDIUM:
                    speedMode = SpeedMode.SLOW;
                    setLines(new GuiLine[]{lines.get(0), new GuiLine(lines.get(1).getSelectionZoneText(), lines.get(1).getPostSelectionText().substring(0, 6) + "Slow")});
                    break;
                case SLOW:
                    speedMode = SpeedMode.PRECISION;
                    setLines(new GuiLine[]{lines.get(0), new GuiLine(lines.get(1).getSelectionZoneText(), lines.get(1).getPostSelectionText().substring(0, 6) + "Precision")});
                    break;
                case PRECISION:
                    speedMode = SpeedMode.FAST;
                    setLines(new GuiLine[]{lines.get(0), new GuiLine(lines.get(1).getSelectionZoneText(), lines.get(1).getPostSelectionText().substring(0, 6) + "Fast")});
                    break;
            }
        }
        displayLines(lines);
    }

    @Override
    protected void initLoopRender() {

    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void stop() {

    }

    /**
     * Updates the displayed distance value.
     */
    private void updateLinesForIncrement(){
        setLines(new GuiLine[]{
                new GuiLine(lines.get(0).getSelectionZoneText(), "I traveled: " + distance + unit.abbreviation),
                lines.get(1)
        });
    }
}