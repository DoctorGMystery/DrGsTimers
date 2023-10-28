package net.doctorg.drgstimers.client;

import net.doctorg.drgstimers.data.DateTime;
import net.doctorg.drgstimers.data.TimerData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientTimer extends TimerData {
    private final DateTime setTime;
    private final DateTime time;
    private final boolean isTimerRunning;
    private final boolean runWhileGamePaused;

    public ClientTimer(float setSeconds, int setMinuets, int setHours, float seconds, int minuets, int hours, boolean isTimerRunning, boolean runWhileGamePaused) {
        this.setTime = new DateTime(setSeconds, setMinuets, setHours);
        this.time = new DateTime(seconds, minuets, hours);
        this.isTimerRunning = isTimerRunning;
        this.runWhileGamePaused = runWhileGamePaused;
    }

    public ClientTimer(float setSeconds, int setMinuets, int setHours, DateTime time, boolean isTimerRunning, boolean runWhileGamePaused) {
        this.setTime = new DateTime(setSeconds, setMinuets, setHours);
        this.time = new DateTime(time);
        this.isTimerRunning = isTimerRunning;
        this.runWhileGamePaused = runWhileGamePaused;
    }

    public ClientTimer(DateTime setTime, float seconds, int minuets, int hours, boolean isTimerRunning, boolean runWhileGamePaused) {
        this.setTime = new DateTime(setTime);
        this.time = new DateTime(seconds, minuets, hours);
        this.isTimerRunning = isTimerRunning;
        this.runWhileGamePaused = runWhileGamePaused;
    }

    public ClientTimer(DateTime setTime, DateTime time, boolean isTimerRunning, boolean runWhileGamePaused) {
        this.setTime = new DateTime(setTime);
        this.time = new DateTime(time);
        this.isTimerRunning = isTimerRunning;
        this.runWhileGamePaused = runWhileGamePaused;
    }

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
