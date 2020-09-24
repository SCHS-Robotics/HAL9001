package com.SCHSRobotics.HAL9001.system.gui.event.criteria;

import com.SCHSRobotics.HAL9001.system.gui.event.Event;

import org.firstinspires.ftc.robotcore.external.function.Function;
import org.jetbrains.annotations.Contract;

public class EventCriteria<T extends Event> {

    private Function<T, Boolean> criteria;

    public EventCriteria(Function<T, Boolean> criteria) {
        this.criteria = criteria;
    }

    public EventCriteria() {
        this((T e) -> true);
    }

    public final boolean satisfiesCriteria(Event event) {
        if(acceptsEvent(event)) {
            return criteria.apply((T) event);
        }
        return false;
    }

    @Contract(pure = true)
    public final boolean acceptsEvent(Event event) {
        try {
            criteria.apply((T) event);
            return true;
        }
        catch (ClassCastException e) {
            return false;
        }
    }
}
