package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.SubSystem;
import com.SCHSRobotics.HAL9001.util.annotations.ConfigProgramType;
import com.SCHSRobotics.HAL9001.util.annotations.ConfigurableBoolean;
import com.SCHSRobotics.HAL9001.util.annotations.ConfigurableButton;
import com.SCHSRobotics.HAL9001.util.annotations.ConfigurableDouble;
import com.SCHSRobotics.HAL9001.util.annotations.ConfigurableInteger;
import com.SCHSRobotics.HAL9001.util.annotations.TeleopConfig;
import com.SCHSRobotics.HAL9001.util.misc.Button;
import com.SCHSRobotics.HAL9001.util.misc.ConfigData;
import com.SCHSRobotics.HAL9001.util.misc.ConfigParam;

public class SubSystem1 extends SubSystem {

    enum TESTING {
        A, B, C, D
    }
    TESTING test;
    public @ConfigurableButton(name = "hah") Button b = new Button(1,Button.BooleanInputs.b);
    public @ConfigurableBoolean(name = "WHAHBAM", program_type = ConfigProgramType.TELEOP) boolean b2 = true;
    public @ConfigurableDouble(name = "test", lowerBound = 0.1, upperBound = 1, program_type = ConfigProgramType.TELEOP) double x = 0.4;
    public @ConfigurableInteger(name = "test2", lowerBound = 2, upperBound = 10, program_type = ConfigProgramType.TELEOP) int z = 5;

    public SubSystem1(Robot robot) {
        super(robot);
        usesConfig = true;
    }

    @Override
    public void init() {

    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {
        ConfigData data = robot.pullNonGamepad(this);
        test = data.getData("TEST", TESTING.class);
        Log.wtf("b2",""+b2);
        Log.wtf("x",""+x);
        Log.wtf("z",""+z);
    }

    @Override
    public void handle(){

    }

    @Override
    public void stop() {

    }

    @TeleopConfig
    public static ConfigParam[] teleopConfig() {
        return new ConfigParam[] {
                new ConfigParam("TEST", TESTING.A),
                new ConfigParam("TEST2", TESTING.A),
                new ConfigParam("TEST3", TESTING.A)
        };
    }
}
