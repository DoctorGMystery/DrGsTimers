package net.doctorg.drgstimers.data;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class TimerData {

    protected DateTime setTime;
    protected DateTime time;
    protected boolean isTimerRunning;
    protected boolean runWhileGamePaused;
    protected boolean visible = true;
    protected boolean alwaysVisible = false;

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

    @OnlyIn(Dist.CLIENT)
    public boolean isVisible() { return visible; }

    @OnlyIn(Dist.CLIENT)
    public void setVisible(boolean visible) { this.visible = visible; }

    @OnlyIn(Dist.CLIENT)
    public boolean isAlwaysVisible() { return alwaysVisible; }

    @OnlyIn(Dist.CLIENT)
    public void setAlwaysVisible(boolean alwaysVisible) { this.alwaysVisible = alwaysVisible; }
}
