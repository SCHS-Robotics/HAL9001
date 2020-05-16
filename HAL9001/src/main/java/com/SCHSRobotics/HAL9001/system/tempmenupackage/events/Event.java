package com.SCHSRobotics.HAL9001.system.tempmenupackage.events;

import com.SCHSRobotics.HAL9001.system.tempmenupackage.MaxHeap;

import org.jetbrains.annotations.Nullable;

public abstract class Event implements Comparable<Event> {
    //idk what this is going to do, but I think I am going insane so I'm just going to leave it here for now. I feel like I have an idea but idk what it is yet
    private static final MaxHeap<Event> eventHeap = new MaxHeap<>();
    protected int priority;
    public Event(int priority) {
        this.priority = priority;
    }

    public static void throwEvent(Event event) {
        eventHeap.add(event);
    }

    @Nullable
    public static Event getNextEvent() {
        return eventHeap.poll();
    }

    @Override
    public int compareTo(Event event) {
        return this.priority - event.priority;
    }
}
