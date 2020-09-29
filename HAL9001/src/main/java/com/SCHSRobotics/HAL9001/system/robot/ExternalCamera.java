package com.SCHSRobotics.HAL9001.system.robot;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @// TODO: 12/24/2020
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(ExternalCamera.CameraContainer.class)
public @interface ExternalCamera {
    int resWidth();

    int resHeight();

    String configName();

    String uniqueId() default "";

    boolean usesViewport();

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface CameraContainer {
        ExternalCamera[] value();
    }
}