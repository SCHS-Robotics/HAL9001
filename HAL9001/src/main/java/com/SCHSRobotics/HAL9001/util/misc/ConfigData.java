package com.SCHSRobotics.HAL9001.util.misc;

import com.SCHSRobotics.HAL9001.util.exceptions.NotAnAlchemistException;

import java.util.Map;

/**
 * A class for getting non-gamepad data out of the configuration system easily.
 *
 * @author Cole Savage, Level Up
 * @since 1.0.0
 * @version 1.0.0
 *
 * Creation Date: 12/8/19
 */
public class ConfigData {

    //A hashmap to store the config data in.
    private Map<String, Object> map;

    /**
     * Constructor for ConfigData. Never actually used by the user.
     *
     * @param map A hashmap containing raw data pulled from the config.
     */
    public ConfigData(Map<String,Object> map) {
        this.map = map;
    }

    /**
     * Gets the data associated with the specified name in the config and returns it.
     *
     * @param name The name of the configuration option.
     * @param clazz The datatype of the configuration option.
     * @param <T> The datatype of the configuration option.
     * @return The value of the configuration option.
     *
     * @throws NullPointerException Throws this exception if the config option does not exist.
     * @throws NotAnAlchemistException Throws this exception if the wrong type parameter was provided for the config option.
     */
    public <T> T getData(String name, Class<T> clazz) {
        Object val = map.get(name);
        if(val == null) {
            throw new NullPointerException("No such value in config with name "+name+"!");
        }
        if(!clazz.isInstance(val)) {
            throw new NotAnAlchemistException("Wrong type parameter provided for config parameter "+name+"!");
        }
        return clazz.cast(val);
    }
}
