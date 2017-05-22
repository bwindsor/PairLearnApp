package com.github.bwindsor.pairlearnapp;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ben on 22/05/2017.
 * This is a timer which can have a task scheduled, but the time countdown until it happens can be
 * paused.
 */

public class PausableTimer {
    private Timer mTimer;
    private TimerTask mTask;
    private long mScheduleTime;
    private long mTimeOutMilliseconds;

    public PausableTimer(TimerTask task, long timeOutMilliseconds) {
        this.mTask = task;
        this.mTimeOutMilliseconds = timeOutMilliseconds;
    }

    public void pause() {
        this.mTimer.cancel();
        this.mTimeOutMilliseconds -= (System.currentTimeMillis() - this.mScheduleTime);
    }

    public void resume() {
        if (this.mTimeOutMilliseconds > 0) {
            this.mTimer = new Timer();
            this.mScheduleTime = System.currentTimeMillis();
            this.mTimer.schedule(this.mTask, this.mTimeOutMilliseconds);
        }
    }
}
