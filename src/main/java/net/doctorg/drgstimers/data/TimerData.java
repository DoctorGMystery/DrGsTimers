package net.doctorg.drgstimers.data;

public abstract class TimerData {

    protected DateTime setTime;
    protected DateTime time;
    protected boolean isTimerRunning;
    protected boolean runWhileGamePaused;
    protected boolean visible = true;

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

    /**
     * @return always 'true' on server side
     */
    public boolean isVisible() { return visible; }

    /**
     * Note: Has no impact on server side
     */
    public void setVisible(boolean visible) { this.visible = visible; }
}
