package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import java.util.HashMap;
import java.util.Map;

public class Payload {
    private final Map<String, Object> objectMap;
    private final Map<String, Class<?>> classMap;

    public Payload() {
        objectMap = new HashMap<>();
        classMap = new HashMap<>();
    }

    public Payload addItem(String id, Object obj) {
        objectMap.put(id, obj);
        classMap.put(id, obj.getClass());
        return this;
    }

    public <T> T getItem(String id) {
        Object obj = objectMap.get(id);
        Class<T> clazz = (Class<T>) classMap.get(id);
        return clazz.cast(obj);
    }

    public boolean idPresent(String id) {
        return objectMap.containsKey(id);
    }
}
