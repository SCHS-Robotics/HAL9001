package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CriteriaPacket implements Iterable<EventCriteria<?>> {
    private List<EventCriteria<? extends Event>> criteriaList;

    public CriteriaPacket() {
        criteriaList = new ArrayList<>();
    }

    public <T extends Event> void add(EventCriteria<T> criteria) {
        criteriaList.add(criteria);
    }

    @NotNull
    @Override
    public Iterator<EventCriteria<?>> iterator() {
        return criteriaList.iterator();
    }
}