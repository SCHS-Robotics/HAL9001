package com.SCHSRobotics.HAL9001.util.misc;

import com.SCHSRobotics.HAL9001.util.exceptions.NotAnAlchemistException;

import java.util.Map;

public class ConfigData {

    private Map<String, Object> map;
    public ConfigData(Map<String,Object> map) {
        this.map = map;
    }

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
