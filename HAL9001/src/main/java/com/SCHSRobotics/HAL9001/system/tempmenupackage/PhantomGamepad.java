package com.SCHSRobotics.HAL9001.system.tempmenupackage;

import com.SCHSRobotics.HAL9001.util.exceptions.ExceptionChecker;
import com.SCHSRobotics.HAL9001.util.misc.Button;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class PhantomGamepad {
    private Queue<OnClickEvent> events;
    private Queue<WhileClickEvent> holdEvents;
    private Queue<Double> eventTimestamps;
    private Queue<Double> holdEventTimestamps;
    private Timer timer;
    private Timer holdTimer;
    private Timer holdWaitTimer;
    public PhantomGamepad() {
        events = new LinkedBlockingQueue<>();
        holdEvents = new LinkedBlockingQueue<>();
        eventTimestamps = new LinkedBlockingQueue<>();
        holdEventTimestamps = new LinkedBlockingQueue<>();
        timer = new Timer();
        holdTimer = new Timer();
        holdWaitTimer = new Timer();
    }

    public void startTimer() {
        if(eventTimestamps.size() > 0) {
            timer.start(eventTimestamps.peek(), TimeUnit.MILLISECONDS);
        }
        if(holdEventTimestamps.size() > 0) {
            holdTimer.start(holdEventTimestamps.peek(), TimeUnit.MILLISECONDS);
            holdWaitTimer.start(300, TimeUnit.MILLISECONDS);
        }
    }

    public PhantomGamepad click(double timeMs, Button<Boolean> button) {
        events.add(new OnClickEvent(0, button));
        eventTimestamps.add(timeMs);
        return this;
    }

    public PhantomGamepad holdClick(double timeMs, Button<Boolean> button) {
        holdEvents.add(new WhileClickEvent(0, button));
        holdEventTimestamps.add(timeMs);
        return this;
    }

    public void generateEvents() {
        if(timer.requiredTimeElapsed() && !events.isEmpty()) {
            Event.injectEvent(events.poll());
            Double boxed = eventTimestamps.poll();
            ExceptionChecker.assertNonNull(boxed, new NullPointerException("this is not possible"));
            timer.start(boxed, TimeUnit.MILLISECONDS);
        }

        if(!holdTimer.requiredTimeElapsed() && !holdEvents.isEmpty() && holdWaitTimer.requiredTimeElapsed()) {
            Event.injectEvent(holdEvents.peek());
            holdWaitTimer.reset();
        }
        else if(holdTimer.requiredTimeElapsed() && !holdEvents.isEmpty()) {
            Event.injectEvent(holdEvents.poll());
            Double boxed = holdEventTimestamps.poll();
            ExceptionChecker.assertNonNull(boxed, new NullPointerException("this is not possible"));
            holdTimer.start(boxed, TimeUnit.MILLISECONDS);
        }
    }
}
