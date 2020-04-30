package com.SCHSRobotics.HAL9001.util.misc;

import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BidirectionalMap<K,V> implements Map<K, V> {

    private Map<K,V> forwardMap;
    private Map<V,K> reverseMap;

    public BidirectionalMap() {
        forwardMap = new HashMap<>();
        reverseMap = new HashMap<>();
    }

    public BidirectionalMap(Map<K,V> forwardMap) {
        this.forwardMap = forwardMap;
        this.reverseMap = new HashMap<>();
        for(Entry<K,V> p : forwardMap.entrySet()) {
            if(reverseMap.containsKey(p.getValue())) {
                throw new DumpsterFireException("Duplicate value detected: Key value pairs in a bidirectional map must be invertible.");
            }
            reverseMap.put(p.getValue(), p.getKey());
        }
    }

    @Override
    public int size() {
        return forwardMap.size();
    }

    @Override
    public boolean isEmpty() {
        return forwardMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return forwardMap.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return forwardMap.containsValue(o);
    }

    @Override
    public V get(Object o) {
        return forwardMap.get(o);
    }

    public V getForward(Object o) {
        return get(o);
    }

    public K getReverse(Object o) {
        return reverseMap.get(o);
    }

    @Override
    public V put(@NotNull K k, @NotNull V v) {
        forwardMap.put(k, v);
        reverseMap.put(v, k);
        return v;
    }

    public V putForward(@NotNull K k, @NotNull V v) {
        return put(k, v);
    }

    public K putReverse(@NotNull V v, @NotNull K k) {
        forwardMap.put(k, v);
        reverseMap.put(v, k);
        return k;
    }

    @Override
    public V remove(Object o) {
        V val = forwardMap.remove(o);
        reverseMap.remove(val);
        return val;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> map) {
        forwardMap.putAll(map);
        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            reverseMap.put(entry.getValue(), entry.getKey());
        }
    }

    public BidirectionalMap<V, K> invert() {
        return new BidirectionalMap<>(reverseMap);
    }

    @Override
    public void clear() {
        forwardMap.clear();
        reverseMap.clear();
    }

    @Override
    public @NotNull Set<K> keySet() {
        return forwardMap.keySet();
    }

    @Override
    public @NotNull Collection<V> values() {
        return forwardMap.values();
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return forwardMap.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if(o.getClass() == BidirectionalMap.class) {
            return false;
        }
        return forwardMap.equals(o);
    }

    @Override
    public int hashCode() {
        return forwardMap.hashCode();
    }
}
