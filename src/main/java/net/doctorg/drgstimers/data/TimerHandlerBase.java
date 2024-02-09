package net.doctorg.drgstimers.data;

import net.doctorg.drgstimers.util.TimerNotInListException;

import java.util.HashMap;
import java.util.UUID;

public abstract class TimerHandlerBase<T extends TimerData> {
    protected HashMap<String, T> timerStack = new HashMap<>();
    protected final HashMap<String, T> runningTimers = new HashMap<>();

    protected UUID levelId = new UUID(0, 0);

    public T getTimer(String idName) throws TimerNotInListException {
        if (timerStack.containsKey(idName)) {
            return timerStack.get(idName);
        }
        throw new TimerNotInListException("This timer does not exist!");
    }

    public T getRunningTimer(String idName) throws TimerNotInListException {
        if (runningTimers.containsKey(idName)) {
            return runningTimers.get(idName);
        }
        throw new TimerNotInListException("This timer does not exist!");
    }

    public HashMap<String, T> getTimerStack() {
        return timerStack;
    }
    public HashMap<String, T> getRunningTimers() {
        return runningTimers;
    }

    public UUID getLevelId() {
        return levelId;
    }

    public void setLevelId(UUID levelId) {
        this.levelId = levelId;
    }
}
