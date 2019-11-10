/*
 * Filename: StandAlone.java
 * Author: Cole Savage
 * Team Name: Level Up
 * Date: 8/17/19
 */

package com.SCHSRobotics.HAL9001.util.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A class annotation used to denote that an opmode is a stand-alone program.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StandAlone {}
