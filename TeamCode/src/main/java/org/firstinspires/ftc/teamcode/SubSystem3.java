package org.firstinspires.ftc.teamcode;

import com.SCHSRobotics.HAL9001.system.config.ConfigData;
import com.SCHSRobotics.HAL9001.system.config.ConfigParam;
import com.SCHSRobotics.HAL9001.system.config.TeleopConfig;
import com.SCHSRobotics.HAL9001.system.robot.Robot;
import com.SCHSRobotics.HAL9001.system.robot.SubSystem;

public class SubSystem3 extends SubSystem {

    enum TESTING {
        A, B, C, D
    }
    TESTING test;
    
    public SubSystem3(Robot robot) {
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
    public void handle() {}

    @Override
    public void stop() {

    }

    @TeleopConfig
    public static ConfigParam[] teleopConfig() {
        return new ConfigParam[] {
                new ConfigParam("TEST", TESTING.A),
                new ConfigParam("TEST2", TESTING.A),
                new ConfigParam("TEST3", TESTING.A),
                new ConfigParam("TEST4",ConfigParam.numberMap(1.0,100.0,0.02),1.0)
        };
    }
}
