package com.SCHSRobotics.HAL9001.system.tempmenupackage.events;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MultiElementMap<K, V> implements Map<K, List<V>> {
    private Map<K, List<V>> internalStorage;
    public MultiElementMap() {
        internalStorage = new HashMap<>();
    }

    @Override
    public int size() {
        return internalStorage.size();
    }

    @Override
    public boolean isEmpty() {
        return internalStorage.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return internalStorage.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        Collection<List<V>> values = internalStorage.values();

        for(List<V> valueList : values) {
            if(valueList.contains(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<V> get(Object o) {
        return internalStorage.get(o);
    }

    @Override
    public List<V> put(@NotNull K k, @NotNull List<V> v) {
        return internalStorage.put(k, v);
    }

    public V putElement(@NotNull K k, @NotNull V v) {
        List<V> currentlyStored = internalStorage.get(k);
        if(currentlyStored == null) {
            currentlyStored = new ArrayList<>();
            currentlyStored.add(v);
            internalStorage.put(k, currentlyStored);
        }
        else {
            currentlyStored.add(v);
        }
        return v;
    }

    @Override
    public List<V> remove(Object o) {
        return Objects.requireNonNull(internalStorage.remove(o));
    }

    @Override
    public void putAll(Map<? extends K, ? extends List<V>> map) {
        for(Entry<? extends K, ? extends List<V>> pair : map.entrySet()) {
            put(pair.getKey(), pair.getValue());
        }
    }

    @Override
    public void clear() {
        for(List<V> valueList : internalStorage.values()) {
            valueList.clear();
        }
        internalStorage.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return internalStorage.keySet();
    }

    @NotNull
    @Override
    public Collection<List<V>> values() {
        return internalStorage.values();
    }

    @NotNull
    @Override
    public Set<Entry<K, List<V>>> entrySet() {
        return internalStorage.entrySet();
    }
}
