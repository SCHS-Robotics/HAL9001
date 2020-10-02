package com.SCHSRobotics.HAL9001.system.robot.subsystems;

import com.SCHSRobotics.HAL9001.system.robot.Robot;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.I2cAddr;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.MagneticFlux;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;
import org.firstinspires.ftc.robotcore.external.navigation.Temperature;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

public class IMU {
    private BNO055IMU imu;
    private BNO055IMU.Parameters parameters;
    private Robot robot;
    public IMU(Robot robot, Params params) {
        this.robot = robot;
        parameters = params.parameters;
        imu = robot.hardwareMap.get(BNO055IMU.class, params.imuConfig);
    }

    public IMU(Robot robot, String imuConfig) {
        this(robot, new Params(imuConfig));
    }

    public IMU(Robot robot, int imuNumber) {
        this(robot, new Params(imuNumber));
    }

    public void init() {
        imu.initialize(parameters);
        robot.telemetry.addLine("Calibrating...");
        robot.telemetry.update();
        while(!imu.isSystemCalibrated());
        robot.telemetry.addLine("Calibration Complete");
        robot.telemetry.update();
    }

    /**
     * Gets the robot's current yaw angle from the gyro.
     *
     * @param angleUnit The unit the angle will be returned in.
     * @return The current yaw angle if the gyroscope is use. If the gyro is not active, it returns 0.
     */
    public double getCurrentAngle(AngleUnit angleUnit) {
        return getOrientation(angleUnit).firstAngle;
    }

    /**
     * Gets the robot's current yaw angle from the gyro.
     *
     * @return The current yaw angle if the gyroscope is use. If the gyro is not active, it returns 0.
     */
    public double getCurrentAngle() {
        return getCurrentAngle(AngleUnit.RADIANS);
    }

    /**
     * Gets the robot's current pitch angle from the gyro.
     *
     * @param angleUnit The unit the angle will be returned in.
     * @return The current pitch angle if the gyroscope is use. If the gyro is not active, it returns 0.
     */
    public double getCurrentPitch(AngleUnit angleUnit) {
        return getOrientation(angleUnit).secondAngle;
    }

    /**
     * Gets the robot's current pitch angle from the gyro.
     *
     * @return The current pitch angle if the gyroscope is use. If the gyro is not active, it returns 0.
     */
    public double getCurrentPitch() {
        return getCurrentPitch(AngleUnit.RADIANS);
    }

    /**
     * Gets the robot's current roll angle from the gyro.
     *
     * @param angleUnit The unit the angle will be returned in.
     * @return The current roll angle if the gyroscope is use. If the gyro is not active, it returns 0.
     */
    public double getCurrentRoll(AngleUnit angleUnit) {
        return getOrientation(angleUnit).thirdAngle;
    }

    /**
     * Gets the robot's current roll angle from the gyro.
     *
     * @return The current roll angle if the gyroscope is use. If the gyro is not active, it returns 0.
     */
    public double getCurrentRoll() {
        return getCurrentRoll(AngleUnit.RADIANS);
    }

    /**
     * Gets the robot's angular orientation in space.
     *
     * @param angleUnit The unit the angles will be returned in.
     * @param order The order of the axes used to define the angles.
     * @param reference The coordinate axes reference.
     * @return The robot's orientation.
     */
    public Orientation getOrientation(AngleUnit angleUnit, AxesOrder order, AxesReference reference) {
        return imu.getAngularOrientation(reference,order,angleUnit);
    }

    /**
     * Gets the robot's angular orientation in space.
     *
     * @param angleUnit The unit the angles will be returned in.
     * @param order The order of the axes used to define the angles.
     * @return The robot's orientation.
     */
    public Orientation getOrientation(AngleUnit angleUnit, AxesOrder order) {
        return getOrientation(angleUnit,order,AxesReference.INTRINSIC);
    }

    /**
     * Gets the robot's angular orientation in space.
     *
     * @param angleUnit The unit the angles will be returned in.
     * @param reference The coordinate axes reference.
     * @return The robot's orientation.
     */
    public Orientation getOrientation(AngleUnit angleUnit, AxesReference reference) {
        return getOrientation(angleUnit,AxesOrder.ZYX,reference);
    }

    /**
     * Gets the robot's angular orientation in space.
     *
     * @param order The order of the axes used to define the angles.
     * @param reference The coordinate axes reference.
     * @return The robot's orientation.
     */
    public Orientation getOrientation(AxesOrder order, AxesReference reference) {
        return getOrientation(AngleUnit.RADIANS, order, reference);
    }

    /**
     * Gets the robot's angular orientation in space.
     *
     * @param angleUnit The unit the angles will be returned in.
     * @return The robot's orientation.
     */
    public Orientation getOrientation(AngleUnit angleUnit) {
        return getOrientation(angleUnit,AxesOrder.ZYX,AxesReference.INTRINSIC);
    }

    /**
     * Gets the robot's angular orientation in space.
     *
     * @param order The order of the axes used to define the angles.
     * @return The robot's orientation.
     */
    public Orientation getOrientation(AxesOrder order) {
        return getOrientation(AngleUnit.RADIANS,order,AxesReference.INTRINSIC);
    }

    /**
     * Gets the robot's angular orientation in space.
     *
     * @param reference The coordinate axes reference.
     * @return The robot's orientation.
     */
    public Orientation getOrientation(AxesReference reference) {
        return getOrientation(AngleUnit.RADIANS,AxesOrder.ZYX,reference);
    }

    /**
     * Gets the robot's angular orientation in space.
     *
     * @return The robot's orientation.
     */
    public Orientation getOrientation() {
        return getOrientation(AngleUnit.RADIANS,AxesOrder.ZYX,AxesReference.INTRINSIC);
    }

    public Quaternion getQuaternionOrientation() {
        return imu.getQuaternionOrientation();
    }

    public Acceleration getAcceleration() {
        return imu.getAcceleration();
    }

    public Acceleration getLinearAcceleration() {
        return imu.getLinearAcceleration();
    }

    public Acceleration getOverallAcceleration() {
        return imu.getOverallAcceleration();
    }

    public AngularVelocity getAngularVelocity() {
        return imu.getAngularVelocity();
    }

    public Acceleration getGravity() {
        return imu.getGravity();
    }

    public MagneticFlux getMagneticFieldStrength() {
        return imu.getMagneticFieldStrength();
    }

    public Temperature getTemperature() {
        return imu.getTemperature();
    }

    public Velocity getVelocity() {
        return imu.getVelocity();
    }

    public Position getPosition() {
        return imu.getPosition();
    }

    public void startAccelerationIntegration(Position initialPosition, Velocity initialVelocity,int msPollInterval) {
        imu.startAccelerationIntegration(initialPosition,initialVelocity,msPollInterval);
    }

    public void stopAccelerationIntegration() {
        imu.stopAccelerationIntegration();
    }

    public void writeCalibrationDataFile() {
        imu.writeCalibrationData(imu.readCalibrationData());
    }

    public static final class Params {
        private BNO055IMU.Parameters parameters;
        private String imuConfig;
        public Params(String imuConfig) {
            this(imuConfig, new BNO055IMU.Parameters());
        }
        public Params(String imuConfig, BNO055IMU.Parameters parameters) {
            this.parameters = parameters;
            this.imuConfig = imuConfig;
        }
        public Params(int imuNumber) {
            this(imuNumber == 2 ? "imu 2" : "imu", new BNO055IMU.Parameters());
        }
        public Params(int imuNumber, BNO055IMU.Parameters parameters) {
            this(imuNumber == 2 ? "imu 2" : "imu", parameters);
        }

        public Params setAccelBandwidth(BNO055IMU.AccelBandwidth accelBandwidth) {
            parameters.accelBandwidth = accelBandwidth;
            return this;
        }
        public Params setAccelerationIntegrationAlgorithm(BNO055IMU.AccelerationIntegrator integrationAlgorithm) {
            parameters.accelerationIntegrationAlgorithm = integrationAlgorithm;
            return this;
        }
        public Params setAccelerationPowerMode(BNO055IMU.AccelPowerMode powerMode) {
            parameters.accelPowerMode = powerMode;
            return this;
        }
        public Params setAccelerationRange(BNO055IMU.AccelRange accelerationRange) {
            parameters.accelRange = accelerationRange;
            return this;
        }
        public Params setAccelerationUnit(BNO055IMU.AccelUnit unit) {
            parameters.accelUnit = unit;
            return this;
        }
        public Params setGyroBandwidth(BNO055IMU.GyroBandwidth gyroBandwidth) {
            parameters.gyroBandwidth = gyroBandwidth;
            return this;
        }
        public Params setGyroPowerMode(BNO055IMU.GyroPowerMode gyroPowerMode) {
            parameters.gyroPowerMode = gyroPowerMode;
            return this;
        }
        public Params setGyroRange(BNO055IMU.GyroRange gyroRange) {
            parameters.gyroRange = gyroRange;
            return this;
        }
        public Params setAngleUnit(BNO055IMU.AngleUnit angleUnit) {
            parameters.angleUnit = angleUnit;
            return this;
        }
        public Params setPitchMode(BNO055IMU.PitchMode pitchMode) {
            parameters.pitchMode = pitchMode;
            return this;
        }
        public Params setMagnetometerOperationMode(BNO055IMU.MagOpMode magnetometerOperationMode) {
            parameters.magOpMode = magnetometerOperationMode;
            return this;
        }
        public Params setMagnetometerPowerMode(BNO055IMU.MagPowerMode magnetometerPowerMode) {
            parameters.magPowerMode = magnetometerPowerMode;
            return this;
        }
        public Params setMagnetometerUpdateRate(BNO055IMU.MagRate updateRate) {
            parameters.magRate = updateRate;
            return this;
        }
        public Params setTemperatureUnit(BNO055IMU.TempUnit temperatureUnit) {
            parameters.temperatureUnit = temperatureUnit;
            return this;
        }
        public Params setCalibrationData(BNO055IMU.CalibrationData calibrationData) {
            parameters.calibrationData = calibrationData;
            return this;
        }
        public Params setCalibrationDataFile(String calibrationDataFilePath) {
            parameters.calibrationDataFile = calibrationDataFilePath;
            return this;
        }
        public Params setI2CAddress(I2cAddr i2CAddress) {
            parameters.i2cAddr = i2CAddress;
            return this;
        }
        public Params setLoggingEnabled(boolean loggingEnabled) {
            parameters.loggingEnabled = loggingEnabled;
            return this;
        }
        public Params setLoggingTag(String loggingTag) {
            parameters.loggingTag = loggingTag;
            return this;
        }
        public Params setSensorMode(BNO055IMU.SensorMode sensorMode) {
            parameters.mode = sensorMode;
            return this;
        }
        public Params setUseExternalCrystal(boolean useExternalCrystal) {
            parameters.useExternalCrystal = useExternalCrystal;
            return this;
        }

        @Override
        protected Params clone() {
            return new Params(imuConfig, parameters.clone());
        }
    }
}
