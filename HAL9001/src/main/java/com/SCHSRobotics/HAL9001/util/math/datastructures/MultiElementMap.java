package com.SCHSRobotics.HAL9001.util.math.datastructures;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MultiElementMap<K, V> implements Iterable<List<V>> {
    private Map<K, List<V>> internalStorage;
    public MultiElementMap() {
        internalStorage = new HashMap<>();
    }

    public int size() {
        return internalStorage.size();
    }

    public boolean isEmpty() {
        return internalStorage.isEmpty();
    }

    public boolean containsKey(Object o) {
        return internalStorage.containsKey(o);
    }

    public boolean containsValue(Object o) {
        Collection<List<V>> values = internalStorage.values();

        for(List<V> valueList : values) {
            if(valueList.contains(o)) {
                return true;
            }
        }
        return false;
    }

    public List<V> get(Object o) {
        return internalStorage.get(o);
    }

    public V put(K k, @NotNull V v) {
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

    public void putList(K k, @NotNull List<V> v) {
        internalStorage.put(k, v);
    }

    public List<V> remove(Object o) {
        return Objects.requireNonNull(internalStorage.remove(o));
    }

    public void putAll(Map<? extends K, ? extends List<V>> map) {
        for(Map.Entry<? extends K, ? extends List<V>> pair : map.entrySet()) {
            putList(pair.getKey(), pair.getValue());
        }
    }

    public void clear() {
        for(List<V> valueList : internalStorage.values()) {
            valueList.clear();
        }
        internalStorage.clear();
    }

    @NotNull
    public Set<K> keySet() {
        return internalStorage.keySet();
    }

    @NotNull
    public Collection<List<V>> values() {
        return internalStorage.values();
    }

    @NotNull
    public Set<Map.Entry<K, List<V>>> entrySet() {
        return internalStorage.entrySet();
    }

    @NotNull
    @Override
    public Iterator<List<V>> iterator() {
        return internalStorage.values().iterator();
    }
}
