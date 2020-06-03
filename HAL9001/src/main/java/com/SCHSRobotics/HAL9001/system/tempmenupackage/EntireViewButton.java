package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import android.util.Log;

import com.SCHSRobotics.HAL9001.util.misc.Button;

@HandlesEvents(events = {OnClickEvent.class, WhileClickEvent.class, OnClickReleaseEvent.class})
public class EntireViewButton implements AdvancedListener {
    private TaskPacket<Button<Boolean>> onClickTasks, whileClickedTasks, onClickReleasedTasks, backgroundTasks;

    public EntireViewButton() {
        onClickTasks = new TaskPacket<>();
        whileClickedTasks = new TaskPacket<>();
        onClickReleasedTasks = new TaskPacket<>();
        backgroundTasks = new TaskPacket<>();
    }

    public EntireViewButton onClick(Button<Boolean> button, Task task) {
        onClickTasks.add(button, task);
        return this;
    }

    public EntireViewButton whileClicked(Button<Boolean> button, Task task) {
        whileClickedTasks.add(button, task);
        return this;
    }

    public EntireViewButton onClickReleased(Button<Boolean> button, Task task) {
        onClickReleasedTasks.add(button, task);
        return this;
    }

    public EntireViewButton addBackgroundTask(Task task) {
        backgroundTasks.add(task);
        return this;
    }

    @Override
    public CriteriaPacket getCriteria() {
        //Create event criteria.
        GamepadEventCriteria<OnClickEvent, Button<Boolean>> buttonCriteria = new GamepadEventCriteria<>(onClickTasks.getValidKeys());
        GamepadEventCriteria<WhileClickEvent, Button<Boolean>> whileClickButtonCriteria = new GamepadEventCriteria<>(whileClickedTasks.getValidKeys());
        GamepadEventCriteria<OnClickReleaseEvent, Button<Boolean>> onClickReleaseEventGamepadEventCriteria = new GamepadEventCriteria<>(onClickReleasedTasks.getValidKeys());

        //Create criteria packet.
        CriteriaPacket criteriaPacket = new CriteriaPacket();
        criteriaPacket.add(buttonCriteria);
        criteriaPacket.add(whileClickButtonCriteria);
        criteriaPacket.add(onClickReleaseEventGamepadEventCriteria);

        return criteriaPacket;
    }

    @Override
    public boolean onEvent(Event eventIn) {
        if(eventIn instanceof LoopEvent) {
            Log.wtf("test", "LoopEvent");
            backgroundTasks.runTasks(new DataPacket(eventIn, this));
        }
        else if(eventIn instanceof OnClickEvent) {
            OnClickEvent event = (OnClickEvent) eventIn;
            onClickTasks.runTasks(event.getButton(), new DataPacket(event, this));
            return true;
        }
        else if(eventIn instanceof WhileClickEvent) {
            WhileClickEvent event = (WhileClickEvent) eventIn;
            whileClickedTasks.runTasks(event.getButton(), new DataPacket(event, this));
            return true;
        }
        else if(eventIn instanceof OnClickReleaseEvent) {
            OnClickReleaseEvent event = (OnClickReleaseEvent) eventIn;
            onClickReleasedTasks.runTasks(event.getButton(), new DataPacket(event, this));
            return true;
        }
        return false;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public void setText(String text) {}
}
