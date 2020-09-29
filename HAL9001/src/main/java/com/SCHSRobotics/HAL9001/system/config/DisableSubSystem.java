package com.SCHSRobotics.HAL9001.system.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that prevents a subsystem field in the robot class from being detected by the internal code, disabling it.
 *
 * @author Cole Savage, Level Up
 * @version 1.0.0
 * <p>
 * Creation Date: 12/21/19
 * @since 1.0.6
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DisableSubSystem {}
