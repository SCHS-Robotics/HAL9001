package com.SCHSRobotics.HAL9001.system.tempmenupackage;

public class DataPacket {
    private final Event event;
    private final EventListener listener;
    public DataPacket(Event event, EventListener listener) {
        this.event = event;
        this.listener = listener;
    }

    public <T extends Event> T getEvent() {
        return (T) event;
    }

    public <T extends EventListener> T getListener() {
        return (T) listener;
    }
}
