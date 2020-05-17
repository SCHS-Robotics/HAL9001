package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import java.util.Set;

public class TaskPacket <T> {
    private MultiElementMap<T, Task> tasks;
    public TaskPacket() {
        tasks = new MultiElementMap<>();
    }

    public void add(Task task) {
        add(null, task);
    }

    public void add(T key, Task task) {
        tasks.put(key, task);
    }

    public Set<T> getValidKeys() {
        return tasks.keySet();
    }

    public void runTasks(T key, DataPacket packet) {
        if(tasks.containsKey(key)) {
            for (Task task : tasks.get(key)) {
                task.run(packet);
            }
        }
    }

    public void runTasks(DataPacket packet) {
        runTasks(null, packet);
    }
}
