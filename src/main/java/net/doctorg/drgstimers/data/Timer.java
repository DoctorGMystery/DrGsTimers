package net.doctorg.drgstimers.data;

import net.doctorg.drgstimers.util.TimerHandler;

public class Timer extends TimerData {
    private boolean shouldRefreshTimerRunning = false;

    public Timer(DateTime time) {
        this.setTime = new DateTime(time);
        this.time = new DateTime(setTime);
    }

    public Timer(float seconds, int minutes, int hours) {
        this.setTime = new DateTime(seconds, minutes, hours);
        this.time = new DateTime(setTime);
    }

    public void setTime(float seconds, int minutes, int hours) {
        this.time = new DateTime(seconds, minutes, hours);
    }

    public void setSetTime(DateTime setTime, boolean resetTimer) {
        this.setTime = new DateTime(setTime);
        if (resetTimer) {
            time = new DateTime(this.setTime);
        }
    }

    public void setTimerRunning(boolean timerRunning) {
        this.isTimerRunning = timerRunning;
        if (timerRunning) {
            TimerHandler.getInstance().getRunningTimers().put(TimerHandler.getInstance().getKeyByValue(this), this);
        } else {
            TimerHandler.getInstance().getRunningTimers().remove(TimerHandler.getInstance().getKeyByValue(this));
        }
        shouldRefreshTimerRunning = false;
    }

    public boolean getShouldRefreshTimerRunning() {
        return this.shouldRefreshTimerRunning;
    }

    public void setRunWhileGamePaused(boolean runWhileGamePaused) {
        this.runWhileGamePaused = runWhileGamePaused;
    }

    public void resetTime(boolean pause) {
        this.time = new DateTime(this.setTime);
        if (pause) {
            setTimerRunning(false);
        }
    }

    public void updateTime(float updateTimeInSec) {
        if (!time.smallerOrEquals(0, 0, 0)) {
            this.time.reduce(updateTimeInSec);
            return;
        }
        shouldRefreshTimerRunning = true;
    }
}
