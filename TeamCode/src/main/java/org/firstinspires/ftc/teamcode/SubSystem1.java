package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.SCHSRobotics.HAL9001.system.source.BaseRobot.Robot;
import com.SCHSRobotics.HAL9001.system.source.BaseRobot.SubSystem;
import com.SCHSRobotics.HAL9001.util.annotations.TeleopConfig;
import com.SCHSRobotics.HAL9001.util.misc.ConfigData;
import com.SCHSRobotics.HAL9001.util.misc.ConfigParam;

public class SubSystem1 extends SubSystem {

    enum TESTING {
        A, B, C, D
    }
    TESTING test;

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
    }

    @Override
    public void handle() {
        Log.w("BAM",test.name());
    }

    @Override
    public void stop() {

    }

    @TeleopConfig
    public static ConfigParam[] teleopConfig() {
        return new ConfigParam[] {
                new ConfigParam("TEST", SubSystem3.TESTING.A),
                new ConfigParam("TEST2", SubSystem3.TESTING.A),
                new ConfigParam("TEST3", SubSystem3.TESTING.A)
        };
    }
}
