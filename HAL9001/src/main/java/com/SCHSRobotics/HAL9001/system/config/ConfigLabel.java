package com.SCHSRobotics.HAL9001.system.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//4/11/20

/**
 * An annotation used to give HAL subsystems specific names. Used to annotate subsystem fields in the robot class
 *
 * @author Cole Savage
 * @version 1.0.0
 * <p>
 * Creation Date: 4/11/20
 * @since 1.1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigLabel {
    String label();
}
