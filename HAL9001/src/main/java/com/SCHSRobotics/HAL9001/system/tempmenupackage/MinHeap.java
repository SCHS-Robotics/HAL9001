package com.SCHSRobotics.HAL9001.system.tempmenupackage;

public class MinHeap<T extends Comparable<T>> extends Heap<T> {

    @Override
    protected void heapifyUp() {
        int idx = count;
        int parent = parentIdx(idx);
        while(parent > 0) {
            if(heapList.get(parent).compareTo(heapList.get(idx)) > 0) {
                T temp = heapList.get(parent);
                heapList.set(parent, heapList.get(idx));
                heapList.set(idx, temp);
            }
            idx = parent;
            parent = parentIdx(idx);
        }
    }

    @Override
    protected void heapifyDown() {
        int idx = 1;
        while(childPresent(idx)) {
            int smallerChildIdx = getSmallerChild(idx);
            if(heapList.get(idx).compareTo(heapList.get(smallerChildIdx)) > 0) {
                T temp = heapList.get(smallerChildIdx);
                heapList.set(smallerChildIdx, heapList.get(idx));
                heapList.set(idx, temp);
            }
            idx = smallerChildIdx;
        }
    }

    private int getSmallerChild(int idx) {
        int leftIdx = leftChildIdx(idx);
        int rightIdx = rightChildIdx(idx);
        if(rightIdx > count) {
            return leftIdx;
        }
        T leftChild = heapList.get(leftIdx);
        T rightChild = heapList.get(rightIdx);
        //negative means less than, 0 means equal, positive means greater than
        if(leftChild.compareTo(rightChild) < 0) {
            return leftIdx;
        }
        return rightIdx;
    }
}
