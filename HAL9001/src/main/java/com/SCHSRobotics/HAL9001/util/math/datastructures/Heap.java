package com.SCHSRobotics.HAL9001.util.math.datastructures;

import java.util.ArrayList;
import java.util.List;

public abstract class Heap <T> {
    protected List<T> heapList;
    protected int count;

    public Heap() {
        heapList = new ArrayList<>();
        heapList.add(null);
        count = 0;
    }

    protected abstract void heapifyUp();

    protected abstract void heapifyDown();

    public final void add(T element) {
        count++;
        heapList.add(element);
        heapifyUp();
    }

    public final T poll() {
        if(count == 0) {
            return null;
        }
        T val = heapList.get(1);
        heapList.set(1, heapList.get(count));
        heapList.remove(count);
        count--;
        heapifyDown();
        return val;
    }

    protected final int parentIdx(int idx) {
        return idx / 2;
    }

    protected final int leftChildIdx(int idx) {
        return idx * 2;
    }

    protected final int rightChildIdx(int idx) {
        return idx * 2 + 1;
    }

    protected final boolean childPresent(int idx) {
        return leftChildIdx(idx) <= count;
    }

    public final int size() {
        return count;
    }
}
