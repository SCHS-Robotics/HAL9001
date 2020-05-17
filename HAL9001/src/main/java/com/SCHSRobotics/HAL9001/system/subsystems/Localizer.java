package com.SCHSRobotics.HAL9001.system.subsystems;

import com.SCHSRobotics.HAL9001.util.math.EncoderToDistanceProcessor;
import com.qualcomm.robotcore.hardware.DcMotor;

public abstract class Localizer {

    protected DcMotor[] encoders;
    protected EncoderToDistanceProcessor distanceProcessor;
    protected boolean encodersSet;

    public Localizer(EncoderToDistanceProcessor distanceProcessor) {
        this.distanceProcessor = distanceProcessor;
        encodersSet = false;
    }

    public Localizer(double encoderPerMeter) {
        this(new EncoderToDistanceProcessor(encoderPerMeter));
    }

    public Localizer() {
        this(1.0);
    }

    public void setEncoders(DcMotor... encoders) {
        this.encoders = encoders;
        encodersSet = true;
    }
/*
    public abstract Pose2d getPositionEncoders();

    public abstract Pose2d getVelocityEncoders();

    public Pose2d getPosition(Units distanceUnit) {
        Pose2d positionEncoders = getPositionEncoders();
        return new Pose2d(
                distanceProcessor.getDistanceFromEncoders(positionEncoders.getX(),distanceUnit),
                distanceProcessor.getDistanceFromEncoders(positionEncoders.getY(),distanceUnit),
                positionEncoders.getHeading());
    }
    public Pose2d getVelocity(Units distanceUnit) {
        Pose2d velocityEncoders = getPositionEncoders();
        return new Pose2d(
                distanceProcessor.getDistanceFromEncoders(velocityEncoders.getX(),distanceUnit),
                distanceProcessor.getDistanceFromEncoders(velocityEncoders.getY(),distanceUnit),
                velocityEncoders.getHeading());
    }
*/
    //TODO bulk reads
    public int[] getRawEncoders() {
        int[] encoderVals = new int[encoders.length];
        for(int i = 0; i < encoders.length; i++) {
            encoderVals[i] = encoders[i].getCurrentPosition();
        }
        return encoderVals;
    }

    public void resetEncoders() {
        for(DcMotor encoder : encoders) {
            DcMotor.RunMode runMode = encoder.getMode();
            encoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            encoder.setMode(runMode);
        }
    }
}
