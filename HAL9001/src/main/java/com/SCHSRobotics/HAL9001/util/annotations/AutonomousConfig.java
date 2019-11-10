/*
 * Filename: AutonomousConfig.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/18/19
 */

package com.SCHSRobotics.HAL9001.util.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A method annotation used to denote a method that specifies the autonomous config options for a subsystem.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutonomousConfig {}
