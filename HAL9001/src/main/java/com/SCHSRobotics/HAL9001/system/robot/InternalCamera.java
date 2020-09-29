package com.SCHSRobotics.HAL9001.system.robot;

import org.openftc.easyopencv.OpenCvInternalCamera;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @// TODO: 9/24/2020
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InternalCamera {
    int resWidth();

    int resHeight();

    OpenCvInternalCamera.CameraDirection direction() default OpenCvInternalCamera.CameraDirection.BACK;

    boolean usesViewport();
}