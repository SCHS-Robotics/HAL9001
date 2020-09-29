package com.SCHSRobotics.HAL9001.system.gui;

import com.SCHSRobotics.HAL9001.util.misc.UniqueID;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Payload {
    private final Map<UniqueID, Object> objectMap;
    private final Map<UniqueID, Class<?>> classMap;

    public Payload() {
        objectMap = new HashMap<>();
        classMap = new HashMap<>();
    }

    public Payload add(UniqueID id, Object obj) {
        objectMap.put(id, obj);
        classMap.put(id, obj.getClass());
        return this;
    }

    public Payload copyFrom(Payload otherPayload, UniqueID... idsToAdd) {
        for(UniqueID id : idsToAdd) {
            if(otherPayload.idPresent(id)) {
                add(id, otherPayload.get(id));
            }
        }
        return this;
    }

    public <T> T remove(UniqueID id) {
        Object obj = objectMap.remove(id);
        Class<T> clazz = (Class<T>) classMap.remove(id);
        return clazz.cast(obj);
    }

    public <T> T get(UniqueID id) {
        Object obj = objectMap.get(id);
        Class<T> clazz = (Class<T>) classMap.get(id);
        return clazz.cast(obj);
    }

    public boolean idPresent(UniqueID id) {
        return objectMap.containsKey(id);
    }

    @NotNull
    @Override
    public String toString() {
        return objectMap.keySet().toString();
    }
}
