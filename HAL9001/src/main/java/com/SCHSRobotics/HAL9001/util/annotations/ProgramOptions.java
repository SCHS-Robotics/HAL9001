package com.SCHSRobotics.HAL9001.util.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A class annotation used to easily put into place program configuration options.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.6
 * @version 1.0.0
 *
 * Creation Date: 12/18/19
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProgramOptions {
    Class<? extends Enum<?>>[] options();
}
