package net.doctorg.drgstimers.util;

import net.doctorg.drgstimers.data.Timer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

public class TimerSavedData extends SavedData {
    public static TimerSavedData instance;

    public TimerSavedData() {}

    public static TimerSavedData load(CompoundTag tag) {
        for (Tag t : tag.getList("Timers", 10)) {
            CompoundTag ct = (CompoundTag) t;

            Timer timer = new Timer(ct.getFloat("startTimeSeconds"), ct.getInt("startTimeMinuets"), ct.getInt("startTimeHours"));
            timer.setTime(ct.getFloat("timeSeconds"), ct.getInt("timeMinuets"), ct.getInt("timeHours"));
            timer.setRunWhileGamePaused(ct.getBoolean("runWhileGameIsPaused"));
            TimerHandler.getInstance().getTimerStack().put(ct.getString("timerIdName"), timer);
            timer.setTimerRunning(ct.getBoolean("timerRunning"));
        }

        return new TimerSavedData();
    }

    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        ListTag listTag = new ListTag();
        for (Timer timer : TimerHandler.getInstance().getTimerStack().values()) {
            listTag.add(timerToTag(timer, TimerHandler.getInstance().getKeyByValue(timer)));
        }
        pCompoundTag.put("Timers", listTag);
        setDirty(false);
        return pCompoundTag;
    }

    public CompoundTag timerToTag(Timer timer, String timerIdName) {
        CompoundTag tag = new CompoundTag();
        tag.putString("timerIdName", timerIdName);
        tag.putBoolean("timerRunning", timer.isTimerRunning());
        tag.putBoolean("runWhileGameIsPaused", timer.getRunWhileGamePaused());
        tag.putFloat("startTimeSeconds", timer.getSetTime().getSeconds());
        tag.putInt("startTimeMinuets", timer.getSetTime().getMinuets());
        tag.putInt("startTimeHours", timer.getSetTime().getHours());
        tag.putFloat("timeSeconds", timer.getTime().getSeconds());
        tag.putInt("timeMinuets", timer.getTime().getMinuets());
        tag.putInt("timeHours", timer.getTime().getHours());
        return tag;
    }
}
