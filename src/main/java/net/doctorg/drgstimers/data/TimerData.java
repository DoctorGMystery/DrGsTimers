package net.doctorg.drgstimers.data;

public abstract class TimerData {

    protected DateTime setTime;
    protected DateTime time;
    protected boolean isTimerRunning;
    protected boolean runWhileGamePaused;

    public DateTime getTime() {
        return new DateTime(this.time);
    }

    public DateTime getSetTime() {
        return new DateTime(this.setTime);
    }

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public boolean getRunWhileGamePaused() {
        return this.runWhileGamePaused;
    }
}
